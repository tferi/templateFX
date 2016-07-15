package com.tothferenc.templateFX.attributes

import java.lang
import javafx.geometry.Side
import javafx.scene.chart.PieChart
import javafx.scene.chart.PieChart.Data

import com.tothferenc.templateFX.base.Attribute

import scala.collection.convert.wrapAsScala._

object Chart {

  val title = Attribute.simple[javafx.scene.chart.Chart, String]("Title", null)

  val titleSide = Attribute.simple[javafx.scene.chart.Chart, Side]("TitleSide", Side.TOP)

  val animated = new Attribute[PieChart, lang.Boolean] {
    override def read(src: PieChart): lang.Boolean = src.getAnimated()
    override def remove(target: PieChart): Unit = target.setAnimated(true)
    override def set(target: PieChart, value: lang.Boolean): Unit = target.setAnimated(value)
    override def toString(): String = "labelsVisible"
  }

  object Pie {

    private val dataEquality: ((PieChart.Data, PieChart.Data)) => Boolean =
      { case (d1, d2) => d1.getName == d2.getName && d1.getPieValue == d2.getPieValue }

    val data = Attribute.listCustomEquals[PieChart, PieChart.Data]("Data", dataEquality)

    val startAngle = Attribute.simple[PieChart, lang.Double]("StartAngle", 0.0)

    val clockwise = Attribute.simple[PieChart, lang.Boolean]("Clockwise", true)

    val labelLineLength = Attribute.simple[PieChart, lang.Double]("LabelLineLength", 20.0)

    val labelsVisible = new Attribute[PieChart, lang.Boolean] {
      override def read(src: PieChart): lang.Boolean = src.getLabelsVisible()
      override def remove(target: PieChart): Unit = target.setLabelsVisible(true)
      override def set(target: PieChart, value: lang.Boolean): Unit = target.setLabelsVisible(value)
      override def toString(): String = "labelsVisible"
    }
  }
}
