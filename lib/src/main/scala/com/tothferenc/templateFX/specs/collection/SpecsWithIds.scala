package com.tothferenc.templateFX.specs.collection

import java.util.{ List => JList }

import com.tothferenc.templateFX.InsertWithKey
import com.tothferenc.templateFX.RemoveNodes
import com.tothferenc.templateFX.Replace
import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.collection.convert.wrapAsScala._
import scala.collection.convert.wrapAsJava._
import scala.collection.mutable

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

  override def build(): JList[Item] = specs.map {
    case (key, spec) => SpecsWithKeys.setKeyOnItem(key, spec.build())
  }
}
