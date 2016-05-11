package com.tothferenc.templateFX

import javafx.scene.Node

abstract class Spec[FXType <: Node] {
  def constraints: Seq[Constraint[FXType]]
  def materialize(): FXType
  def children: ChildrenSpec
  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change]
}
