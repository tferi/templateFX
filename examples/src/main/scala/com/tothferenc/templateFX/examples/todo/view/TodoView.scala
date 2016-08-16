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
      itemsTemplate(reactor, scene, items, showCompleted, editing)
    )
  }

  def itemsTemplate(reactor: Reactor[Intent], scene: Scene, items: List[TodoItem], showCompleted: Boolean, editing: Option[Long]): Template[Region] = {
    if (items.nonEmpty) {
      node[TabPane](
        Vbox.vGrow ~ Priority.ALWAYS,
        tabClosingPolicy ~ TabClosingPolicy.UNAVAILABLE,
        contextMenu ~~ node[ContextMenu](menuItems ~~ List(node[MenuItem](MenuItemAttr.text ~ "Here's a ContextMenu"))),
        tabs ~~ List(
          node[Tab](TabAttr.text ~ "Items", TabAttr.content ~~ itemsTab(reactor, scene, items, showCompleted, editing)),
          node[Tab](TabAttr.text ~ "Chart", TabAttr.content ~~ chartTab(items))
        )
      )
    } else {
      node[StackPane](
        Vbox.vGrow ~ Priority.ALWAYS,
        children ~~ List(node[Label](Stack.alignment ~ Pos.CENTER, text.label ~ "The list is empty. you may add items with the controls."))
      )
    }
  }

  def chartTab(items: List[TodoItem]): Template[PieChart] = {
    node[PieChart](Chart.title ~ "Completed vs Pending Items", Chart.animated ~ false, Chart.Pie.data ~ {
      val (completed, pending) = items.partition(_.completed)
      List(new PieChart.Data("Completed", completed.length), new PieChart.Data("Pending", pending.length))
    })
  }

  def itemsTab(reactor: Reactor[Intent], scene: Scene, items: List[TodoItem], showCompleted: Boolean, editing: Option[Long]): Template[ScrollPane] = {
    val shown = if (showCompleted) items.zipWithIndex else items.zipWithIndex.filterNot(_._1.completed)
    node[ScrollPane](Scroll.fitToHeight onInit true, Scroll.fitToWidth onInit true, Scroll.hBar ~ ScrollBarPolicy.NEVER, Scroll.vBar ~ ScrollBarPolicy.AS_NEEDED, Scroll.content ~~ {
      if (shown.nonEmpty) {
        node[GridPane](
          Grid.columnConstraints ~ List(TodoView.checkboxConstraintsInGrid, TodoView.textConstrainsInGrid, TodoView.buttonConstraintsInGrid),
          Grid.alignment ~ Pos.TOP_LEFT,
          children ~~ unordered {
            val renderItemName: TodoItem => Template[Node] = editing match {
              case Some(editedItemKey) =>
                todoItem =>
                  if (todoItem.id == editedItemKey)
                    node[TextField](id ~ "edited-field", text.inputControl onInit todoItem.name, onActionText ~ EditInputTextApprovedEh(reactor, scene, todoItem.id))
                  else
                    node[Label](text.label ~ todoItem.name, onMouseClicked ~ TodoClickedEh(reactor, todoItem))
              case _ =>
                todoItem =>
                  node[Label](text.label ~ todoItem.name, onMouseClicked ~ TodoClickedEh(reactor, todoItem))
            }
            shown.zipWithIndex.flatMap {
              case ((todoItem @ TodoItem(todoItemId, done, txt), originalIndex), indexInView) =>
                List(
                  todoItemId + "-checkbox" -> node[CheckBox](
                    CheckboxAttr.selected ~ done,
                    Grid.row ~ indexInView,
                    Grid.column ~ 0,
                    onMouseClicked ~ CompleteItemEh(reactor, todoItemId, !done)
                  ),
                  todoItemId.toString -> node[HBox](
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
                  todoItemId + "-button" -> node[Button](
                    text.label ~ "Delete",
                    Grid.row ~ indexInView,
                    Grid.column ~ 2,
                    onActionButton ~ DeleteEh(reactor, todoItemId)
                  )
                )
            }
          }
        )
      } else {
        node[Label](text.label ~ "Completed items are not shown.")
      }
    })
  }

  def controlsTemplate(reactor: Reactor[Intent], scene: Scene, showCompleted: Boolean): Template[VBox] = {
    node[VBox](children ~~ List(
      node[HBox](children ~~ List(
        node[Label](text.label ~ "New item name:"),
        node[TextField](id ~ "textInput", onActionText ~ InsertEh(reactor, scene)),
        node[Button](id ~ "prependButton", text.label ~ "Prepend this item!", onActionButton ~ PrependEH(reactor, scene)),
        node[Button](id ~ "appendButton", text.label ~ "Append this item!", onActionButton ~ AppendEH(reactor, scene))
      )),
      node[HBox](children ~~ List(
        node[Label](text.label ~ "New item position:"),
        node[TextField](id ~ "positionInput", onActionText ~ InsertEh(reactor, scene)),
        node[Button](id ~ "insertButton", text.label ~ "Insert this item!", onActionButton ~ InsertEh(reactor, scene)),
        node[CheckBox](text.label ~ "Show completed", onMouseClicked ~ ToggleShowCompletedEh(reactor, !showCompleted))
      ))
    ))
  }
}