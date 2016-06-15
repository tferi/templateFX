package com.tothferenc.templateFX.base

import scala.language.existentials
import scala.language.experimental.macros

final case class Reconciliation[Container, FixedItem](container: Container, fixture: Attribute[Container, FixedItem], spec: Option[Template[FixedItem]]) extends Change {
  override protected def exec(): Unit = spec match {
    case Some(template) => fixture.set(container, template.build())
    case _ => fixture.remove(container)
  }
}