package com.tothferenc.templateFX.specs.base

import com.tothferenc.templateFX.Change

abstract class Template[+T] {
  def build(): T
  def reconcilationSteps(other: Any): Option[List[Change]]
}
