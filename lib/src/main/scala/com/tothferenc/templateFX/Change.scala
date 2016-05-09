package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, RemovableFeature }
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

object Change {
  lazy val logger = Logger(LoggerFactory.getLogger("CHANGELOG"))

  val debug: Boolean = java.lang.Boolean.getBoolean("tfx-debug")
}

sealed abstract class Change extends Product with Serializable {

  def execute(): Unit = {
    if (Change.debug) Change.logger.debug(this.toString)
    exec()
  }

  protected def exec(): Unit
}

final case class RemoveNode(container: TFXParent, node: Node) extends Change {
  override protected def exec(): Unit = container.getChildren.remove(node)
}

final case class RemoveNodes(container: TFXParent, nodes: Seq[Node]) extends Change {
  override protected def exec(): Unit = container.getChildren.removeAll(nodes: _*)
}

final case class RemoveSeq(container: TFXParent, fromInclusive: Int, toExclusive: Int) extends Change {
  override protected def exec(): Unit = container.getChildren.remove(fromInclusive, toExclusive)
}

final case class Insert[FXType <: Node](container: TFXParent, definition: Definition[FXType], position: Int) extends Change {
  override protected def exec(): Unit = container.getChildren.add(position, definition.materialize())
}

final case class InsertWithKey[FXType <: Node, Key](container: TFXParent, definition: Definition[FXType], position: Int, key: Key) extends Change {
  override protected def exec(): Unit = container.getChildren.add(position, SpecsWithKeys.setKeyOnNode(key, definition.materialize()))
}

final case class Replace[FXType <: Node](container: TFXParent, definition: Definition[FXType], position: Int) extends Change {
  override protected def exec(): Unit = container.getChildren.set(position, definition.materialize())
}

final case class Move[FXType <: Node](container: TFXParent, node: Node, targetPosition: Int) extends Change {
  override protected def exec(): Unit = {
    val currentPosition = container.getChildren.indexOf(node)
    if (currentPosition != targetPosition) {
      container.getChildren.remove(node)
      container.getChildren.add(targetPosition, node)
    }
  }
}

final case class Setting[Item <: Node](item: Item, setters: Seq[FeatureSetter[Item]]) extends Change {
  override protected def exec(): Unit = {
    val managedAttributes = ManagedAttributes.get(item).getOrElse {
      val buffer: ListBuffer[RemovableFeature[_]] = new ListBuffer[RemovableFeature[_]]
      ManagedAttributes.set(item, buffer)
      buffer
    }

    setters.foreach { setter =>
      setter.set(item)
      if (!managedAttributes.contains(setter.feature)) {
        managedAttributes.prepend(setter.feature)
      }
    }

  }
}

final case class UnsetAttributes[Item <: Node](item: Item, attributesToUnset: Seq[RemovableFeature[Item]]) extends Change {
  override protected def exec(): Unit = {
    val currentlySetAttributes = ManagedAttributes.get(item)
    attributesToUnset.foreach { attribute =>
      attribute.remove(item)
      currentlySetAttributes.foreach(attributes => attributes -= attribute)
    }
  }
}
