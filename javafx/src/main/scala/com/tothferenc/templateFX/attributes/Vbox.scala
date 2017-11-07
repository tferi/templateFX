package com.tothferenc.templateFX.attributes

import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

import com.tothferenc.templateFX.base.attribute.Attribute

object Vbox {

  val vGrow = Attribute.remote[VBox, Node, Priority]("Vgrow")

  val margin = Attribute.remote[VBox, Node, Insets]("Margin")

  val spacing = Attribute.simple[VBox, Double]("Spacing", 0)
}
