package com.tothferenc.templateFX.base

import com.tothferenc.templateFX.change.Change

abstract class ClassAwareSpec[T] extends ConstraintBasedReconciliation[T] {

  def specifiedClass: Class[T]

  protected def reconciliationStepsForThisNode(other: Any): Option[Iterable[Change]] = {
    other match {
      case expected: T @unchecked if specifiedClass == expected.getClass =>
        Some(requiredChangesIn(expected))
      case _ =>
        None
    }
  }
}
