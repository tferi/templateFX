package com.tothferenc.templateFX

import javafx.embed.swing.JFXPanel
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.base.Attribute
import com.tothferenc.templateFX.base.Template
import org.specs2.mutable.Specification

class FixtureSpec extends Specification {

  val attributes = List(Border.top, Border.right, Border.bottom, Border.left, Border.center)

  new JFXPanel()

  def lt(s: String): Template[Label] = node[Label](text ~ s)

  def bind(fixture: Attribute[BorderPane, Node]) = fixture ~~ lt(fixture.toString)

  "BorderPane" should {
    "be instantiated with a node in the requested place" in {
      val bp: BorderPane = node[BorderPane](bind(Border.left)).build()
      bp.getLeft.asInstanceOf[Label].getText === "left"
      val attributeValues = attributes.filterNot(_ == Border.left).map(_.read(bp))
      attributeValues.forall(_ === null)
    }

    "have only the requested nodes added when reconciled" in {
      val bp: BorderPane = node[BorderPane](bind(Border.right)).build()
      node[BorderPane](bind(Border.left)).reconciliationSteps(bp).foreach(_.foreach(_.execute()))
      bp.getLeft.asInstanceOf[Label].getText === "left"
      val attributeValues = attributes.filterNot(_ == Border.left).map(_.read(bp))
      attributeValues.forall(_ === null)
    }
  }
}
