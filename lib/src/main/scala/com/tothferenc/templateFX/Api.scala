package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.layout.Pane

import scala.reflect.ClassTag

object Api {

  implicit def SpecList2SpecGroup(specs: List[NodeSpec]): ChildrenSpecification = SequentialSpecs(specs)
  implicit def KeyedSpecs2SpecGroup[Key](specs: List[(Key, NodeSpec)]): ChildrenSpecification = IdentifiedSpecs(specs)

  implicit class ReconcilationSyntax(reconcilableGroup: ChildrenSpecification) {
    def changes(container: Pane): List[Change] = reconcilableGroup.requiredChangesIn(container)
    def reconcile(container: Pane): Unit = changes(container).foreach(_.execute())
  }

  implicit class AttributeAssigner[FXType, Attr](attribute: Attribute[FXType, Attr]) {
    def <~(value: Attr) = Binding(attribute, value)
  }

  def parentC[FXType <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[FXType]*)(specGroup: ChildrenSpecification): Spec[FXType] =
    Definition[FXType](constraints, specGroup)(constructorParams)

  def parent[FXType <: Node: ClassTag](constraints: Constraint[FXType]*)(children: Spec[_ <: Node]*): Spec[FXType] =
    Definition[FXType](constraints, children.toList)()

  def parentL[FXType <: Node: ClassTag](constraints: Constraint[FXType]*)(specGroup: ChildrenSpecification): Spec[FXType] =
    Definition[FXType](constraints, specGroup)()

  def leafC[FXType <: Node: ClassTag](constructorParams: Any*)(constraints: Constraint[FXType]*): Spec[FXType] =
    Definition[FXType](constraints, Ignore)(constructorParams)

  def leaf[FXType <: Node: ClassTag](constraints: Constraint[FXType]*): Spec[FXType] =
    Definition[FXType](constraints, Ignore)()

}
