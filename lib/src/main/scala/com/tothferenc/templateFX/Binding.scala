package com.tothferenc.templateFX

abstract class Constraint[-FXType] extends (FXType => Option[Change])

final case class Binding[FXType, Attr](attribute: Attribute[FXType, Attr], value: Attr) extends Constraint[FXType] {
  override def apply(fxObj: FXType): Option[Change] =
    if (Option(attribute.readFrom(fxObj)) != Option(value))
      Some(Mutate(fxObj, attribute, value))
    else
      None
}
