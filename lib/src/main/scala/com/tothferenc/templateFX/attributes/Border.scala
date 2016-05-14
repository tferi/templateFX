package com.tothferenc.templateFX.attributes

import java.lang

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.BorderPane

import com.tothferenc.templateFX.attribute.Attribute

object Border {

	val alignment = Attribute.remote[BorderPane, Node, Pos]("Alignment")

	val margin = Attribute.remote[BorderPane, Node, Insets]("Margin")
}
