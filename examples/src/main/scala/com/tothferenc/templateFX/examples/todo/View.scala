package com.tothferenc.templateFX.examples.todo

import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.Scene
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control.{ Button, Label, ScrollPane, TextField }
import javafx.scene.input.MouseEvent
import javafx.scene.layout._

import com.tothferenc.templateFX.Attributes.Grid.columnConstraints
import com.tothferenc.templateFX.Attributes._
import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.specs.Spec

import scala.util.Try

trait TextReader {
  def getText(scene: Scene, inputName: String) = scene.lookup(inputName).asInstanceOf[TextField].getText
}

final case class InsertEh(reactor: Reactor, scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor handle Insert(getText(scene, "#textInput"), Try(getText(scene, "#positionInput").toInt).getOrElse(0))
}

final case class AppendEH(reactor: Reactor, scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor handle Append(getText(scene, "#textInput"))
}

final case class PrependEH(reactor: Reactor, scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor handle Prepend(getText(scene, "#textInput"))
}

final case class DeleteEh(reactor: Reactor, key: Long) extends EventHandler[ActionEvent] {
  override def handle(event: ActionEvent): Unit =
    reactor handle Delete(key)
}

final case class MouseEnteredEh(reactor: Reactor, key: Long) extends EventHandler[MouseEvent] {
  override def handle(event: MouseEvent): Unit =
    reactor handle MouseEntered(key)
}

final case class MouseExitedEh(reactor: Reactor, key: Long) extends EventHandler[MouseEvent] {
  override def handle(event: MouseEvent): Unit =
    reactor handle MouseExited(key)
}

class AppView {
  def windowContents(reactor: Reactor, scene: Scene, items: List[(Long, String)], hovered: Option[Long]) = List(
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
      if (items.nonEmpty)
        branchL[GridPane](columnConstraints ~ List(new ColumnConstraints(100, 200, 300), new ColumnConstraints(100, 200, 300))) {
          unordered {
            items.zipWithIndex.flatMap {
              case ((key, txt), index) =>
                val shownText = if (hovered.contains(key)) txt + " HOVERED" else txt
                List(
                  key -> leaf[Label](text ~ shownText, Grid.row ~ index, Grid.column ~ 1, onMouseEntered ~ MouseEnteredEh(reactor, key), onMouseExited ~ MouseExitedEh(reactor, key)),
                  key + "-button" -> leaf[Button](text ~ "Delete", Grid.row ~ index, Grid.column ~ 2, onActionButton ~ DeleteEh(reactor, key))
                )
            }
          }
        }
      else
        leaf[Label](text ~ "The list is empty. you may add items with the controls.")
    }
  )
}
