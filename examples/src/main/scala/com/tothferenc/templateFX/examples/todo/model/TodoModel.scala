package com.tothferenc.templateFX.examples.todo.model

import scala.collection.mutable.ArrayBuffer

final case class TodoModel(
	                          items: ArrayBuffer[TodoItem],
                           var showCompleted: Boolean,
	                          var editing: Option[Editing])
