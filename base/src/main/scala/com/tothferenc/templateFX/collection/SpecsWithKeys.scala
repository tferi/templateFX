package com.tothferenc.templateFX.collection

import scala.collection.mutable
import scala.reflect.ClassTag

object SpecsWithKeys {

  private val keys: mutable.WeakHashMap[Any, (ClassTag[_], Any)] = mutable.WeakHashMap.empty

  def setKeyOnItem[Item, Key: ClassTag](key: Key, item: Item): Item = {
    keys += item -> (implicitly[ClassTag[Key]], key)
    item
  }

  def getItemKey[Key: ClassTag](item: Any): Option[Key] = keys.get(item).collect {
    case (storedKeyClassTag, storedKey) if implicitly[ClassTag[Key]] == storedKeyClassTag => storedKey.asInstanceOf[Key]
  }
}
