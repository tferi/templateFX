package com.tothferenc.templateFX

import javafx.embed.swing.JFXPanel
import javafx.scene.chart.PieChart
import javafx.scene.control.Label
import javafx.scene.layout.{ Pane, AnchorPane }
import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.Attributes._
import com.tothferenc.templateFX.attribute.{ Attribute, RemovableFeature }

import org.specs2.mutable.Specification

import scala.collection.convert.wrapAsScala._
import scala.collection.mutable.ListBuffer

class TemplateSpec extends Specification {
  val _ = new JFXPanel()

  private val hello: Definition[Label] = leaf[Label](text ~ "hello")

  private def paneWith(specGroup: ChildrenSpecification) = branchL[AnchorPane]() {
    specGroup
  }

  def child(i: Int, container: TFXParent) = container.getChildren.get(i)

  val paneWithHello: Definition[AnchorPane] = branch[AnchorPane]() {
    hello
  }

  val labelInTwoPanes = branch[AnchorPane]() {
    branch[AnchorPane]() {
      hello
    }
  }

  val helloWorld: List[Definition[Label]] = List(
    hello,
    leaf[Label](text ~ "world")
  )

  val keyedHelloWorld = List(
    1 -> leaf[Label](text ~ "hello"),
    2 -> leaf[Label](text ~ "world")
  )

  "Templates" should {
    "be parsed well" in {
      paneWithHello.materialize().asInstanceOf[Pane].getChildren.get(0).asInstanceOf[Label].getText === "hello"
    }

    "have their constraints applied to inheritors" in {
      val container: TFXParent = branch[AnchorPane]() {
        leaf[PieChart](com.tothferenc.templateFX.Attributes.title ~ "well")
      }.materialize()
      val chart: PieChart = container.getChildren.get(0).asInstanceOf[PieChart]
      chart.getTitle === "well"
    }

    "be reconciled as expected when a single mutation is needed" in {
      val pane = paneWithHello.materialize()
      val changes: List[Change] = List(
        leaf[Label](text ~ "world")
      ).requiredChangesIn(pane)
      changes.length === 1
    }

    "be reconciled as expected when an element needs to be replaced with another type" in {
      val pane = paneWithHello.materialize()
      List(leaf[PieChart]()).reconcile(pane)
      pane.getChildren.get(0) should beAnInstanceOf[PieChart]
    }

    "be reconciled as expected when an element needs to be inserted" in {
      val pane = paneWithHello.materialize()
      val newDef: Definition[Label] = leaf[Label](text ~ "world")
      val newTemplate = helloWorld
      val changes: Seq[Change] = newTemplate.requiredChangesIn(pane)
      changes.length === 1
      val insertNode: Insert[Label] = changes.head.asInstanceOf[Insert[Label]]
      insertNode.container === pane
      changes.foreach(_.execute())
      pane.getChildren.get(0).asInstanceOf[Label].getText === "hello"
      pane.getChildren.get(1).asInstanceOf[Label].getText === "world"
    }

    "be reconciled #2" in {
      val pane = paneWithHello.materialize()
      labelInTwoPanes.children.reconcile(pane)
      pane.getChildren.get(0).asInstanceOf[Pane].getChildren.get(0).asInstanceOf[Label].getText === "hello"
    }

    "be able to do a simple reconcilation with replacements by key" in {
      val pane = paneWith(keyedHelloWorld).materialize()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      keyedHelloWorld.reverse.requiredChangesIn(pane) === List(Move(pane, child1, 0), Move(pane, child0, 1))
    }

    "be able to do a simple reconcilation with an insertion and replacements by key" in {

      val helloDearWorld = List(
        3 -> hello,
        2 -> leaf[Label](text ~ "dear"),
        1 -> leaf[Label](text ~ "world")
      )
      val pane = paneWith(keyedHelloWorld).materialize()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      val changes = helloDearWorld.requiredChangesIn(pane)
      changes(0) === InsertWithKey(pane, hello, 0, 3)
      changes(1) === Move(pane, child1, 1)
      changes(3) === Move(pane, child0, 2)
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
      val pane = paneWith(helloDearWorld).materialize()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      val child2 = child(2, pane)
      val changes = List(1 -> hello).requiredChangesIn(pane)
      val change = changes(0).asInstanceOf[RemoveNodes]
      change.nodes should contain(child1)
      change.nodes should contain(child2)
      change.nodes.length === 2
      changes.foreach(_.execute())
      pane.getChildren.collect {
        case l: Label => l.getText
      } === List("hello")
    }

    "be able to remove sequence of elements" in {
      val pane = paneWith(helloWorld).materialize()
      val changes: List[Change] = List(hello).requiredChangesIn(pane)
      changes === List(RemoveSeq(pane, 1, 2))
      changes.foreach(_.execute())
      pane.getChildren.size === 1
      pane.getChildren.get(0).asInstanceOf[Label].getText === "hello"
    }

    "manage attributes as expected" in {
      val pane = paneWithHello.materialize()
      def getLabel: Label = {
        pane.getChildren.get(0).asInstanceOf[Label]
      }
      val label = getLabel
      val attributes = ManagedAttributes.get(label).fold(List.empty[RemovableFeature[_]])(_.toList)
      attributes === List(text)
      val changes = List(leaf[Label](styleClasses ~ List("nice"))).requiredChangesIn(pane)
      changes.exists(_.isInstanceOf[UnsetAttributes[_]]) === true
      changes.exists(_.isInstanceOf[Setting[_]]) === true
      changes.foreach(_.execute())
      val newAttr = ManagedAttributes.get(label)
      newAttr.get.length === 1
      newAttr.get.contains(styleClasses)

    }
  }

}
