package com.tothferenc.templateFX.examples.todo

sealed abstract class Intent
final case class Append(text: String)extends Intent
final case class Prepend(text: String)extends Intent
final case class Insert(text: String, position: Int)extends Intent
final case class Delete(id: Long)extends Intent
final case class Highlight(id: Long)extends Intent
final case class RemoveHighlight(id: Long)extends Intent
