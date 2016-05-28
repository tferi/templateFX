package com.tothferenc.templateFX.examples.todo

sealed abstract class Intent extends Product with Serializable
final case class Append(text: String) extends Intent
final case class Prepend(text: String) extends Intent
final case class Insert(text: String, position: Int) extends Intent
final case class Delete(id: Long) extends Intent
final case class Move(id: Long, position: Int) extends Intent
final case class ToggleCompleted(id: Long, completed: Boolean) extends Intent
final case class ToggleShowCompleted(show: Boolean) extends Intent
final case class Editing(id: Long) extends Intent
final case class EditFinished(id: Long, text: String) extends Intent
