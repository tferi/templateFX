package com.tothferenc.templateFX.collection

import java.util.{List => JList}

import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.change.Change
import com.tothferenc.templateFX.change.InsertWithKey
import com.tothferenc.templateFX.change.RemoveNodes
import com.tothferenc.templateFX.change.Replace

import scala.collection.convert.wrapAsJava._
import scala.collection.convert.wrapAsScala._
import scala.collection.mutable
import scala.reflect.ClassTag

final case class SpecsWithIds[Key: ClassTag, Item](specs: List[(Key, Template[Item])]) extends CollectionSpec[Item] {

  override def requiredChangesIn(collection: JList[Item]): List[Change] = {
    val existingNodesByKey = collection.groupBy(SpecsWithKeys.getItemKey[Key])
    val specKeySet = specs.map(_._1)
    val removals = for {
      (key, nodes) <- existingNodesByKey if key.isEmpty || !specKeySet.contains(key.get)
      node <- nodes
    } yield node

    val mutationsInsertions: List[Change] = specs.flatMap {
      case (key, spec) => existingNodesByKey.get(Some(key)) match {
        case Some(mutable.Buffer(node)) =>
          spec.reconciliationSteps(node).getOrElse(List(Replace(collection, spec, collection.indexOf(node))))
        case _ =>
          List(InsertWithKey(collection, spec, 0, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(collection, removals))) ::: mutationsInsertions
  }

  override def build(): JList[Item] = specs.map {
    case (key, spec) => SpecsWithKeys.setKeyOnItem(key, spec.build())
  }
}
