package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, RemovableFeature }

abstract class FeatureSetter[-Item](val feature: RemovableFeature[Item]) {
  def set(item: Item): Unit
}

abstract class Constraint[-T] extends (T => Option[FeatureSetter[T]]) {
  def attribute: RemovableFeature[_]
  def isSatisfiedBy(item: T): Boolean
}

final case class Binding[Item <: Node, Attr](attribute: Attribute[Item, Attr], value: Attr) extends Constraint[Item] {

  override def isSatisfiedBy(item: Item): Boolean =
    Option(attribute.read(item)) != Option(value)

  override def apply(item: Item): Option[FeatureSetter[Item]] =
    if (isSatisfiedBy(item))
      Some(new FeatureSetter[Item](attribute) { override def set(item: Item) = attribute.set(item, value) })
    else
      None
}
