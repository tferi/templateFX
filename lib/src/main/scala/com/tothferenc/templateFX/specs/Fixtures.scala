package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

abstract class Fixtures[FXType <: Node] extends ReflectiveSpec[FXType] {

  def fixtures: List[NodeFixture[FXType]]

  def specs: List[Option[NodeSpec]]

  override def initNodesBelow(instance: FXType): Unit = fixtures.zip(specs).foreach {
    case (fixture, specOpt) => SetFixture(instance, fixture, specOpt).execute()
  }

  override def reconcilationSteps(otherItem: Node): Option[List[Change]] = {
    super.reconcilationSteps(otherItem).map {
      _ ::: fixtures.zip(specs).flatMap {
        case (fixture, specOpt) => fixture.reconcile(otherItem.asInstanceOf[FXType], specOpt)
      }
    }
  }
}
