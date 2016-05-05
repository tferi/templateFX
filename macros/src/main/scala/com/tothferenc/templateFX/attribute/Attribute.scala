package com.tothferenc.templateFX.attribute

object Attribute {
  val key = "attributes"
}

abstract class Attribute[-FXType, AttrType] extends Unsettable[FXType] {

  def read(src: FXType): AttrType

  def set(target: FXType, value: AttrType): Unit
}