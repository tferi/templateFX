package com.tothferenc.templateFX.examples.todo.view

import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.chart.PieChart
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.control._
import javafx.scene.layout._

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.examples.todo._
import com.tothferenc.templateFX.examples.todo.model.Editing
import com.tothferenc.templateFX.examples.todo.model.TodoItem
import com.tothferenc.templateFX.specs.base.Template

object TodoView {
  val checkboxConstraintsInGrid: ColumnConstraints = new ColumnConstraints(10, 50, 100, Priority.SOMETIMES, HPos.RIGHT, true)
  val textConstrainsInGrid: ColumnConstraints = new ColumnConstraints(100, 500, 600, Priority.ALWAYS, HPos.LEFT, true)
  val buttonConstraintsInGrid: ColumnConstraints = new ColumnConstraints(100, 100, 300, Priority.SOMETIMES, HPos.RIGHT, true)
}

class TodoView {
  def windowTemplate(reactor: Reactor[Intent], scene: Scene, items: List[TodoItem], showCompleted: Boolean, editing: Option[Long]): List[Template[Node]] = {
    List(
      controlsTemplate(reactor, scene, showCompleted),
      if (items.nonEmpty) {
        branch[TabPane, Tab](Vbox.vGrow ~ Priority.ALWAYS, tabClosingPolicy ~ TabClosingPolicy.UNAVAILABLE)(
          fixture[Tab, Node](textTab ~ "Items")(itemsTab(reactor, scene, items, showCompleted, editing)),
          fixture[Tab, Node](textTab ~ "Chart")(chartTab(items))
        )
      } else {
        branch[StackPane, Node](Vbox.vGrow ~ Priority.ALWAYS)(
          leaf[Label](Stack.alignment ~ Pos.CENTER, text ~ "The list is empty. you may add items with the controls.")
        )
      }
    )
  }

  def chartTab(items: List[TodoItem]): Template[PieChart] = {
    leaf[PieChart](Chart.title ~ "Completed vs Pending Items", Chart.Pie.data ~ {
      val (completed, pending) = items.partition(_.completed)
      List(new PieChart.Data("Completed", completed.length), new PieChart.Data("Pending", pending.length))
    })
  }

  def itemsTab(reactor: Reactor[Intent], scene: Scene, items: List[TodoItem], showCompleted: Boolean, editing: Option[Long]): Template[ScrollPane] = {


    def textFieldIfEditing(reactor: Reactor[Intent], scene: Scene, editedItemKey: Long, todoItem: TodoItem): Template[Control] = {
      if (todoItem.id == editedItemKey)
        editingTextField(reactor, scene, todoItem.id, todoItem.name)
      else
        itemNameLabel(reactor, todoItem)
    }

    def itemNameLabel(reactor: Reactor[Intent], todoItem: TodoItem): Template[Label] = {
      leaf[Label](text ~ todoItem.name, onMouseClicked ~ TodoClickedEh(reactor, todoItem))
    }

    def editingTextField(reactor: Reactor[Intent], scene: Scene, todoItemId: Long, txt: String): Template[TextField] = {
      leaf[TextField](id ~ "edited-field", inputText onInit txt, onActionText ~ EditInputTextApprovedEh(reactor, scene, todoItemId))
    }

    val shown = if (showCompleted) items.zipWithIndex else items.zipWithIndex.filterNot(_._1.completed)
    fixture[ScrollPane, Node](Scroll.fitToHeight << true, Scroll.fitToWidth << true, Scroll.hBar ~ ScrollBarPolicy.NEVER, Scroll.vBar ~ ScrollBarPolicy.AS_NEEDED) {
      if (shown.nonEmpty) {
        branchL[GridPane](Grid.columnConstraints ~ List(TodoView.checkboxConstraintsInGrid, TodoView.textConstrainsInGrid, TodoView.buttonConstraintsInGrid), Grid.alignment ~ Pos.TOP_LEFT) {
          unordered[String] {
            val renderItemName: TodoItem => Template[Node] = editing match {
              case Some(editedItem) => item => textFieldIfEditing(reactor, scene, editedItem, item)
              case _ => item => itemNameLabel(reactor, item)
            }
            shown.zipWithIndex.flatMap {
              case ((todoItem @ TodoItem(todoItemId, done, txt), originalIndex), indexInView) =>
                List(
                  todoItemId + "-checkbox" -> leaf[CheckBox](selected ~ done, Grid.row ~ indexInView, Grid.column ~ 0, onMouseClicked ~ CompleteItemEh(reactor, todoItemId, !done)),
                  todoItemId.toString -> branch[HBox, Node](Grid.row ~ indexInView, Grid.column ~ 1, Hbox.hGrow ~ Priority.ALWAYS, onDragOver ~ AcceptMove, onDragDetected ~ DragDetectedEh(todoItemId), onDragDropped ~ DragDroppedEh(reactor, originalIndex), styleClasses ~ List(".todo-item"), onMouseEntered ~ SetCursorToHand(scene), onMouseExited ~ SetCursorToDefault(scene))(
                    renderItemName(todoItem)
                  ),
                  todoItemId + "-button" -> leaf[Button](text ~ "Delete", Grid.row ~ indexInView, Grid.column ~ 2, onActionButton ~ DeleteEh(reactor, todoItemId))
                )
            }
          }
        }
      } else {
        leaf[Label](text ~ "Completed items are not shown.")
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