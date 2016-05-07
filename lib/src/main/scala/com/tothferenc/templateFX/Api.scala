package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.attribute.Attribute

import scala.language.implicitConversions
import scala.reflect.ClassTag

object Api {

  implicit def specs2ordered(specs: List[NodeDef]): ChildrenSpecification = OrderedSpecs(specs)
  implicit def specs2orderedWithIds[Key](specs: List[(Key, NodeDef)]): ChildrenSpecification = OrderedSpecsWithIds(specs)

  def unordered[Key](specs: List[(Key, NodeDef)]) = SpecsWithIds(specs)

  implicit class ReconcilationSyntax(reconcilableGroup: ChildrenSpecification) {
    def changes(container: Pane): List[Change] = reconcilableGroup.requiredChangesIn(container)
    def reconcile(container: Pane): Unit = changes(container).foreach(_.execute())
  }

  implicit class AttributeAssigner[FXType, Attr](attribute: Attribute[FXType, Attr]) {
    def ~(value: Attr) = Binding(attribute, value)
  }

  def branchC[FXType <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[FXType]*)(specGroup: ChildrenSpecification): Definition[FXType] =
    Definition[FXType](constraints, specGroup)(constructorParams)

  def branch[FXType <: Node: ClassTag](constraints: Constraint[FXType]*)(children: NodeDef*): Definition[FXType] =
    Definition[FXType](constraints, children.toList)()

  def branchL[FXType <: Node: ClassTag](constraints: Constraint[FXType]*)(specGroup: ChildrenSpecification): Definition[FXType] =
    Definition[FXType](constraints, specGroup)()

  def leafC[FXType <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[FXType]*): Definition[FXType] =
    Definition[FXType](constraints, Ignore)(constructorParams)

  def leaf[FXType <: Node: ClassTag](constraints: Constraint[FXType]*): Definition[FXType] =
    Definition[FXType](constraints, Ignore)()

}
