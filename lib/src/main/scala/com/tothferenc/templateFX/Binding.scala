package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, RemovableFeature, SettableFeature }

abstract class FeatureSetter[-Item] {
  def feature: RemovableFeature[Item]
  def set(item: Item): Unit
}

final case class Align[-Item, Value](
    feature: SettableFeature[Item, Value],
    value: Value
) extends FeatureSetter[Item] {
  override def set(item: Item): Unit = feature.set(item, value)
}

abstract class Constraint[-T] extends (T => Option[FeatureSetter[T]]) {
  def feature: RemovableFeature[T]
}

final case class Enforcement[Item <: Node, Attr](feature: SettableFeature[Item, Attr], value: Attr) extends Constraint[Item] {
  override def apply(v1: Item): Option[FeatureSetter[Item]] = Some(Align(feature, value))
}

final case class Binding[Item <: Node, Attr](feature: Attribute[Item, Attr], value: Attr) extends Constraint[Item] {

  override def apply(item: Item): Option[FeatureSetter[Item]] =
    if (Option(feature.read(item)) == Option(value))
      None
    else
      Some(Align(feature, value))
}
