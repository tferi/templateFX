package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, Unsettable }

import scala.collection.mutable.ListBuffer

sealed abstract class Change extends Product with Serializable {
  def execute(): Unit
}

final case class RemoveNode(container: TFXParent, node: Node) extends Change {
  override def execute(): Unit = container.getChildren.remove(node)
}

final case class RemoveNodes(container: TFXParent, nodes: Seq[Node]) extends Change {
  override def execute(): Unit = container.getChildren.removeAll(nodes: _*)
}

final case class RemoveSeq(container: TFXParent, fromInclusive: Int, toExclusive: Int) extends Change {
  override def execute(): Unit = container.getChildren.remove(fromInclusive, toExclusive)
}

final case class Insert[FXType <: Node](container: TFXParent, definition: Spec[FXType], position: Int) extends Change {
  override def execute(): Unit = container.getChildren.add(position, definition.materialize())
}

final case class InsertWithKey[FXType <: Node, Key](container: TFXParent, definition: Spec[FXType], position: Int, key: Key) extends Change {
  override def execute(): Unit = container.getChildren.add(position, IdentifiedSpecs.setKeyOnNode(key, definition.materialize()))
}

final case class Replace[FXType <: Node](container: TFXParent, definition: Spec[FXType], position: Int) extends Change {
  override def execute(): Unit = container.getChildren.set(position, definition.materialize())
}

final case class Move[FXType <: Node](container: TFXParent, node: Node, targetPosition: Int) extends Change {
  override def execute(): Unit = {
    val currentPosition = container.getChildren.indexOf(node)
    if (currentPosition != targetPosition) {
      container.getChildren.remove(node)
      container.getChildren.add(targetPosition, node)
    }
  }
}

final case class Mutate[Item <: Node, Attr](item: Item, attribute: Attribute[Item, Attr], value: Attr) extends Change {
  override def execute(): Unit = {

    attribute.set(item, value)

    ManagedAttributes.get(item) match {
      case Some(managedAttributes) =>
        if (!managedAttributes.contains(attribute)) {
          managedAttributes.prepend(attribute)
        }
      case _ =>
        val managedAttributes: ListBuffer[Unsettable[_]] = new ListBuffer[Unsettable[_]]
        managedAttributes.prepend(attribute)
        ManagedAttributes.set(item, managedAttributes)
    }
  }
}

final case class UnsetAttributes[Item <: Node](item: Item, attributesToUnset: Seq[Unsettable[Item]]) extends Change {
  override def execute(): Unit = {
    val currentlySetAttributes = ManagedAttributes.get(item)
    attributesToUnset.foreach { attribute =>
      attribute.unset(item)
      currentlySetAttributes.foreach(attributes => attributes -= attribute)
    }
  }
}
