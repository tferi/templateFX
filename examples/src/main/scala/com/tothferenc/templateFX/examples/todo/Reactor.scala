package com.tothferenc.templateFX.examples.todo

abstract class Reactor {
  def handle(message: Any): Unit
}
