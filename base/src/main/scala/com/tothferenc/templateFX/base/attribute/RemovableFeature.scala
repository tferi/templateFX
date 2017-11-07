package com.tothferenc.templateFX.base.attribute

abstract class RemovableFeature[-Item] {
  def remove(item: Item): Unit
}
