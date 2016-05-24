package com.tothferenc.templateFX.examples.todo.view

import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control._
import javafx.scene.layout._

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.examples.todo._
import com.tothferenc.templateFX.examples.todo.model.TodoItem
import com.tothferenc.templateFX.specs.base.Template

object TodoView {
  val checkboxConstraintsInGrid: ColumnConstraints = new ColumnConstraints(10, 50, 100, Priority.SOMETIMES, HPos.RIGHT, true)
  val textConstrainsInGrid: ColumnConstraints = new ColumnConstraints(100, 500, 600, Priority.ALWAYS, HPos.LEFT, true)
  val buttonConstraintsInGrid: ColumnConstraints = new ColumnConstraints(100, 100, 300, Priority.SOMETIMES, HPos.RIGHT, true)
}

class TodoView {
  def windowTemplate(reactor: Reactor[Intent], scene: Scene, items: List[TodoItem], showCompleted: Boolean): List[Template[Node]] = {
    List(
      controlsTemplate(reactor, scene, showCompleted),
      branch[TabPane, Tab](Vbox.vGrow ~ Priority.ALWAYS)(
        tab(textTab ~ "items")(itemsTab(reactor, scene, items, showCompleted)),
        tab(textTab ~ "dummy")(leaf[Label](text ~ "Hello"))
      )
    )
  }

  def itemsTab(reactor: Reactor[Intent], scene: Scene, items: List[TodoItem], showCompleted: Boolean): Template[ScrollPane] = {
    val shown = if (showCompleted) items else items.filterNot(_.completed)
    scrollable(Scroll.fitToHeight << true, Scroll.fitToWidth << true, Scroll.hBar ~ ScrollBarPolicy.NEVER, Scroll.vBar ~ ScrollBarPolicy.AS_NEEDED) {
      if (shown.nonEmpty) {
        branchL[GridPane](Grid.columnConstraints ~ List(TodoView.checkboxConstraintsInGrid, TodoView.textConstrainsInGrid, TodoView.buttonConstraintsInGrid), Grid.alignment ~ Pos.TOP_LEFT) {
          unordered[String] {
            shown.zipWithIndex.flatMap {
              case (TodoItem(todoItemId, done, txt), index) =>
                List(
                  todoItemId + "-checkbox" -> leaf[CheckBox](selected ~ done, Grid.row ~ index, Grid.column ~ 0, onMouseClicked ~ CompleteItemEh(reactor, todoItemId, !done)),
                  todoItemId.toString -> branch[HBox, Node](Grid.row ~ index, Grid.column ~ 1, Hbox.hGrow ~ Priority.ALWAYS, onDragOver ~ AcceptMove, onDragDetected ~ DragDetectedEh(todoItemId), onDragDropped ~ DragDroppedEh(reactor, index), styleClasses ~ List(".todo-item"), onMouseEntered ~ SetCursorToHand(scene), onMouseExited ~ SetCursorToDefault(scene))(
                    leaf[Label](text ~ txt)
                  ),
                  todoItemId + "-button" -> leaf[Button](text ~ "Delete", Grid.row ~ index, Grid.column ~ 2, onActionButton ~ DeleteEh(reactor, todoItemId))
                )
            }
          }
        }
      } else {
        leaf[Label](text ~ (if (items.isEmpty) "The list is empty. you may add items with the controls." else "Completed items are not shown."))
      }
    }
  }

  def controlsTemplate(reactor: Reactor[Intent], scene: Scene, showCompleted: Boolean): Template[VBox] = {
    branch[VBox, Node]()(
      branch[HBox, Node]()(
        leaf[Label](text ~ "New item name:"),
        leaf[TextField](id ~ "textInput", onActionText ~ InsertEh(reactor, scene)),
        leaf[Button](id ~ "prependButton", text ~ "Prepend this item!", onActionButton ~ PrependEH(reactor, scene)),
        leaf[Button](id ~ "appendButton", text ~ "Append this item!", onActionButton ~ AppendEH(reactor, scene))
      ),
      branch[HBox, Node]()(
        leaf[Label](text ~ "New item position:"),
        leaf[TextField](id ~ "positionInput", onActionText ~ InsertEh(reactor, scene)),
        leaf[Button](id ~ "insertButton", text ~ "Insert this item!", onActionButton ~ InsertEh(reactor, scene)),
        leaf[CheckBox](text ~ "Show completed", onMouseClicked ~ ToggleShowCompletedEh(reactor, !showCompleted))
      )
    )
  }
}