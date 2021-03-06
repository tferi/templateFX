package com.tothferenc.templateFX.base

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.change.Mutation
import com.tothferenc.templateFX.specs.UniversalConstructor

abstract class ReflectiveSpec[T] extends ClassAwareSpec[T] {

  protected def constructorParams: Seq[AnyRef]

  def initNodesBelow(instance: T): Unit

  def build(): T = {
    val instance = UniversalConstructor.instantiate[T](specifiedClass, constructorParams)
    Mutation(instance, constraintsToApply.flatMap(constraint => constraint.enforce(instance)), Nil).execute()
    initNodesBelow(instance)
    instance
  }
}
