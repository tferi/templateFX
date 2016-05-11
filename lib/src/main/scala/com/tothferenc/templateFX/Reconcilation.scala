package com.tothferenc.templateFX

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.specs.Spec

import scala.annotation.tailrec
import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

abstract class ChildrenSpec {
  def requiredChangesIn(container: Pane): List[Change]
  def materializeAll(): List[Node]
  def reconcile(container: TFXParent): Unit = requiredChangesIn(container).foreach(_.execute())
}

case object Ignore extends ChildrenSpec {
  override def requiredChangesIn(container: TFXParent): List[Change] = Nil

  override def materializeAll(): List[Node] = Nil
}

final case class SpecsWithIds[Key](specs: List[(Key, NodeSpec)]) extends ChildrenSpec {
  override def requiredChangesIn(container: TFXParent): List[Change] = {
    val existingChildren: ObservableList[Node] = container.getChildren
    val existingNodesByKey = existingChildren.groupBy { node =>
      node.getUserData.asInstanceOf[mutable.Map[String, Any]]
        .get(SpecsWithKeys.TFX_KEY)
        .map(_.asInstanceOf[Key])
    }
    val specKeySet = specs.map(_._1)
    val removals = for {
      (key, nodes) <- existingNodesByKey if key.isEmpty || !specKeySet.contains(key.get)
      node <- nodes
    } yield node

    val mutationsInsertions: List[Change] = specs.flatMap {
      case (key, spec) => existingNodesByKey.get(Some(key)) match {
        case Some(mutable.Buffer(node)) =>
          spec.reconcileWithNode(container, existingChildren.indexOf(node), node)
        case _ =>
          List(InsertWithKey(container, spec, 0, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(container, removals.toSeq))) ::: mutationsInsertions
  }

  override def materializeAll(): List[Node] = specs.map {
    case (key, spec) => SpecsWithKeys.setKeyOnNode(key, spec.materialize())
  }.toList
}

object SpecsWithKeys {
  val TFX_KEY = "tfx_key"

  private[templateFX] def setKeyOnNode[Key](key: Key, node: Node): Node = {
    UserData.set(node, TFX_KEY, key)
    node
  }
}

final case class OrderedSpecsWithIds[Key](specsWithKeys: List[(Key, NodeSpec)]) extends ChildrenSpec {

  override def requiredChangesIn(container: TFXParent): List[Change] = {
    val existingChildren: ObservableList[Node] = container.getChildren
    val existingNodesByKey = existingChildren.groupBy { node =>
      node.getUserData.asInstanceOf[mutable.Map[String, Any]]
        .get(SpecsWithKeys.TFX_KEY)
        .map(_.asInstanceOf[Key])
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
    case (key, spec) => SpecsWithKeys.setKeyOnNode(key, spec.materialize())
  }
}

final case class OrderedSpecs(specs: List[NodeSpec]) extends ChildrenSpec {

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