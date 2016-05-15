package com.tothferenc.templateFX.examples.todo

import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input._
import javafx.scene.layout._

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._

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

final case class HighlightEh(reactor: Reactor[Intent], key: Long) extends EventHandler[MouseEvent] {
  override def handle(event: MouseEvent): Unit =
    reactor handle Highlight(key)
}

final case class RemoveHighlightEH(reactor: Reactor[Intent], key: Long) extends EventHandler[MouseEvent] {
  override def handle(event: MouseEvent): Unit =
    reactor handle RemoveHighlight(key)
}

final case class DragDetectedEh(key: Long) extends EventHandler[MouseEvent] {
  override def handle(event: MouseEvent): Unit = {

    val eventTarget = event.getTarget.asInstanceOf[Node]

    /* drag was detected, start a drag-and-drop gesture*/
    /* allow any transfer mode */
    val dragBoard = eventTarget.startDragAndDrop(TransferMode.MOVE)

    /* Put a string on a dragboard */
    val clipboardContent = new ClipboardContent()
    clipboardContent.putString(key.toString)
    dragBoard.setContent(clipboardContent)

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

    /* data dropped */
    /* if there is a string data on dragboard, read it and use it */
    val dragBoard = event.getDragboard
    Try(dragBoard.getString).flatMap(s => Try(s.toLong)).foreach { id =>
      event.setDropCompleted(true)
      reactor handle Move(id, index)

    }
    /* let the source know whether the string was successfully
		 * transferred and used */

    event.consume()
  }
}

object AppView {
  val textConstrainsInGrid: ColumnConstraints = new ColumnConstraints(100, 500, 600, Priority.ALWAYS, HPos.LEFT, true)
  val buttonConstraintsInGrid: ColumnConstraints = new ColumnConstraints(100, 100, 300, Priority.SOMETIMES, HPos.RIGHT, true)
}

class AppView {
  def windowContents(reactor: Reactor[Intent], scene: Scene, items: List[(Long, String)], hovered: Option[Long]) = List(
    branch[VBox]()(
      branch[HBox]()(
        leaf[Label](text ~ "New item name:"),
        leaf[TextField](id ~ "textInput", onActionText ~ InsertEh(reactor, scene)),
        leaf[Button](id ~ "prependButton", text ~ "Prepend this item!", onActionButton ~ PrependEH(reactor, scene)),
        leaf[Button](id ~ "appendButton", text ~ "Append this item!", onActionButton ~ AppendEH(reactor, scene))
      ),
      branch[HBox]()(
        leaf[Label](text ~ "New item position:"),
        leaf[TextField](id ~ "positionInput", onActionText ~ AppendEH(reactor, scene)),
        leaf[Button](id ~ "insertButton", text ~ "Insert this item!", onActionButton ~ InsertEh(reactor, scene))
      )
    ),
    scrollable(Scroll.fitToHeight << true, Scroll.fitToWidth << true, Scroll.hBar ~ ScrollBarPolicy.NEVER, Scroll.vBar ~ ScrollBarPolicy.ALWAYS) {
      if (items.nonEmpty) {
        branchL[GridPane](Grid.columnConstraints ~ List(AppView.textConstrainsInGrid, AppView.buttonConstraintsInGrid), Grid.alignment ~ Pos.TOP_LEFT) {
          unordered {
            items.zipWithIndex.flatMap {
              case ((key, txt), index) =>
                val shownText = if (hovered.contains(key)) txt + " HOVERED" else txt
                List(
                  key -> branch[Pane](Grid.row ~ index, Grid.column ~ 0, Hbox.hGrow ~ Priority.ALWAYS, onDragOver ~ AcceptMove, onDragDetected ~ DragDetectedEh(key), onDragDropped ~ DragDroppedEh(reactor, index))(
                    leaf[Label](text ~ shownText, onMouseEntered ~ HighlightEh(reactor, key), onMouseExited ~ RemoveHighlightEH(reactor, key))
                  ),
                  key + "-button" -> leaf[Button](text ~ "Delete", Grid.row ~ index, Grid.column ~ 1, onActionButton ~ DeleteEh(reactor, key))
                )
            }
          }
        }
      }
      else
        leaf[Label](text ~ "The list is empty. you may add items with the controls.")
    }
  )
}
