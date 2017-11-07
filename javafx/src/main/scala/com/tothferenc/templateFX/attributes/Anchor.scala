package com.tothferenc.templateFX.attributes

import java.lang
import javafx.scene.Node
import javafx.scene.layout.AnchorPane

import com.tothferenc.templateFX.base.attribute.Attribute

object Anchor {

  val top = Attribute.remote[AnchorPane, Node, lang.Double]("TopAnchor")

  val bottom = Attribute.remote[AnchorPane, Node, lang.Double]("BottomAnchor")

  val left = Attribute.remote[AnchorPane, Node, lang.Double]("LeftAnchor")

  val right = Attribute.remote[AnchorPane, Node, lang.Double]("RightAnchor")

}