package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

abstract class Fixtures[T] extends ReflectiveSpec[T] {

  def fixtures: List[NodeFixture[T]]

  def specs: List[Option[Template[Node]]]

  override def initNodesBelow(instance: T): Unit = fixtures.zip(specs).foreach {
    case (fixture, specOpt) => SetFixture(instance, fixture, specOpt).execute()
  }

  override def reconcilationSteps(otherItem: Node): Option[List[Change]] = {
    super.reconcilationSteps(otherItem).map {
      _ ::: fixtures.zip(specs).flatMap {
        case (fixture, specOpt) => fixture.reconcile(otherItem.asInstanceOf[T], specOpt)
      }
    }
  }
}
