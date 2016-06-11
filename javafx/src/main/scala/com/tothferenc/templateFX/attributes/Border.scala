package com.tothferenc.templateFX.attributes

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.BorderPane

import com.tothferenc.templateFX.base.Attribute

object Border {

  val alignment = Attribute.remote[BorderPane, Node, Pos]("Alignment")

  val margin = Attribute.remote[BorderPane, Node, Insets]("Margin")

  val top = Attribute.simple[BorderPane, Node]("Top", null)

  val right = Attribute.simple[BorderPane, Node]("Right", null)

  val bottom = Attribute.simple[BorderPane, Node]("Bottom", null)

  val left = Attribute.simple[BorderPane, Node]("Left", null)

  val center = Attribute.simple[BorderPane, Node]("Center", null)
}
