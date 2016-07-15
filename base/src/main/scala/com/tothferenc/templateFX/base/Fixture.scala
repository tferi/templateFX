package com.tothferenc.templateFX.base

final case class Fixture[Item, U](item: Item, attribute: Attribute[Item, U]) {
  def reconcile(template: Template[U]) = changes(template).foreach(_.execute())

  private def changes(template: Template[U]): List[Change] = {
    attribute.reconcile(item, Some(template))
  }
}