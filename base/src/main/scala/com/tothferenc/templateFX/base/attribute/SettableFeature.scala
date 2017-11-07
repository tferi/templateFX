package com.tothferenc.templateFX.base.attribute

abstract class SettableFeature[-Holder, -AttrType] extends RemovableFeature[Holder] {
  def set(target: Holder, value: AttrType): Unit
}
