package com.tothferenc.templateFX.collection

import java.util.{ List => JList }

import com.tothferenc.templateFX.Insert
import com.tothferenc.templateFX.RemoveSeq
import com.tothferenc.templateFX.Replace
import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.Template

import scala.annotation.tailrec
import scala.collection.convert.wrapAsJava._
import scala.collection.convert.wrapAsScala._

final case class OrderedSpecs[Item](specs: List[Template[Item]]) extends CollectionSpec[Item] {

  override def build(): JList[Item] = specs.map(_.build())

  private def reconcileInHierarchy(collection: JList[Item], position: Int, nodeO: Option[Item], spec: Template[Item]): Iterable[Change] = {
    nodeO match {
      case Some(node) =>
        spec.reconciliationSteps(node).getOrElse(List(Replace(collection, spec, position)))

      case None =>
        List(Insert(collection, spec, position))
    }
  }

  override def requiredChangesIn(collection: JList[Item]): List[Change] = {
    val numChildrenOnSceneGraph: Int = collection.size()
    val numChildrenSpecs: Int = specs.length

    @tailrec def reconcile(i: Int, acc: List[Change]): List[Change] =
      if (i < numChildrenSpecs)
        reconcile(i + 1, acc ++ reconcileInHierarchy(collection, i, collection.lift(i), specs(i)))
      else
        acc

    if (numChildrenOnSceneGraph > numChildrenSpecs)
      RemoveSeq(collection, numChildrenSpecs, numChildrenOnSceneGraph) :: reconcile(0, Nil)
    else
      reconcile(0, Nil)
  }
}
