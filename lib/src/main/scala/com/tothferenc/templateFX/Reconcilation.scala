package com.tothferenc.templateFX

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.Pane

import scala.annotation.tailrec
import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

abstract class ChildrenSpecification {
  def changes(container: Pane): List[Change]
  def materializeAll(): List[Node]
  def reconcile(container: TFXParent): Unit = changes(container).foreach(_.execute())
}

case object Ignore extends ChildrenSpecification {
  override def changes(container: TFXParent): List[Change] = Nil

  override def materializeAll(): List[Node] = Nil
}

object IdentifiedSpecs {
  private val TFX_KEY = "tfx_key"

  private[templateFX] def setKeyOnNode[Key](key: Key, node: Node): Node = {
    val keySetting: (String, Key) = TFX_KEY -> key
    if (node.getUserData == null) {
      node.setUserData(new mutable.ListMap[String, Any]() += keySetting)
    } else {
      node.getUserData match {
        case mmap: mutable.Map[String, Any] => mmap += keySetting
      }
    }
    node
  }
}

final case class IdentifiedSpecs[Key](specsWithKeys: List[(Key, NodeSpec)]) extends ChildrenSpecification {

  override def changes(container: TFXParent): List[Change] = {
    val existingChildren: ObservableList[Node] = container.getChildren
    val existingNodesByKey = existingChildren.groupBy { node =>
      node.getUserData.asInstanceOf[mutable.Map[String, Any]]
        .get(IdentifiedSpecs.TFX_KEY)
        .flatMap {
          case k: Key => Some(k)
          case _ => None
        }
    }
    val removals = existingNodesByKey.get(None).map(_.map(Remove(container, _)).toList).getOrElse(Nil)
    val mutationsMovesInsertions = specsWithKeys.zipWithIndex.flatMap {
      case ((key, spec), desiredPosition) => existingNodesByKey.get(Some(key)) match {
        case Some(mutable.Buffer(node)) =>
          Move(container, node, desiredPosition) :: spec.reconcileWithNode(container, existingChildren.indexOf(node), node)
        case _ =>
          List(InsertWithKey(container, spec, desiredPosition, key))
      }
    }
    removals ::: mutationsMovesInsertions
  }

  override def materializeAll(): List[Node] = specsWithKeys.map {
    case (key, spec) => IdentifiedSpecs.setKeyOnNode(key, spec.materialize())
  }
}

final case class SequentialSpecs(specs: List[NodeSpec]) extends ChildrenSpecification {

  override def materializeAll(): List[Node] = specs.map(_.materialize())

  private def reconcileInHierarchy(container: TFXParent, position: Int, nodeO: Option[Node], specO: Option[Spec[_ <: Node]]): List[Change] = (nodeO, specO) match {
    case (Some(node), Some(spec)) =>
      spec.reconcileWithNode(container, position, node)

    case (Some(redundant), None) =>
      List(Remove(container, redundant))

    case (None, Some(missing)) =>
      List(Insert(container, missing, position))

    case _ =>
      Nil
  }

  override def changes(container: Pane): List[Change] = {
    val childrenOnSceneGraph = container.getChildren
    val numChildrenOnSceneGraph: Int = childrenOnSceneGraph.size()
    val numChildrenSpecs: Int = specs.length
    val longest = if (numChildrenOnSceneGraph > numChildrenSpecs) numChildrenOnSceneGraph else numChildrenSpecs

    @tailrec def reconcile(i: Int, acc: List[Change]): List[Change] =
      if (i < longest)
        reconcile(i + 1, acc ::: reconcileInHierarchy(container, i, childrenOnSceneGraph.lift(i), specs.lift(i)))
      else
        acc

    reconcile(0, Nil)
  }
}