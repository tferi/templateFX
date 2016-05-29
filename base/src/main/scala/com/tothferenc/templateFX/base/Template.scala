package com.tothferenc.templateFX.base

abstract class Template[+T] {
  def build(): T
  def reconcilationSteps(other: Any): Option[List[Change]]
}
