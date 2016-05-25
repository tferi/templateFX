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

    private val dataEquality: ((PieChart.Data, PieChart.Data)) => Boolean =
      { case (d1, d2) => d1.getName == d2.getName && d1.getPieValue == d2.getPieValue }

    val data = Attribute.listCustomEquals[PieChart, PieChart.Data]("Data", dataEquality)

    val startAngle = Attribute.simple[PieChart, lang.Double]("StartAngle", 0.0)

    val clockwise = Attribute.simple[PieChart, lang.Boolean]("Clockwise", true)

    val labelLineLength = Attribute.simple[PieChart, lang.Double]("LabelLineLength", 20.0)

    //val labelsVisible = Attribute.simple[PieChart, lang.Boolean]("LabelsVisible", true)
  }
}
