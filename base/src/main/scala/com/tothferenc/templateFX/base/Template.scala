package com.tothferenc.templateFX.base

import com.tothferenc.templateFX.change.Change

abstract class Template[+T] {

  def build(): T

  /**
   *
   * @param target The object we'd like to conform to this template.
   * @return None if the template can't reconcile this item,
   *         otherwise a List of Changes which need to be executed to make the parameter conform to this instance.
   */
  def reconciliationSteps(target: Any): Option[Iterable[Change]]
}
