package com.tothferenc.templateFX.specs.base

import com.tothferenc.templateFX.base.Change

abstract class ClassAwareSpec[T] extends ConstraintBasedReconcilation[T] {

  implicit def specifiedClass: Class[T]

  protected def reconcilationStepsForThisNode(other: Any): Option[List[Change]] = {
    other match {
      case expected: T @unchecked if specifiedClass == expected.getClass =>
        Some(requiredChangesIn(expected))
      case _ =>
        None
    }
  }
}
