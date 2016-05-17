package com.tothferenc.templateFX.userdata

import javafx.scene.Node

import scala.collection.mutable

object UserData {

  def get[Container: UserDataAccess, ExpectedType](container: Container, key: String): Option[ExpectedType] = {
    implicitly[UserDataAccess[Container]].get(container).flatMap { userData =>
      userData.get(key).map(_.asInstanceOf[ExpectedType])
    }
  }

  def set[Container: UserDataAccess, Value](container: Container, key: String, value: Value): Unit = {
    implicitly[UserDataAccess[Container]].getOrInit(container) += key -> value
  }
}
