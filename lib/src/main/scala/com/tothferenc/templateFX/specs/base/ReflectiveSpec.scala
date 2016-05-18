package com.tothferenc.templateFX.specs.base

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.specs.UniversalConstructor

abstract class ReflectiveSpec[T] extends ClassAwareSpec[T] {

  protected def constructorParams: Seq[Any]

  def initNodesBelow(instance: T): Unit

  def build(): T = {
    val instance = UniversalConstructor.instantiate[T](constructorParams)
    Mutation(instance, constraints.flatMap(_(instance)), Nil).execute()
    initNodesBelow(instance)
    instance
  }
}
