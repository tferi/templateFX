package com.tothferenc.templateFX

import java.util.{ List => JList }

import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.userdata.UserData
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.annotation.tailrec
import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

abstract class CollectionSpec[-Container, Item] extends Template[List[Item]] {

  def reconcilationSteps(other: Any): Option[List[Change]] = {
    other match {
      case expected: JList[Item] @unchecked =>
        Some(requiredChangesIn(expected))
      case _ =>
        None
    }
  }
  def requiredChangesIn(collection: JList[Item]): List[Change]
  def build(): List[Item]
  def reconcile(collection: JList[Item]): Unit = requiredChangesIn(collection).foreach(_.execute())
}

abstract class CollectionAccess[-Container, Item] {
  def getCollection(container: Container): java.util.List[Item]
}

final case class SpecsWithIds[Key, Container, Item](specs: List[(Key, Template[Item])])(implicit collectionAccess: CollectionAccess[Container, Item], userDataAccess: UserDataAccess[Item]) extends CollectionSpec[Container, Item] {

  override def requiredChangesIn(collection: JList[Item]): List[Change] = {
    val existingNodesByKey = collection.groupBy { item =>
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
          spec.reconcilationSteps(node).getOrElse(List(Replace(collection, spec, collection.indexOf(node))))
        case _ =>
          List(InsertWithKey(collection, spec, 0, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(collection, removals.toSeq))) ::: mutationsInsertions
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

  override def requiredChangesIn(collection: JList[Item]): List[Change] = {
    val existingNodesByKey = collection.groupBy { item =>
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
          spec.reconcilationSteps(node).map(MoveNode(collection, node, desiredPosition) :: _)
            .getOrElse(List(Replace(collection, spec, collection.indexOf(node))))
        case _ =>
          List(InsertWithKey(collection, spec, desiredPosition, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(collection, removals.toSeq))) ::: mutationsMovesInsertions
  }

  override def build(): List[Item] = specsWithKeys.map {
    case (key, spec) => SpecsWithKeys.setKeyOnItem(key, spec.build())
  }
}

final case class OrderedSpecs[Container, Item](specs: List[Template[Item]])(implicit collectionAccess: CollectionAccess[Container, Item], userDataAccess: UserDataAccess[Item]) extends CollectionSpec[Container, Item] {

  override def build(): List[Item] = specs.map(_.build())

  private def reconcileInHierarchy(collection: JList[Item], position: Int, nodeO: Option[Item], spec: Template[Item]): List[Change] = {
    nodeO match {
      case Some(node) =>
        spec.reconcilationSteps(node).getOrElse(List(Replace(collection, spec, position)))

      case None =>
        List(Insert(collection, spec, position))
    }
  }

  override def requiredChangesIn(collection: JList[Item]): List[Change] = {
    val numChildrenOnSceneGraph: Int = collection.size()
    val numChildrenSpecs: Int = specs.length

    @tailrec def reconcile(i: Int, acc: List[Change]): List[Change] =
      if (i < numChildrenSpecs)
        reconcile(i + 1, acc ::: reconcileInHierarchy(collection, i, collection.lift(i), specs(i)))
      else
        acc

    if (numChildrenOnSceneGraph > numChildrenSpecs)
      RemoveSeq(collection, numChildrenSpecs, numChildrenOnSceneGraph) :: reconcile(0, Nil)
    else
      reconcile(0, Nil)
  }
}