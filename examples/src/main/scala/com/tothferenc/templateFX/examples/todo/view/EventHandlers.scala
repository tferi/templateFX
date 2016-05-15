package com.tothferenc.templateFX.examples.todo.view

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.input._

import com.tothferenc.templateFX.examples.todo._

import scala.util.Try

trait TextReader {
  def getText(scene: Scene, inputName: String) = scene.lookup(inputName).asInstanceOf[TextField].getText
}

final case class InsertEh(reactor: Reactor[Intent], scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor handle Insert(getText(scene, "#textInput"), Try(getText(scene, "#positionInput").toInt).getOrElse(0))
}

final case class AppendEH(reactor: Reactor[Intent], scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor handle Append(getText(scene, "#textInput"))
}

final case class PrependEH(reactor: Reactor[Intent], scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor handle Prepend(getText(scene, "#textInput"))
}

final case class DeleteEh(reactor: Reactor[Intent], key: Long) extends EventHandler[ActionEvent] {
  override def handle(event: ActionEvent): Unit =
    reactor handle Delete(key)
}

final case class SetCursorToHand(scene: Scene) extends EventHandler[MouseEvent] {
  override def handle(event: MouseEvent): Unit = scene.setCursor(Cursor.HAND)
}

final case class SetCursorToDefault(scene: Scene) extends EventHandler[MouseEvent] {
  override def handle(event: MouseEvent): Unit = scene.setCursor(Cursor.DEFAULT)
}

final case class DragDetectedEh(key: Long) extends EventHandler[MouseEvent] {
  override def handle(event: MouseEvent): Unit = {
    val clipboardContent = new ClipboardContent()
    clipboardContent.putString(key.toString)
    event.getTarget.asInstanceOf[Node].startDragAndDrop(TransferMode.MOVE).setContent(clipboardContent)

    event.consume()
  }
}

case object AcceptMove extends EventHandler[DragEvent] {
  override def handle(event: DragEvent): Unit = {
    event.acceptTransferModes(TransferMode.MOVE)
    event.consume()
  }
}

final case class DragDroppedEh(reactor: Reactor[Intent], index: Int) extends EventHandler[DragEvent] {
  override def handle(event: DragEvent): Unit = {

    Try(event.getDragboard.getString).flatMap(s => Try(s.toLong)).foreach { id =>
      event.setDropCompleted(true)
      reactor handle Move(id, index)
    }
    event.consume()
  }
}

