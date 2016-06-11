package com.tothferenc.templateFX.attributes

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority

import com.tothferenc.templateFX.base.Attribute

object Hbox {

  val hGrow = Attribute.remote[HBox, Node, Priority]("Hgrow")

  val margin = Attribute.remote[HBox, Node, Insets]("Margin")

  val spacing = Attribute.simple[HBox, Double]("Spacing", 0)
}
