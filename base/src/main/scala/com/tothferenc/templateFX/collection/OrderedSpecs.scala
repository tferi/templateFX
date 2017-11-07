package com.tothferenc.templateFX.collection

import java.util
import java.util.{List => JList}

import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.change.Change
import com.tothferenc.templateFX.change.Insert
import com.tothferenc.templateFX.change.RemoveSeq
import com.tothferenc.templateFX.change.Replace

import scala.collection.mutable
import scala.collection.immutable

final case class OrderedSpecs[Item](specs: immutable.Seq[Template[Item]]) extends CollectionSpec[Item] {

  override def build(): JList[Item] = {
    val items = new util.ArrayList[Item]()
    specs.foreach(template => items.add(template.build()))
    items
  }

  private def reconcileInHierarchy(collection: JList[Item], position: Int, nodeO: Option[Item], spec: Template[Item]): Iterable[Change] = {
    nodeO match {
      case Some(node) =>
        spec.reconciliationSteps(node).getOrElse(List(Replace(collection, spec, position)))

      case None =>
        List(Insert(collection, spec, position))
    }
  }

  override def requiredChangesIn(collection: JList[Item]): Iterable[Change] = {
    val numChildrenOnSceneGraph: Int = collection.size()
    val numChildrenSpecs: Int = specs.length

    val buffer = new mutable.ArrayBuffer[Change]()

    for {
      i <- specs.indices
    } {
      buffer ++= {
        val existing = if(i < collection.size()) Some(collection.get(i)) else None
        reconcileInHierarchy(collection, i, existing, specs(i))
      }
    }

    if (numChildrenOnSceneGraph > numChildrenSpecs) {
      buffer += RemoveSeq(collection, numChildrenSpecs, numChildrenOnSceneGraph)
    }

    buffer
  }
}
