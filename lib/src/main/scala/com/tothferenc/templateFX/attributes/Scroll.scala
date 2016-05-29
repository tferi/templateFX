package com.tothferenc.templateFX.attributes
import java.lang

import com.tothferenc.templateFX.base.{ Attribute, SettableFeature }
import javafx.css.Styleable
import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.Node
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control._
import javafx.scene.input._
import javafx.scene.layout.{ AnchorPane, ColumnConstraints, GridPane }

import com.sun.javafx.geom.BaseBounds
import com.sun.javafx.geom.transform.BaseTransform
import com.sun.javafx.jmx.{ MXNodeAlgorithm, MXNodeAlgorithmContext }
import com.sun.javafx.sg.prism.NGNode

import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

object Scroll {
  val fitToHeight = Attribute.writeOnly[ScrollPane, lang.Boolean]("FitToHeight", false)

  val fitToWidth = Attribute.writeOnly[ScrollPane, lang.Boolean]("FitToWidth", false)

  val hBar = Attribute.simple[ScrollPane, ScrollBarPolicy]("HbarPolicy", null)

  val vBar = Attribute.simple[ScrollPane, ScrollBarPolicy]("VbarPolicy", null)
}