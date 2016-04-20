package com.tothferenc.templateFX

abstract class Attribute[-FXType, AttrType] {

  def readFrom(src: FXType): AttrType

  def set(target: FXType, value: AttrType): Unit
}