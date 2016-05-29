package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.Fixture
import com.tothferenc.templateFX.base.Template

final case class ParameterizedFixture[Container, FixedItem](fixture: Fixture[Container, FixedItem], template: Option[Template[FixedItem]]) {
  def reconcile(container: Container): List[Change] = fixture.reconcile(container, template)
}

object ParameterizedFixture {
  type For[C] = ParameterizedFixture[C, _]
}