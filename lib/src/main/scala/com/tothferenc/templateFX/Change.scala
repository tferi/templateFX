package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.RemovableFeature
import com.tothferenc.templateFX.specs.NodeFixture
import com.tothferenc.templateFX.specs.Spec
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.collection.mutable

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

final case class SetFixture[Container, NodeType <: Node](container: Container, fixture: NodeFixture[Container], spec: Option[Spec[NodeType]]) extends Change {
  override protected def exec(): Unit = fixture.set(container, spec.map(_.materialize()).orNull)
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
  override protected def exec(): Unit = container.getChildren.add(position, SpecsWithKeys.setKeyOnNode(key, definition.materialize()))
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

final case class Mutation[Item <: Node](item: Item, featureSetters: Seq[FeatureSetter[Item]], featuresToRemove: Iterable[RemovableFeature[Item]]) extends Change {
  override protected def exec(): Unit = {
    val managedAttributes = ManagedAttributes.get(item).getOrElse {
      val set: scala.collection.mutable.Set[RemovableFeature[_]] = new mutable.HashSet[RemovableFeature[_]]
      ManagedAttributes.set(item, set)
      set
    }

    featuresToRemove.foreach { attribute =>
      attribute.remove(item)
      managedAttributes -= attribute
    }

    featureSetters.foreach { setter =>
      setter.set(item)
      managedAttributes += setter.feature
    }
  }
}
