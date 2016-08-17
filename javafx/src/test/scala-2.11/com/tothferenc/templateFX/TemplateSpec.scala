package com.tothferenc.templateFX

import javafx.embed.swing.JFXPanel
import javafx.scene.Node
import javafx.scene.chart.PieChart
import javafx.scene.control.Label
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.base.RemovableFeature
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.collection.CollectionSpec
import com.tothferenc.templateFX.collection.OrderedSpecs
import org.specs2.mutable.Specification

import scala.collection.convert.wrapAsScala._

class TemplateSpec extends Specification {
  val _ = new JFXPanel()

  private val hello: Template[Node] = node[Label](text.label ~ "hello")

  private def paneWith(specGroup: CollectionSpec[Node]) = node[AnchorPane](children ~~ specGroup)

  def child(i: Int, container: Pane) = container.getChildren.get(i)

  val paneWithHello: Template[AnchorPane] = node[AnchorPane](children ~~ List(hello))

  val paneWithHelloChildrenSpec = OrderedSpecs[Node](List(paneWithHello))

  val labelInTwoPanes = node[AnchorPane](children ~~ List(paneWithHello))

  val helloWorld: List[Template[Node]] = List(
    hello,
    node[Label](text.label ~ "world")
  )

  val keyedHelloWorld: List[(Int, Template[Node])] = List(
    1 -> node[Label](text.label ~ "hello"),
    2 -> node[Label](text.label ~ "world")
  )

  "Templates" should {
    "be parsed well" in {
      paneWithHello.build().asInstanceOf[Pane].getChildren.get(0).asInstanceOf[Label].getText === "hello"
    }

    "have their constraints applied to inheritors" in {
      val container: Pane = node[AnchorPane](children ~~ List(node[PieChart](com.tothferenc.templateFX.attributes.title ~ "well"))).build()
      val chart: PieChart = container.getChildren.get(0).asInstanceOf[PieChart]
      chart.getTitle === "well"
    }

    "be reconciled as expected when a single mutation is needed" in {
      val pane = paneWithHello.build()
      val changes: List[Change] = List.apply[Template[Node]](
        node[Label](text.label ~ "world")
      ).requiredChangesIn(pane.getChildren)
      changes.length === 1
    }

    "be reconciled as expected when an element needs to be replaced with another type" in {
      val pane = paneWithHello.build()
      List.apply[Template[Node]](node[PieChart]()).reconcile(pane.getChildren)
      pane.getChildren.get(0) should beAnInstanceOf[PieChart]
    }

    "be reconciled as expected when an element needs to be inserted" in {
      val pane = paneWithHello.build()
      val newDef: Template[Label] = node[Label](text.label ~ "world")
      val newTemplate = helloWorld
      val changes: Seq[Change] = newTemplate.requiredChangesIn(pane.getChildren)
      changes.length === 1
      val insertNode: Insert[Pane, Label] = changes.head.asInstanceOf[Insert[Pane, Label]]
      insertNode.collection === pane.getChildren
      changes.foreach(_.execute())
      pane.getChildren.get(0).asInstanceOf[Label].getText === "hello"
      pane.getChildren.get(1).asInstanceOf[Label].getText === "world"
    }

    "be reconciled #2" in {
      val pane = paneWithHello.build()
      paneWithHelloChildrenSpec.reconcile(pane.getChildren)
      pane.getChildren.get(0).asInstanceOf[Pane].getChildren.get(0).asInstanceOf[Label].getText === "hello"
    }

    "be able to do a simple reconciliation with replacements by key" in {
      val pane = paneWith(keyedHelloWorld).build()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      keyedHelloWorld.reverse.requiredChangesIn(pane.getChildren) === List(MoveNode(pane.getChildren, child1, 0), MoveNode(pane.getChildren, child0, 1))
    }

    "be able to do a simple reconciliation with an insertion and replacements by key" in {

      val helloDearWorld = List(
        3 -> hello,
        2 -> node[Label](text.label ~ "dear"),
        1 -> node[Label](text.label ~ "world")
      )
      val pane = paneWith(keyedHelloWorld).build()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      val changes = helloDearWorld.requiredChangesIn(pane.getChildren)
      changes.head === InsertWithKey(pane.getChildren, hello, 0, 3)
      changes(1) === MoveNode(pane.getChildren, child1, 1)
      changes(3) === MoveNode(pane.getChildren, child0, 2)
      changes.foreach(_.execute())
      pane.getChildren.collect {
        case l: Label => l.getText
      } === List("hello", "dear", "world")
    }

    "be able to do a simple reconciliation with deletions by key" in {

      val helloDearWorld = List(
        1 -> hello,
        2 -> node[Label](text.label ~ "dear"),
        3 -> node[Label](text.label ~ "world")
      )
      val pane = paneWith(helloDearWorld).build()
      val child0 = child(0, pane)
      val child1 = child(1, pane)
      val child2 = child(2, pane)
      val changes = List(1 -> hello).requiredChangesIn(pane.getChildren)
      val change = changes.head.asInstanceOf[RemoveNodes[Pane, Node]]
      change.nodes should contain(child1)
      change.nodes should contain(child2)
      change.nodes.toList.length === 2
      changes.foreach(_.execute())
      pane.getChildren.collect {
        case l: Label => l.getText
      } === List("hello")
    }

    "be able to remove sequence of elements" in {
      val pane = paneWith(helloWorld).build()
      val changes: List[Change] = List(hello).requiredChangesIn(pane.getChildren)
      changes === List(RemoveSeq(pane.getChildren, 1, 2))
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
      attributes === List(text.label)
      val changes = List.apply[Template[Node]](node[Label](styleClasses ~ List("nice"))).requiredChangesIn(pane.getChildren)
      changes.exists(_.isInstanceOf[Mutation[_]]) === true
      changes.foreach(_.execute())
      val newAttr = ManagedAttributes.get(label)
      newAttr.get.size === 1
      newAttr.get.contains(styleClasses)

    }
  }

}
