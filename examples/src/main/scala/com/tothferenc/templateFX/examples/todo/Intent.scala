package com.tothferenc.templateFX.examples.todo

import com.tothferenc.templateFX.examples.todo.model.Editing

sealed abstract class EditType extends Product with Serializable
object EditType {
	case object Ongoing extends EditType
	case object Finished extends EditType
}

sealed abstract class Intent extends Product with Serializable
final case class Append(text: String) extends Intent
final case class Prepend(text: String) extends Intent
final case class Insert(text: String, position: Int) extends Intent
final case class Delete(id: Long) extends Intent
final case class Move(id: Long, position: Int) extends Intent
final case class ToggleCompleted(id: Long, completed: Boolean) extends Intent
final case class ToggleShowCompleted(show: Boolean) extends Intent
final case class Edit(editing: Editing, editType: EditType) extends Intent
