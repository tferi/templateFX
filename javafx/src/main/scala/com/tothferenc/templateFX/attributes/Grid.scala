package com.tothferenc.templateFX.attributes

import java.lang
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority

import com.tothferenc.templateFX.base.Attribute
import scala.collection.convert.wrapAsScala._

object Grid {

  val alignment = Attribute.simple[GridPane, Pos]("Alignment", null)

  val columnConstraints = Attribute.list[GridPane, ColumnConstraints]("ColumnConstraints")

  val column = Attribute.remote[GridPane, Node, lang.Integer]("ColumnIndex")

  val row = Attribute.remote[GridPane, Node, lang.Integer]("RowIndex")

  val columnSpan = Attribute.remote[GridPane, Node, lang.Integer]("ColumnSpan")

  val rowSpan = Attribute.remote[GridPane, Node, lang.Integer]("RowSpan")

  val hAlignment = Attribute.remote[GridPane, Node, HPos]("Halignment")

  val vAlignment = Attribute.remote[GridPane, Node, VPos]("Valignment")

  val vGrow = Attribute.remote[GridPane, Node, Priority]("Vgrow")

  val hGrow = Attribute.remote[GridPane, Node, Priority]("Hgrow")
}
