package com.tothferenc.templateFX.attributes

import java.lang
import javafx.geometry.Side
import javafx.scene.chart.PieChart

import com.tothferenc.templateFX.attribute.Attribute

import scala.collection.convert.wrapAsScala._

object Chart {

  val title = Attribute.simple[javafx.scene.chart.Chart, String]("Title", null)

  val titleSide = Attribute.simple[javafx.scene.chart.Chart, Side]("TitleSide", Side.TOP)

  object Pie {

    val data = Attribute.list[PieChart, PieChart.Data]("Data")

    val startAngle = Attribute.simple[PieChart, lang.Double]("StartAngle", 0.0)

    val clockwise = Attribute.simple[PieChart, lang.Boolean]("Clockwise", true)

    val labelLineLength = Attribute.simple[PieChart, lang.Double]("LabelLineLength", 20.0)

    //val labelsVisible = Attribute.simple[PieChart, lang.Boolean]("LabelsVisible", true)
  }
}
