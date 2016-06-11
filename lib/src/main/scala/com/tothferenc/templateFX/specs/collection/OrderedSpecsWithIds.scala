package com.tothferenc.templateFX.specs.collection

import java.util.{ List => JList }

import com.tothferenc.templateFX.InsertWithKey
import com.tothferenc.templateFX.MoveNode
import com.tothferenc.templateFX.RemoveNodes
import com.tothferenc.templateFX.Replace
import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.collection.convert.wrapAsScala._
import scala.collection.convert.wrapAsJava._
import scala.collection.mutable

final case class OrderedSpecsWithIds[Key, Item](specsWithKeys: List[(Key, Template[Item])])(implicit userDataAccess: UserDataAccess[Item]) extends CollectionSpec[Item] {

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

  override def build(): JList[Item] = specsWithKeys.map {
    case (key, spec) => SpecsWithKeys.setKeyOnItem(key, spec.build())
  }
}
