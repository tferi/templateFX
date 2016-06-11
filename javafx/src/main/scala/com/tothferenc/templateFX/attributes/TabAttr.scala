package com.tothferenc.templateFX.attributes

import javafx.scene.Node

import com.tothferenc.templateFX.base.Attribute

object TabAttr {
  val content = Attribute.simple[javafx.scene.control.Tab, Node]("Content", null)

  val text = Attribute.simple[javafx.scene.control.Tab, String]("Text", null)
}
