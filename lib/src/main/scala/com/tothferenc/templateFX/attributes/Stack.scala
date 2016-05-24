package com.tothferenc.templateFX.attributes

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.StackPane

import com.tothferenc.templateFX.attribute.Attribute

object Stack {

	val alignment = Attribute.remote[StackPane, Node, Pos]("Alignment")
}
