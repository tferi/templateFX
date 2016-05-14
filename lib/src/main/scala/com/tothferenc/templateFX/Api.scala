package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.attribute.{ Attribute, SettableFeature }
import com.tothferenc.templateFX.specs.{ Hierarchy, ScrollSpec, Spec }

import scala.language.implicitConversions
import scala.reflect.ClassTag

object Api {

  implicit def specs2ordered(specs: List[NodeSpec]): ChildrenSpec = OrderedSpecs(specs)
  implicit def specs2orderedWithIds[Key](specs: List[(Key, NodeSpec)]): ChildrenSpec = OrderedSpecsWithIds(specs)

  def unordered[Key](specs: List[(Key, NodeSpec)]) = SpecsWithIds(specs)

  implicit class ReconcilationSyntax(reconcilableGroup: ChildrenSpec) {
    def changes(container: Pane): List[Change] = reconcilableGroup.requiredChangesIn(container)
    def reconcile(container: Pane): Unit = changes(container).foreach(_.execute())
  }

  implicit class AttributeAssigner[FXType, Attr](attribute: Attribute[FXType, Attr]) {
    def ~(value: Attr) = Binding(attribute, value)
  }

  implicit class SettableAssigner[FXType, Attr](attribute: SettableFeature[FXType, Attr]) {
    def <<(value: Attr) = Enforcement(attribute, value)
  }

  def scrollable(constraints: Constraint[ScrollPane]*)(content: NodeSpec): Spec[ScrollPane] =
    ScrollSpec[ScrollPane](constraints, content)()

  def branchC[FXType <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[FXType]*)(specGroup: ChildrenSpec): Spec[FXType] =
    Hierarchy[FXType](constraints, specGroup)(constructorParams)

  def branch[FXType <: Node: ClassTag](constraints: Constraint[FXType]*)(children: NodeSpec*): Spec[FXType] =
    Hierarchy[FXType](constraints, children.toList)()

  def branchL[FXType <: Node: ClassTag](constraints: Constraint[FXType]*)(specGroup: ChildrenSpec): Spec[FXType] =
    Hierarchy[FXType](constraints, specGroup)()

  def leafC[FXType <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[FXType]*): Spec[FXType] =
    Hierarchy[FXType](constraints, Ignore)(constructorParams)

  def leaf[FXType <: Node: ClassTag](constraints: Constraint[FXType]*): Spec[FXType] =
    Hierarchy[FXType](constraints, Ignore)()

}
