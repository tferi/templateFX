package com.tothferenc.templateFX.userdata

import scala.collection.mutable

abstract class UserDataAccess[-Container] {
  def init(container: Container): mutable.Map[String, Any]
  def get(container: Container): Option[mutable.Map[String, Any]]

  def getOrInit(container: Container): mutable.Map[String, Any] = get(container).getOrElse(init(container))
}