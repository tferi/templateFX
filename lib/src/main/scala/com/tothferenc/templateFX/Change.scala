package com.tothferenc.templateFX

import javafx.scene.Node

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Change {
  val logger = Logger(LoggerFactory.getLogger("CHANGELOG"))
}

sealed abstract class Change extends Product with Serializable {

  def execute(): Unit = {
    Change.logger.debug(this.toString)
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

final case class Insert[FXType <: Node](container: TFXParent, definition: Spec[FXType], position: Int) extends Change {
  override protected def exec(): Unit = container.getChildren.add(position, definition.materialize())
}

final case class InsertWithKey[FXType <: Node, Key](container: TFXParent, definition: Spec[FXType], position: Int, key: Key) extends Change {
  override protected def exec(): Unit = container.getChildren.add(position, IdentifiedSpecs.setKeyOnNode(key, definition.materialize()))
}

final case class Replace[FXType <: Node](container: TFXParent, definition: Spec[FXType], position: Int) extends Change {
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

final case class Mutate[Item, Attr](item: Item, attribute: Attribute[Item, Attr], value: Attr) extends Change {
  override protected def exec(): Unit = attribute.set(item, value)
}
