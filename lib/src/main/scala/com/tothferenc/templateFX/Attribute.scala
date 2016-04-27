package com.tothferenc.templateFX

object Attribute {
  val key = "attributes"
}

abstract class Unsettable[-FXType] {
  def unset(target: FXType): Unit
}

abstract class Attribute[-FXType, AttrType] extends Unsettable[FXType] {

  def read(src: FXType): AttrType

  def set(target: FXType, value: AttrType): Unit
}