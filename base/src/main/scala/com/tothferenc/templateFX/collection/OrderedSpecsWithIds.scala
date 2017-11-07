package com.tothferenc.templateFX.collection

import java.util
import java.util.{List => JList}

import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.change.Change
import com.tothferenc.templateFX.change.InsertWithKey
import com.tothferenc.templateFX.change.MoveNode
import com.tothferenc.templateFX.change.RemoveNodes
import com.tothferenc.templateFX.change.Replace

import scala.collection.convert.wrapAsScala._
import scala.collection.mutable
import scala.reflect.ClassTag

final case class OrderedSpecsWithIds[Key: ClassTag, Item](specsWithKeys: List[(Key, Template[Item])]) extends CollectionSpec[Item] {

  override def requiredChangesIn(collection: JList[Item]): List[Change] = {
    val existingNodesByKey = collection.groupBy(SpecsWithKeys.getItemKey[Key])
    val specKeySet = {
      val set = new mutable.HashSet[Key]()
      specsWithKeys.foreach(pair => set.add(pair._1))
      set
    }
    val removals = for {
      (key, nodes) <- existingNodesByKey if key.isEmpty || !specKeySet.contains(key.get)
      node <- nodes
    } yield node

    val mutationsMovesInsertions = specsWithKeys.zipWithIndex.flatMap {
      case ((key, spec), desiredPosition) => existingNodesByKey.get(Some(key)) match {
        case Some(mutable.Buffer(node)) =>
          spec.reconciliationSteps(node).map(List(MoveNode(collection, node, desiredPosition)) ++ _)
            .getOrElse(List(Replace(collection, spec, collection.indexOf(node))))
        case Some(buffer) if buffer.lengthCompare(1) > 0 =>
          throw new Exception("Multiple elements in sequence with the same key")
        case _ =>
          List(InsertWithKey(collection, spec, desiredPosition, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(collection, removals))) ::: mutationsMovesInsertions
  }

  override def build(): JList[Item] = {
    val buffer = new util.ArrayList[Item]()
    specsWithKeys.foreach {
      case (key, spec) => buffer.add(SpecsWithKeys.setKeyOnItem(key, spec.build()))
    }
    buffer
  }
}
