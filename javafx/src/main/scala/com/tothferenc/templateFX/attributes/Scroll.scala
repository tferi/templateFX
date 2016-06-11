package com.tothferenc.templateFX.attributes
import java.lang
import javafx.scene.Node
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control._

import com.tothferenc.templateFX.base.Attribute

object Scroll {
  val fitToHeight = Attribute.writeOnly[ScrollPane, lang.Boolean]("FitToHeight", false)

  val fitToWidth = Attribute.writeOnly[ScrollPane, lang.Boolean]("FitToWidth", false)

  val hBar = Attribute.simple[ScrollPane, ScrollBarPolicy]("HbarPolicy", null)

  val vBar = Attribute.simple[ScrollPane, ScrollBarPolicy]("VbarPolicy", null)

  val content = Attribute.simple[ScrollPane, Node]("Content", null)
}