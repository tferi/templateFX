package com.tothferenc.templateFX

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.Pane

import scala.annotation.tailrec
import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

abstract class ChildrenSpecification {
  def requiredChangesIn(container: Pane): List[Change]
  def materializeAll(): List[Node]
  def reconcile(container: TFXParent): Unit = requiredChangesIn(container).foreach(_.execute())
}

case object Ignore extends ChildrenSpecification {
  override def requiredChangesIn(container: TFXParent): List[Change] = Nil

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

  override def requiredChangesIn(container: TFXParent): List[Change] = {
    val existingChildren: ObservableList[Node] = container.getChildren
    val existingNodesByKey = existingChildren.groupBy { node =>
      node.getUserData.asInstanceOf[mutable.Map[String, Any]]
        .get(IdentifiedSpecs.TFX_KEY)
        .flatMap {
          case k: Key => Some(k)
          case _ => None
        }
    }
    val specKeySet = specsWithKeys.map(_._1).toSet
    val removals = for {
      (key, nodes) <- existingNodesByKey if key.isEmpty || !specKeySet.contains(key.get)
      node <- nodes
    } yield node

    val mutationsMovesInsertions = specsWithKeys.zipWithIndex.flatMap {
      case ((key, spec), desiredPosition) => existingNodesByKey.get(Some(key)) match {
        case Some(mutable.Buffer(node)) =>
          Move(container, node, desiredPosition) :: spec.reconcileWithNode(container, existingChildren.indexOf(node), node)
        case _ =>
          List(InsertWithKey(container, spec, desiredPosition, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(container, removals.toSeq))) ::: mutationsMovesInsertions
  }

  override def materializeAll(): List[Node] = specsWithKeys.map {
    case (key, spec) => IdentifiedSpecs.setKeyOnNode(key, spec.materialize())
  }
}

final case class SequentialSpecs(specs: List[NodeSpec]) extends ChildrenSpecification {

  override def materializeAll(): List[Node] = specs.map(_.materialize())

  private def reconcileInHierarchy(container: TFXParent, position: Int, nodeO: Option[Node], spec: Spec[_ <: Node]): List[Change] = nodeO match {
    case Some(node) =>
      spec.reconcileWithNode(container, position, node)

    case None =>
      List(Insert(container, spec, position))
  }

  override def requiredChangesIn(container: Pane): List[Change] = {
    val childrenOnSceneGraph = container.getChildren
    val numChildrenOnSceneGraph: Int = childrenOnSceneGraph.size()
    val numChildrenSpecs: Int = specs.length

    @tailrec def reconcile(i: Int, acc: List[Change]): List[Change] =
      if (i < numChildrenSpecs)
        reconcile(i + 1, acc ::: reconcileInHierarchy(container, i, childrenOnSceneGraph.lift(i), specs(i)))
      else
        acc

    if (numChildrenOnSceneGraph > numChildrenSpecs)
      RemoveSeq(container, numChildrenSpecs, numChildrenOnSceneGraph) :: reconcile(0, Nil)
    else
      reconcile(0, Nil)
  }
}