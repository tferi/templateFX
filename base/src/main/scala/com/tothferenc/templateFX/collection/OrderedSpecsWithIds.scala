package com.tothferenc.templateFX.collection

import java.util
import java.util.{List => JList}

import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.change.Change
import com.tothferenc.templateFX.change.InsertWithKey
import com.tothferenc.templateFX.change.MoveNode
import com.tothferenc.templateFX.change.RemoveNodes
import com.tothferenc.templateFX.change.Replace
import com.tothferenc.templateFX.errors.DuplicateKeyException

import scala.collection.convert.wrapAsScala._
import scala.collection.mutable
import scala.collection.immutable
import scala.collection.mutable
import scala.reflect.ClassTag

final case class OrderedSpecsWithIds[Key: ClassTag, Item](specsWithKeys: immutable.Seq[(Key, Template[Item])]) extends CollectionSpec[Item] {

  override def requiredChangesIn(collection: JList[Item]): List[Change] = {
    val existingNodes = new mutable.HashMap[Option[Key], mutable.Buffer[(Item, Int)]]()
    collection.zipWithIndex.foreach { pair =>
        val key = SpecsWithKeys.getItemKey[Key](pair._1)
        val buffer = existingNodes.getOrElseUpdate(key, new mutable.ArrayBuffer[(Item, Int)]())
        buffer += pair
    }
    val existingNodesByKey = existingNodes.toMap
    val specKeySet = {
      val set = new mutable.HashSet[Key]()
      specsWithKeys.foreach(pair => set.add(pair._1))
      set
    }
    val removals = for {
      (key, redundantNodes) <- existingNodesByKey if key.isEmpty || !specKeySet.contains(key.get)
      pair <- redundantNodes
    } yield pair._1

    val mutationsMovesInsertions = specsWithKeys.zipWithIndex.flatMap {
      case ((key, spec), desiredPosition) => existingNodesByKey.get(Some(key)) match {
        case Some(mutable.Buffer((node, currentPosition))) =>
          spec.reconciliationSteps(node).map(List(MoveNode(collection, currentPosition, desiredPosition)) ++ _)
            .getOrElse(List(Replace(collection, spec, collection.indexOf(node))))
        case Some(buffer) if buffer.lengthCompare(1) > 0 =>
          throw DuplicateKeyException(key.toString)
        case _ =>
          List(InsertWithKey(collection, spec, desiredPosition, key))
      }
    }
    (if (removals.isEmpty) Nil else List(RemoveNodes(collection, removals))) ++ mutationsMovesInsertions
  }

  override def build(): JList[Item] = {
    val buffer = new util.ArrayList[Item]()
    specsWithKeys.foreach {
      case (key, spec) => buffer.add(SpecsWithKeys.setKeyOnItem(key, spec.build()))
    }
    buffer
  }
}
