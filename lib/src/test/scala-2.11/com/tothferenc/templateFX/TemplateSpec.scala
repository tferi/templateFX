package com.tothferenc.templateFX

import javafx.embed.swing.JFXPanel
import javafx.scene.Node
import javafx.scene.chart.PieChart
import javafx.scene.control.Label
import javafx.scene.layout.{ AnchorPane, Pane }

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.attribute.RemovableFeature
import com.tothferenc.templateFX.specs.base.ClassAwareSpec
import com.tothferenc.templateFX.specs.base.Template
import com.tothferenc.templateFX.userdata._
import org.specs2.mutable.Specification

import scala.collection.convert.wrapAsScala._

class TemplateSpec extends Specification {
  val _ = new JFXPanel()

  private val hello: Template[Node] = leaf[Label](text ~ "hello")

  private def paneWith(specGroup: CollectionSpec[TFXParent, Node]) = branchL[AnchorPane]() {
    specGroup
  }

  def child(i: Int, container: TFXParent) = container.getChildren.get(i)

  val paneWithHello: Template[AnchorPane] = branch[AnchorPane]() {
    hello
  }

  val paneWithHelloChildrenSpec = OrderedSpecs[Pane, Node](List(paneWithHello))(paneChildrenAccess, nodeUserDataAccess)

  val labelInTwoPanes = branch[AnchorPane]()(paneWithHello)

  val helloWorld: List[Template[Node]] = List(
    hello,
    leaf[Label](text ~ "world")
  )

  val keyedHelloWorld = List(
    1 -> leaf[Label](text ~ "hello"),
    2 -> leaf[Label](text ~ "world")
  )

  "Templates" should {
    "be parsed well" in {
      paneWithHello.build().asInstanceOf[Pane].getChildren.get(0).asInstanceOf[Label].getText === "hello"
    }

    "have their constraints applied to inheritors" in {
      val container: TFXParent = branch[AnchorPane]() {
        leaf[PieChart](com.tothferenc.templateFX.attributes.title ~ "well")
      }.build()
      val chart: PieChart = container.getChildren.get(0).asInstanceOf[PieChart]
      chart.getTitle === "well"
    }

    "be reconciled as expected when a single mutation is needed" in {
      val pane = paneWithHello.build()
      val changes: List[Change] = List(
        leaf[Label](text ~ "world")
      ).requiredChangesIn(pane)
      changes.length === 1
    }

    "be reconciled as expected when an element needs to be replaced with another type" in {
      val pane = paneWithHello.build()
      List(leaf[PieChart]()).reconcile(pane)
      pane.getChildren.get(0) should beAnInstanceOf[PieChart]
    }

    "be reconciled as expected when an element needs to be inserted" in {
      val pane = paneWithHello.build()
      val newDef: Template[Label] = leaf[Label](text ~ "world")
      val newTemplate = helloWorld
      val changes: Seq[Change] = newTemplate.requiredChangesIn(pane)
      changes.length === 1
      val insertNode: Insert[TFXParent, Label] = changes.head.asInstanceOf[Insert[TFXParent, Label]]
      insertNode.container === pane
      changes.foreach(_.execute())
      pane.getChildren.get(0).asInstanceOf[Label].getText === "hello"
      pane.getChildren.get(1).asInstanceOf[Label].getText === "world"
    }

    "be reconciled #2" in {
      val pane = paneWithHello.build()
      paneWithHelloChildrenSpec.reconcile(pane)
      pane.getChildren.get(0).asInstanceOf[Pane].getChildren.get(0).asInstanceOf[Label].getText === "hello"
    }

    "be able to do a simple reconcilation with replacements by key" in {
      val pane = paneWith(keyedHelloWorld).build()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      keyedHelloWorld.reverse.requiredChangesIn(pane) === List(MoveNode(pane, child1, 0), MoveNode(pane, child0, 1))
    }

    "be able to do a simple reconcilation with an insertion and replacements by key" in {

      val helloDearWorld = List(
        3 -> hello,
        2 -> leaf[Label](text ~ "dear"),
        1 -> leaf[Label](text ~ "world")
      )
      val pane = paneWith(keyedHelloWorld).build()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      val changes = helloDearWorld.requiredChangesIn(pane)
      changes(0) === InsertWithKey(pane, hello, 0, 3)
      changes(1) === MoveNode(pane, child1, 1)
      changes(3) === MoveNode(pane, child0, 2)
      changes.foreach(_.execute())
      pane.getChildren.collect {
        case l: Label => l.getText
      } === List("hello", "dear", "world")
    }

    "be able to do a simple reconcilation with deletions by key" in {

      val helloDearWorld = List(
        1 -> hello,
        2 -> leaf[Label](text ~ "dear"),
        3 -> leaf[Label](text ~ "world")
      )
      val pane = paneWith(helloDearWorld).build()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      val child2 = child(2, pane)
      val changes = List(1 -> hello).requiredChangesIn(pane)
      val change = changes(0).asInstanceOf[RemoveNodes[TFXParent, Node]]
      change.nodes should contain(child1)
      change.nodes should contain(child2)
      change.nodes.length === 2
      changes.foreach(_.execute())
      pane.getChildren.collect {
        case l: Label => l.getText
      } === List("hello")
    }

    "be able to remove sequence of elements" in {
      val pane = paneWith(helloWorld).build()
      val changes: List[Change] = List(hello).requiredChangesIn(pane)
      changes === List(RemoveSeq(pane, 1, 2))
      changes.foreach(_.execute())
      pane.getChildren.size === 1
      pane.getChildren.get(0).asInstanceOf[Label].getText === "hello"
    }

    "manage attributes as expected" in {
      val pane = paneWithHello.build()
      def getLabel: Label = {
        pane.getChildren.get(0).asInstanceOf[Label]
      }
      val label = getLabel
      val attributes = ManagedAttributes.get(label).fold(List.empty[RemovableFeature[_]])(_.toList)
      attributes === List(text)
      val changes = List(leaf[Label](styleClasses ~ List("nice"))).requiredChangesIn(pane)
      changes.exists(_.isInstanceOf[Mutation[_]]) === true
      changes.foreach(_.execute())
      val newAttr = ManagedAttributes.get(label)
      newAttr.get.size === 1
      newAttr.get.contains(styleClasses)

    }
  }

}
