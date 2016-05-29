package com.tothferenc.templateFX.base

import scala.language.existentials

final case class SetFixture[Container, FixedItem](container: Container, fixture: Fixture[Container, FixedItem], spec: Option[Template[FixedItem]]) extends Change {
  override protected def exec(): Unit = spec match {
    case Some(template) => fixture.set(container, template.build())
    case _ => fixture.remove(container)
  }
}

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
