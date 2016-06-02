package com.tothferenc.templateFX

import java.util

import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.userdata.UserData
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.annotation.tailrec
import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

abstract class CollectionSpec[-Container, +Item] extends Template[List[Item]] {
  def reconcilationSteps(other: Any): Option[List[Change]] = None
  def requiredChangesIn(container: Container): List[Change]
  def build(): List[Item]
  def reconcile(container: Container): Unit = requiredChangesIn(container).foreach(_.execute())
}

abstract class CollectionAccess[-Container, Item] {
  def getCollection(container: Container): java.util.List[Item]
}

case object Ignore extends CollectionSpec[Any, Nothing] {
  override def requiredChangesIn(container: Any): List[Change] = Nil

  override def build(): List[Nothing] = Nil
}

final case class SpecsWithIds[Key, Container, Item](specs: List[(Key, Template[Item])])(implicit collectionAccess: CollectionAccess[Container, Item], userDataAccess: UserDataAccess[Item]) extends CollectionSpec[Container, Item] {

  override def requiredChangesIn(container: Container): List[Change] = {
    val existingChildren: java.util.List[Item] = collectionAccess.getCollection(container)
    val existingNodesByKey = existingChildren.groupBy { item =>
      userDataAccess.get(item).orNull
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
          spec.reconcilationSteps(node).getOrElse(List(Replace(existingChildren, spec, existingChildren.indexOf(node))))
        case _ =>
          List(InsertWithKey(existingChildren, spec, 0, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(existingChildren, removals.toSeq))) ::: mutationsInsertions
  }

  override def build(): List[Item] = specs.map {
    case (key, spec) => SpecsWithKeys.setKeyOnItem(key, spec.build())
  }
}

object SpecsWithKeys {
  val TFX_KEY = "tfx_key"

  private[templateFX] def setKeyOnItem[Item, Key](key: Key, item: Item)(implicit userDataAccess: UserDataAccess[Item]): Item = {
    UserData.set(item, TFX_KEY, key)
    item
  }
}

final case class OrderedSpecsWithIds[Key, Container, Item](specsWithKeys: List[(Key, Template[Item])])(implicit collectionAccess: CollectionAccess[Container, Item], userDataAccess: UserDataAccess[Item]) extends CollectionSpec[Container, Item] {

  override def requiredChangesIn(container: Container): List[Change] = {
    val existingChildren: java.util.List[Item] = collectionAccess.getCollection(container)
    val existingNodesByKey = existingChildren.groupBy { item =>
      userDataAccess.get(item).orNull
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
          spec.reconcilationSteps(node).map(MoveNode(existingChildren, node, desiredPosition) :: _)
            .getOrElse(List(Replace(existingChildren, spec, existingChildren.indexOf(node))))
        case _ =>
          List(InsertWithKey(existingChildren, spec, desiredPosition, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(existingChildren, removals.toSeq))) ::: mutationsMovesInsertions
  }

  override def build(): List[Item] = specsWithKeys.map {
    case (key, spec) => SpecsWithKeys.setKeyOnItem(key, spec.build())
  }
}

final case class OrderedSpecs[Container, Item](specs: List[Template[Item]])(implicit collectionAccess: CollectionAccess[Container, Item], userDataAccess: UserDataAccess[Item]) extends CollectionSpec[Container, Item] {

  override def build(): List[Item] = specs.map(_.build())

  private def reconcileInHierarchy(container: Container, position: Int, nodeO: Option[Item], spec: Template[Item]): List[Change] = {

    val collection: util.List[Item] = collectionAccess.getCollection(container)
    nodeO match {
      case Some(node) =>
        spec.reconcilationSteps(node).getOrElse(List(Replace(collection, spec, position)))

      case None =>
        List(Insert(collection, spec, position))
    }
  }

  override def requiredChangesIn(container: Container): List[Change] = {
    val childrenOnSceneGraph = collectionAccess.getCollection(container)
    val numChildrenOnSceneGraph: Int = childrenOnSceneGraph.size()
    val numChildrenSpecs: Int = specs.length

    @tailrec def reconcile(i: Int, acc: List[Change]): List[Change] =
      if (i < numChildrenSpecs)
        reconcile(i + 1, acc ::: reconcileInHierarchy(container, i, childrenOnSceneGraph.lift(i), specs(i)))
      else
        acc

    if (numChildrenOnSceneGraph > numChildrenSpecs)
      RemoveSeq(childrenOnSceneGraph, numChildrenSpecs, numChildrenOnSceneGraph) :: reconcile(0, Nil)
    else
      reconcile(0, Nil)
  }
}