package com.tothferenc.templateFX.base

import com.tothferenc.templateFX.base.attribute.Attribute
import com.tothferenc.templateFX.change.Change

final case class Fixture[Item, U](item: Item, attribute: Attribute[Item, U]) {
  def reconcile(template: Template[U]): Unit = changes(template).foreach(_.execute())

  private def changes(template: Template[U]): Iterable[Change] = {
    attribute.reconcile(item, Some(template))
  }
}