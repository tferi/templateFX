package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, RemovableFeature }

abstract class FeatureSetter[-Item] {
  def feature: RemovableFeature[Item]
  def set(item: Item): Unit
}

final case class Align[-Item, Value](
    feature: Attribute[Item, Value],
    value: Value
  ) extends FeatureSetter[Item] {
  override def set(item: Item): Unit = feature.set(item, value)
}

abstract class Constraint[-T] extends (T => Option[FeatureSetter[T]]) {
  def attribute: RemovableFeature[_]
  def isSatisfiedBy(item: T): Boolean
}

final case class Binding[Item <: Node, Attr](attribute: Attribute[Item, Attr], value: Attr) extends Constraint[Item] {

  override def isSatisfiedBy(item: Item): Boolean =
    Option(attribute.read(item)) == Option(value)

  override def apply(item: Item): Option[FeatureSetter[Item]] =
    if (isSatisfiedBy(item))
      None
    else
      Some(Align(attribute, value))
}
