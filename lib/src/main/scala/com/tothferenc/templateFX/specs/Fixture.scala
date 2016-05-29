package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.SettableFeature
import com.tothferenc.templateFX.base.Template

import scala.language.existentials

abstract class Fixture[-Container, FixedItem] extends SettableFeature[Container, FixedItem] {

  override def remove(item: Container): Unit

  def read(container: Container): FixedItem

  def set(container: Container, fixed: FixedItem): Unit

  def reconcile(container: Container, specOption: Option[Template[FixedItem]]): List[Change] = {
    Option(read(container)) -> specOption match {
      case (Some(existing), Some(specified)) =>
        specified.reconcilationSteps(existing).getOrElse(List(SetFixture(container, this, specOption)))

      case (None, None) =>
        Nil

      case _ =>
        List(SetFixture(container, this, specOption))
    }
  }
}
