package com.tothferenc.templateFX

import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, Unsettable }

abstract class Constraint[-FXType] extends (FXType => Option[Change]) {
  def attribute: Unsettable[_]
}

final case class Binding[FXType <: Node, Attr](attribute: Attribute[FXType, Attr], value: Attr) extends Constraint[FXType] {
  override def apply(fxObj: FXType): Option[Change] =
    if (Option(attribute.read(fxObj)) != Option(value))
      Some(Mutate(fxObj, attribute, value))
    else
      None
}
