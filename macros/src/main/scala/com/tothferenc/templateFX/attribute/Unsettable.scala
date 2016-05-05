package com.tothferenc.templateFX.attribute

abstract class Unsettable[-FXType] {
  def unset(target: FXType): Unit
}
