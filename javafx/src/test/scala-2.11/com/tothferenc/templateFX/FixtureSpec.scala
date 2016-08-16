package com.tothferenc.templateFX

import javafx.embed.swing.JFXPanel
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.base.Attribute
import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.Template
import org.specs2.mutable.Specification

class FixtureSpec extends Specification {

  val attr = List(top, attributes.right, bottom, attributes.left, center)

  new JFXPanel()

  def lt(s: String): Template[Label] = node[Label](text ~ s)

  def bind(fixture: Attribute[BorderPane, Node]) = fixture ~~ lt(fixture.toString)

  "BorderPane" should {
    "be instantiated with a node in the requested place" in {
      val bp: BorderPane = node[BorderPane](bind(attributes.left)).build()
      bp.getLeft.asInstanceOf[Label].getText === "left"
      val attributeValues = attr.filterNot(_ == attributes.left).map(_.read(bp))
      attributeValues.forall(_ === null)
    }

    "have only the requested nodes added when reconciled" in {
      val bp: BorderPane = node[BorderPane](bind(attributes.right)).build()
      val steps: Option[List[Change]] = node[BorderPane](bind(attributes.left)).reconciliationSteps(bp)
      steps.foreach(_.foreach(_.execute()))
      bp.getLeft.asInstanceOf[Label].getText === "left"
      val attributeValues = attr.filterNot(_ == attributes.left).map(_.read(bp))
      attributeValues.forall(_ === null)
    }
  }
}
