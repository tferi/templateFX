package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, Unsettable }

abstract class Constraint[-T] extends (T => Option[Change]) {
  def attribute: Unsettable[_]
  def isSatisfiedBy(item: T): Boolean
}

final case class Binding[Item <: Node, Attr](attribute: Attribute[Item, Attr], value: Attr) extends Constraint[Item] {

  override def isSatisfiedBy(item: Item): Boolean =
    Option(attribute.read(item)) != Option(value)

  override def apply(item: Item): Option[Change] =
    if (isSatisfiedBy(item))
      Some(Mutate(item, attribute, value))
    else
      None
}
