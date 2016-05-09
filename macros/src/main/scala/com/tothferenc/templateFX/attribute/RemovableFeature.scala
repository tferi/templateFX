package com.tothferenc.templateFX.attribute

abstract class RemovableFeature[-Item] {
  def remove(item: Item): Unit
}
