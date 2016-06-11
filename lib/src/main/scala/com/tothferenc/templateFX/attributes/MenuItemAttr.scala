package com.tothferenc.templateFX.attributes

import javafx.scene.Node

import com.tothferenc.templateFX.base.Attribute

object MenuItemAttr {
  val text = Attribute.simple[javafx.scene.control.MenuItem, String]("Text", null)

  val graphic = Attribute.simple[javafx.scene.control.MenuItem, Node]("Graphic", null)
}
