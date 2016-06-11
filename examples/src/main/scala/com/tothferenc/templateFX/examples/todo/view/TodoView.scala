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
import com.tothferenc.templateFX.fixtures._
import com.tothferenc.templateFX.attributes._
import com.tothferenc.templateFX.base.Template
import com.tothferenc.templateFX.examples.todo._
import com.tothferenc.templateFX.examples.todo.model.TodoItem

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
        leaf[TabPane](
          Vbox.vGrow ~ Priority.ALWAYS,
          tabClosingPolicy ~ TabClosingPolicy.UNAVAILABLE,
          contextMenu ~~ leaf[ContextMenu](menuItems ~~ List(leaf[MenuItem](textMenuItem ~ "Here's a ContextMenu"))),
          tabs ~~ List(
            fixture[Tab, Node](textTab ~ "Items")(itemsTab(reactor, scene, items, showCompleted, editing)),
            fixture[Tab, Node](textTab ~ "Chart")(chartTab(items))
          )
        )
      } else {
        leaf[StackPane](
          Vbox.vGrow ~ Priority.ALWAYS,
          children ~~ List(leaf[Label](Stack.alignment ~ Pos.CENTER, text ~ "The list is empty. you may add items with the controls."))
        )
      }
    )
  }

  def chartTab(items: List[TodoItem]): Template[PieChart] = {
    leaf[PieChart](Chart.title ~ "Completed vs Pending Items", Chart.animated ~ false, Chart.Pie.data ~ {
      val (completed, pending) = items.partition(_.completed)
      List(new PieChart.Data("Completed", completed.length), new PieChart.Data("Pending", pending.length))
    })
  }

  def itemsTab(reactor: Reactor[Intent], scene: Scene, items: List[TodoItem], showCompleted: Boolean, editing: Option[Long]): Template[ScrollPane] = {
    val shown = if (showCompleted) items.zipWithIndex else items.zipWithIndex.filterNot(_._1.completed)
    leaf[ScrollPane](Scroll.fitToHeight onInit true, Scroll.fitToWidth onInit true, Scroll.hBar ~ ScrollBarPolicy.NEVER, Scroll.vBar ~ ScrollBarPolicy.AS_NEEDED, scrollPaneContent ~~ {
      if (shown.nonEmpty) {
        leaf[GridPane](
          Grid.columnConstraints ~ List(TodoView.checkboxConstraintsInGrid, TodoView.textConstrainsInGrid, TodoView.buttonConstraintsInGrid),
          Grid.alignment ~ Pos.TOP_LEFT,
          children ~~ unordered[String] {
            val renderItemName: TodoItem => Template[Node] = editing match {
              case Some(editedItemKey) =>
                todoItem =>
                  if (todoItem.id == editedItemKey)
                    leaf[TextField](id ~ "edited-field", inputText onInit todoItem.name, onActionText ~ EditInputTextApprovedEh(reactor, scene, todoItem.id))
                  else
                    leaf[Label](text ~ todoItem.name, onMouseClicked ~ TodoClickedEh(reactor, todoItem))
              case _ =>
                todoItem =>
                  leaf[Label](text ~ todoItem.name, onMouseClicked ~ TodoClickedEh(reactor, todoItem))
            }
            shown.zipWithIndex.flatMap {
              case ((todoItem @ TodoItem(todoItemId, done, txt), originalIndex), indexInView) =>
                List(
                  todoItemId + "-checkbox" -> leaf[CheckBox](
                    selected ~ done,
                    Grid.row ~ indexInView,
                    Grid.column ~ 0,
                    onMouseClicked ~ CompleteItemEh(reactor, todoItemId, !done)
                  ),
                  todoItemId.toString -> leaf[HBox](
                    Grid.row ~ indexInView,
                    Grid.column ~ 1,
                    Hbox.hGrow ~ Priority.ALWAYS,
                    onDragOver ~ AcceptMove,
                    onDragDetected ~ DragDetectedEh(todoItemId),
                    onDragDropped ~ DragDroppedEh(reactor, originalIndex),
                    styleClasses ~ List(".todo-item"),
                    onMouseEntered ~ SetCursorToHand(scene),
                    onMouseExited ~ SetCursorToDefault(scene),
                    children ~~ List(renderItemName(todoItem))
                  ),
                  todoItemId + "-button" -> leaf[Button](
                    text ~ "Delete",
                    Grid.row ~ indexInView,
                    Grid.column ~ 2,
                    onActionButton ~ DeleteEh(reactor, todoItemId)
                  )
                )
            }
          }
        )
      } else {
        leaf[Label](text ~ "Completed items are not shown.")
      }
    })
  }

  def controlsTemplate(reactor: Reactor[Intent], scene: Scene, showCompleted: Boolean): Template[VBox] = {
    leaf[VBox](children ~~ List(
      leaf[HBox](children ~~ List(
        leaf[Label](text ~ "New item name:"),
        leaf[TextField](id ~ "textInput", onActionText ~ InsertEh(reactor, scene)),
        leaf[Button](id ~ "prependButton", text ~ "Prepend this item!", onActionButton ~ PrependEH(reactor, scene)),
        leaf[Button](id ~ "appendButton", text ~ "Append this item!", onActionButton ~ AppendEH(reactor, scene))
      )),
      leaf[HBox](children ~~ List(
        leaf[Label](text ~ "New item position:"),
        leaf[TextField](id ~ "positionInput", onActionText ~ InsertEh(reactor, scene)),
        leaf[Button](id ~ "insertButton", text ~ "Insert this item!", onActionButton ~ InsertEh(reactor, scene)),
        leaf[CheckBox](text ~ "Show completed", onMouseClicked ~ ToggleShowCompletedEh(reactor, !showCompleted))
      ))
    ))
  }
}