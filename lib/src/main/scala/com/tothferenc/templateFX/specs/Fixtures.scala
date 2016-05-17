package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

abstract class Fixtures[FXType <: Node] extends ReflectiveSpec[FXType] {

  def fixtures: List[NodeFixture[FXType]]

  def specs: List[Option[NodeSpec]]

  override def initNodesBelow(instance: FXType): Unit = fixtures.zip(specs).foreach {
    case (fixture, specOpt) => SetFixture(instance, fixture, specOpt).execute()
  }

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    mutationsIfTypeMatches(node).map(_ ::: fixtures.zip(specs).flatMap {
      case (fixture, specOpt) => fixture.reconcile(node.asInstanceOf[FXType], specOpt)
    }).getOrElse(List(Replace(container, this, position)))
  }
}
