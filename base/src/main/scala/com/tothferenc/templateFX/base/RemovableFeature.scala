package com.tothferenc.templateFX.base

abstract class RemovableFeature[-Item] {
  def remove(item: Item): Unit
}
