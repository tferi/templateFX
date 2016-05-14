package com.tothferenc.templateFX.attributes

import java.lang
import javafx.scene.Node
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane

import com.tothferenc.templateFX.attribute.Attribute

import scala.collection.convert.wrapAsScala._

object Grid {

  val columnConstraints = Attribute.list[GridPane, ColumnConstraints]("ColumnConstraints")

  val column = Attribute.remote[GridPane, Node, lang.Integer]("ColumnIndex")

  val row = Attribute.remote[GridPane, Node, lang.Integer]("RowIndex")
}
