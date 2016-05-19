package com.tothferenc.templateFX

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.attribute.{Attribute, SettableFeature}
import com.tothferenc.templateFX.specs.Leaf
import com.tothferenc.templateFX.specs.base.ClassAwareSpec
import com.tothferenc.templateFX.specs.base.Template
import com.tothferenc.templateFX.specs.{Hierarchy, ScrollSpec}

import scala.language.implicitConversions
import scala.reflect.ClassTag

class PaneNodesAccess extends CollectionAccess[Pane, Node] {
  override def getCollection(container: Pane): ObservableList[Node] = container.getChildren
}

class TabPaneTabsAccess extends CollectionAccess[TabPane, Tab] {
  override def getCollection(container: TabPane): ObservableList[Tab] = container.getTabs
}

object Api {

  implicit val paneChildrenAccess = new PaneNodesAccess
  implicit val tabPaneChildrenAccess = new TabPaneTabsAccess

  implicit def specs2ordered(specs: List[Template[Node]]): CollectionSpec[TFXParent, Node] = OrderedSpecs(specs)
  implicit def specs2orderedWithIds[Key](specs: List[(Key, Template[Node])]): CollectionSpec[TFXParent, Node] = OrderedSpecsWithIds(specs)
  def unordered[Key](specs: List[(Key, Template[Node])]) = SpecsWithIds(specs)
  implicit def tabs2ordered(tabs: List[Template[Tab]]): CollectionSpec[TabPane, Tab] = OrderedSpecs(tabs)

  implicit class ReconcilationSyntax[Container](reconcilableGroup: CollectionSpec[Container, Node]) {
    def changes(container: Container): List[Change] = reconcilableGroup.requiredChangesIn(container)
    def reconcile(container: Container): Unit = changes(container).foreach(_.execute())
  }

  implicit class AttributeAssigner[FXType, Attr](attribute: Attribute[FXType, Attr]) {
    def ~(value: Attr) = Binding(attribute, value)
  }

  implicit class SettableAssigner[FXType, Attr](attribute: SettableFeature[FXType, Attr]) {
    def <<(value: Attr) = Enforcement(attribute, value)
  }

  def scrollable(constraints: Constraint[ScrollPane]*)(content: Template[Node]): Template[ScrollPane] =
    ScrollSpec[ScrollPane](constraints, content)()

  def branchC[N <: TFXParent: ClassTag](constructorParams: Any*)(constraints: Constraint[N]*)(specGroup: CollectionSpec[TFXParent, Node]): Template[N] =
    Hierarchy[N, Node](constraints, specGroup, constructorParams)

  def branch[N <: TFXParent: ClassTag](constraints: Constraint[N]*)(children: Template[Node]*): Template[N] =
    Hierarchy[N, Node](constraints, children.toList, Nil)

  def branchL[N <: TFXParent: ClassTag](constraints: Constraint[N]*)(specGroup: CollectionSpec[TFXParent, Node]): Template[N] =
    Hierarchy[N, Node](constraints, specGroup, Nil)

  def leafC[N <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, constructorParams)

  def leaf[N <: Node: ClassTag](constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, Nil)

  def tabs(constraints: Constraint[TabPane]*)(children: Template[Tab]*): Template[TabPane] =
    Hierarchy[TabPane, Tab](constraints, children.toList, Nil)

}
