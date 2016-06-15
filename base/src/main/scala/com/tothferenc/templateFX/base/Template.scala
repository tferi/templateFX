package com.tothferenc.templateFX.base

abstract class Template[+T] {

  /**
   * @return A new [[T]] instance.
   */
  def build(): T

  /**
   *
   * @param target The object we'd like to conform to this template.
   * @return None if the template can't reconcile this item (it's not an instance of [[T]]),
   *         otherwise Some[List] of [[Change]]s which need to be executed to make the parameter conform to this [[Template]].
   */
  def reconciliationSteps(target: Any): Option[List[Change]]
}
