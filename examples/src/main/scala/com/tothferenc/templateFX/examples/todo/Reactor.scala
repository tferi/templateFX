package com.tothferenc.templateFX.examples.todo

abstract class Reactor[-Message] {
  def handle(message: Message): Unit
}
