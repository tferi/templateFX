package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.specs.base.ReflectiveSpec

abstract class Fixtures[T] extends ReflectiveSpec[T] {

  def parameterizedFixtures: List[ParameterizedFixture[T, _]]

  override def initNodesBelow(instance: T): Unit = parameterizedFixtures.foreach {
    case ParameterizedFixture(fixture, specOpt) => SetFixture(instance, fixture, specOpt).execute()
  }

  override def reconcilationSteps(otherItem: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(otherItem).map {
      _ ::: parameterizedFixtures.flatMap {
        case ParameterizedFixture(fixture, specOpt) => fixture.reconcile(otherItem.asInstanceOf[T], specOpt)
      }
    }
  }
}
