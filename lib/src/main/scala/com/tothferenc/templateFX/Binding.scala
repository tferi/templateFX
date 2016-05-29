package com.tothferenc.templateFX

import com.tothferenc.templateFX.base.Attribute
import com.tothferenc.templateFX.base.RemovableFeature
import com.tothferenc.templateFX.base.SettableFeature
import com.tothferenc.templateFX.specs.Fixture
import com.tothferenc.templateFX.specs.base.Template

abstract class FeatureSetter[-Item] {
  def feature: RemovableFeature[Item]
  def set(item: Item): Unit
}

final case class SetVal[-Item, Value](
    feature: SettableFeature[Item, Value],
    value: Value
) extends FeatureSetter[Item] {
  override def set(item: Item): Unit = feature.set(item, value)
}

final case class Reconcile[-Item, Value](
    feature: Fixture[Item, Value],
    template: Template[Value]
) extends FeatureSetter[Item] {
  override def set(item: Item): Unit = feature.reconcile(item, Some(template)).foreach(_.execute())
}

abstract class Constraint[-T] {
  def feature: RemovableFeature[T]
  def maintained: Boolean
  def apply(item: T): Option[FeatureSetter[T]]
}

final case class FixtureBinding[Item, Attr](feature: Fixture[Item, Attr], template: Template[Attr], maintained: Boolean) extends Constraint[Item] {
  override def apply(v1: Item): Option[FeatureSetter[Item]] = Some(Reconcile(feature, template))
}

final case class Enforcement[Item, Attr](feature: SettableFeature[Item, Attr], value: Attr, maintained: Boolean) extends Constraint[Item] {
  override def apply(v1: Item): Option[FeatureSetter[Item]] = Some(SetVal(feature, value))
}

final case class Binding[Item, Attr](feature: Attribute[Item, Attr], value: Attr, maintained: Boolean) extends Constraint[Item] {

  override def apply(item: Item): Option[FeatureSetter[Item]] =
    if (Option(feature.read(item)).exists(existing => feature.isEqual(existing, value)))
      None
    else
      Some(SetVal(feature, value))
}
