package com.tothferenc.templateFX.examples.todo

final case class Append(text: String)
final case class Prepend(text: String)
final case class Insert(text: String, position: Int)
final case class Delete(id: Long)
final case class MouseEntered(id: Long)
final case class MouseExited(id: Long)
