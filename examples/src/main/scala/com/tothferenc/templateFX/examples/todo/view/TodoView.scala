package com.tothferenc.templateFX.examples.todo.view

import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control.TextField
import javafx.scene.layout._

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.examples.todo._

object TodoView {
  val textConstrainsInGrid: ColumnConstraints = new ColumnConstraints(100, 500, 600, Priority.ALWAYS, HPos.LEFT, true)
  val buttonConstraintsInGrid: ColumnConstraints = new ColumnConstraints(100, 100, 300, Priority.SOMETIMES, HPos.RIGHT, true)
}

class TodoView {
  def windowContents(reactor: Reactor[Intent], scene: Scene, items: List[TodoItem]) = {
    List(
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
          branchL[GridPane](Grid.columnConstraints ~ List(TodoView.textConstrainsInGrid, TodoView.buttonConstraintsInGrid), Grid.alignment ~ Pos.TOP_LEFT) {
            unordered {
              items.zipWithIndex.flatMap {
                case (TodoItem(todoItemId, done, txt), index) =>
                  List(
                    todoItemId -> branch[HBox](Grid.row ~ index, Grid.column ~ 0, Hbox.hGrow ~ Priority.ALWAYS, onDragOver ~ AcceptMove, onDragDetected ~ DragDetectedEh(todoItemId), onDragDropped ~ DragDroppedEh(reactor, index), styleClasses ~ List(".todo-item"), onMouseEntered ~ SetCursorToHand(scene), onMouseExited ~ SetCursorToDefault(scene))(
                      leaf[Label](text ~ txt)
                    ),
                    todoItemId + "-button" -> leaf[Button](text ~ "Delete", Grid.row ~ index, Grid.column ~ 1, onActionButton ~ DeleteEh(reactor, todoItemId))
                  )
              }
            }
          }
        } else
          leaf[Label](text ~ "The list is empty. you may add items with the controls.")
      }
    )
  }
}