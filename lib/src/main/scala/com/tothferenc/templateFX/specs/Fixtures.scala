package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.specs.base.ReflectiveSpec
import com.tothferenc.templateFX.specs.base.Template

abstract class Fixtures[T, FixedItem] extends ReflectiveSpec[T] {

  def fixtures: List[Fixture[T, FixedItem]]

  def specs: List[Option[Template[FixedItem]]]

  override def initNodesBelow(instance: T): Unit = fixtures.zip(specs).foreach {
    case (fixture, specOpt) => SetFixture(instance, fixture, specOpt).execute()
  }

  override def reconcilationSteps(otherItem: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(otherItem).map {
      _ ::: fixtures.zip(specs).flatMap {
        case (fixture, specOpt) => fixture.reconcile(otherItem.asInstanceOf[T], specOpt)
      }
    }
  }
}
