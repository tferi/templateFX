package com.tothferenc.templateFX.specs.collection

import com.tothferenc.templateFX.userdata.UserData
import com.tothferenc.templateFX.userdata.UserDataAccess

object SpecsWithKeys {
  val TFX_KEY = "tfx_key"

  private[templateFX] def setKeyOnItem[Item, Key](key: Key, item: Item)(implicit userDataAccess: UserDataAccess[Item]): Item = {
    UserData.set(item, TFX_KEY, key)
    item
  }
}
