package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.attribute.RemovableFeature
import com.tothferenc.templateFX.specs.base.Template

import scala.language.existentials

abstract class Fixture[-Container, FixedItem] extends RemovableFeature[Container] {

  override def remove(item: Container): Unit = set(item, default)

  def default: FixedItem

  def read(container: Container): Option[FixedItem]

  def set(container: Container, fixed: FixedItem): Unit

  def reconcile(container: Container, specOption: Option[Template[FixedItem]]): List[Change] = {
    read(container) -> specOption match {
      case (Some(existing), Some(specified)) =>
        specified.reconcilationSteps(existing).getOrElse(List(SetFixture(container, this, specOption)))

      case (None, None) =>
        Nil

      case _ =>
        List(SetFixture(container, this, specOption))
    }
  }
}

final case class ParameterizedFixture[Container, FixedItem](fixture: Fixture[Container, FixedItem], template: Option[Template[FixedItem]]) {
  def reconcile(container: Container): List[Change] = fixture.reconcile(container, template)
}

object ParameterizedFixture {
  type For[C] = ParameterizedFixture[C, _]
}