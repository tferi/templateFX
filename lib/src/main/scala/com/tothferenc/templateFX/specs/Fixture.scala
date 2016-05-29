package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.attribute.SettableFeature
import com.tothferenc.templateFX.specs.base.Template

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

final case class ParameterizedFixture[Container, FixedItem](fixture: Fixture[Container, FixedItem], template: Option[Template[FixedItem]]) {
  def reconcile(container: Container): List[Change] = fixture.reconcile(container, template)
}

object ParameterizedFixture {
  type For[C] = ParameterizedFixture[C, _]
}