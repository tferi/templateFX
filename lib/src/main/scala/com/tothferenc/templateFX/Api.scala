package com.tothferenc.templateFX

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.attribute.Attribute
import com.tothferenc.templateFX.attribute.SettableFeature
import com.tothferenc.templateFX.specs._
import com.tothferenc.templateFX.specs.base.Template
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.language.implicitConversions
import scala.reflect.ClassTag

class PaneNodesAccess extends CollectionAccess[Pane, Node] {
  override def getCollection(container: Pane): ObservableList[Node] = container.getChildren
}

class TabPaneTabsAccess extends CollectionAccess[TabPane, Tab] {
  override def getCollection(container: TabPane): ObservableList[Tab] = container.getTabs
}

object Api {

  implicit case object TabContent extends NodeFixture[Tab] {
    override def read(container: Tab): Option[Node] = Option(container.getContent)
    override def set(container: Tab, node: Node): Unit = container.setContent(node)
  }

  implicit case object ScrollableContent extends NodeFixture[ScrollPane] {
    override def read(container: ScrollPane): Option[Node] = Option(container.getContent)
    override def set(container: ScrollPane, node: Node): Unit = container.setContent(node)
  }

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

  def branchC[N <: TFXParent: ClassTag](constructorParams: Any*)(constraints: Constraint[N]*)(specGroup: CollectionSpec[TFXParent, Node]): Template[N] =
    Hierarchy[N, Node](constraints, specGroup, constructorParams)

  def branch[Container, Item](constraints: Constraint[Container]*)(children: Template[Item]*)(
    implicit
    collectionAccess: CollectionAccess[Container, Item],
    itemUserDataAccess: UserDataAccess[Item],
    containerUserDataAccess: UserDataAccess[Container],
    classTag: ClassTag[Container]
  ): Template[Container] =
    Hierarchy[Container, Item](constraints, OrderedSpecs[Container, Item](children.toList), Nil)

  def branchL[N <: TFXParent: ClassTag](constraints: Constraint[N]*)(specGroup: CollectionSpec[TFXParent, Node]): Template[N] =
    Hierarchy[N, Node](constraints, specGroup, Nil)

  def leafC[N: ClassTag: UserDataAccess](constructorParams: Any*)(constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, constructorParams)

  def leaf[N: ClassTag: UserDataAccess](constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, Nil)

  def fixture[F: ClassTag: UserDataAccess: NodeFixture](constraints: Constraint[F]*)(content: Template[Node]): Template[F] =
    FixtureSpec[F](constraints, content)

  def tabs[T <: TabPane: ClassTag](constraints: Constraint[T]*)(children: Template[Tab]*): Template[T] =
    Hierarchy[T, Tab](constraints, children.toList, Nil)

}
