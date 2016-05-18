package com.tothferenc.templateFX

import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.attribute.{ Attribute, SettableFeature }
import com.tothferenc.templateFX.specs.base.ClassAwareSpec
import com.tothferenc.templateFX.specs.base.Template
import com.tothferenc.templateFX.specs.{ Hierarchy, ScrollSpec }

import scala.language.implicitConversions
import scala.reflect.ClassTag

class PaneNodesAccess extends CollectionAccess[Pane, Node] {
  override def getCollection(container: Pane): ObservableList[Node] = container.getChildren
}

object Api {

  implicit val paneChildrenAccess = new PaneNodesAccess

  implicit def specs2ordered(specs: List[Template[Node]]): CollectionSpec[TFXParent, Node] = OrderedSpecs(specs)
  implicit def specs2orderedWithIds[Key](specs: List[(Key, Template[Node])]): CollectionSpec[TFXParent, Node] = OrderedSpecsWithIds(specs)

  def unordered[Key](specs: List[(Key, Template[Node])]) = SpecsWithIds(specs)

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

  def scrollable(constraints: Constraint[ScrollPane]*)(content: NodeSpec): ClassAwareSpec[ScrollPane] =
    ScrollSpec[ScrollPane](constraints, content)()

  def branchC[FXType <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[FXType]*)(specGroup: CollectionSpec[TFXParent, Node]): ClassAwareSpec[FXType] =
    Hierarchy[FXType](constraints, specGroup)(constructorParams)

  def branch[FXType <: Node: ClassTag](constraints: Constraint[FXType]*)(children: Template[Node]*): ClassAwareSpec[FXType] =
    Hierarchy[FXType](constraints, children.toList)()

  def branchL[FXType <: Node: ClassTag](constraints: Constraint[FXType]*)(specGroup: CollectionSpec[TFXParent, Node]): ClassAwareSpec[FXType] =
    Hierarchy[FXType](constraints, specGroup)()

  def leafC[FXType <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[FXType]*): ClassAwareSpec[FXType] =
    Hierarchy[FXType](constraints, Ignore)(constructorParams)

  def leaf[FXType <: Node: ClassTag](constraints: Constraint[FXType]*): ClassAwareSpec[FXType] =
    Hierarchy[FXType](constraints, Ignore)()

}
