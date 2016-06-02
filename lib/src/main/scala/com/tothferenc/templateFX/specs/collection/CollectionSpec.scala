package com.tothferenc.templateFX.specs.collection

import java.util.{ List => JList }

import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.Template

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
