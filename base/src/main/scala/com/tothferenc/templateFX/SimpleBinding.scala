package com.tothferenc.templateFX

import com.tothferenc.templateFX.base._

/**
 * Instances of this class are used to make changes to the object graph during the reconciliation process.
 */
abstract class FeatureSetter[-Item] {
  def feature: RemovableFeature[Item]
  def set(item: Item): Unit
}

/**
 * Replaces the [[feature]]'s current value with the parameter [[value]].
 */
final case class SimpleSetting[-Item, Value](
    feature: SettableFeature[Item, Value],
    value: Value
) extends FeatureSetter[Item] {
  override def set(item: Item): Unit = feature.set(item, value)
}

/**
 * Makes the [[feature]] conform to the [[template]] via a reconciliation process.
 */
final case class Reconcile[-Item, Value](
    feature: Attribute[Item, Value],
    template: Template[Value]
) extends FeatureSetter[Item] {
  override def set(item: Item): Unit = feature.reconcile(item, Some(template)).foreach(_.execute())
}

/**
 * A [[Constraint]] is something that may be applied on an object.
 * @tparam T The type of object we want to apply constraints on.
 */
abstract class Constraint[-T] {

  /**
   * The [[feature]] this constraint is set for.
   * It is stored here to be used if this constraint will be lifted from the [[T]] instance.
   * When the constraint is lifted, the [[feature]] will be restored to its default state.
   * @return
   */
  def feature: RemovableFeature[T]

  /**
   * If this is true, this [[Constraint]] will be applied on the target object on every reconciliation.
   * If it's false, then the [[Constraint]] will be applied once, after the object is instantiated.
   * @return
   */
  def maintained: Boolean

  /**
   * @return A [[FeatureSetter ]]which may be used to set [[feature]]'s value in the parameter to a predefined value.
   */
  def apply(item: T): Option[FeatureSetter[T]]
}

/**
 * This kind of [[Constraint]] should be set up when the user wants to run a full reconciliation on the [[feature]].
 */
final case class ReconciliationBinding[Item, Attr](feature: Attribute[Item, Attr], template: Template[Attr], maintained: Boolean) extends Constraint[Item] {
  override def apply(v1: Item): Option[FeatureSetter[Item]] = Some(Reconcile(feature, template))
}

/**
 * This kind of [[Constraint]] should be set up when the user wants to set [[feature]] to [[value]] regardless of its current state.
 */
final case class Enforcement[Item, Attr](feature: SettableFeature[Item, Attr], value: Attr, maintained: Boolean) extends Constraint[Item] {
  override def apply(v1: Item): Option[FeatureSetter[Item]] = Some(SimpleSetting(feature, value))
}

/**
 * This kind of [[Constraint]] should be set up when the user wants to set [[feature]] to [[value]] when their values do not match.
 */
final case class SimpleBinding[Item, Attr](feature: Attribute[Item, Attr], value: Attr, maintained: Boolean) extends Constraint[Item] {
  override def apply(item: Item): Option[FeatureSetter[Item]] =
    if (Option(feature.read(item)).exists(existing => feature.isEqual(existing, value)))
      None
    else
      Some(SimpleSetting(feature, value))
}
