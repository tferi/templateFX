package com.tothferenc.templateFX.examples.todo

import javafx.event.{ EventHandler, ActionEvent }
import javafx.scene.Scene
import javafx.scene.control.{ Button, TextField, Label }
import javafx.scene.layout.{ VBox, HBox, StackPane }

import com.tothferenc.templateFX.Attributes._
import com.tothferenc.templateFX.Spec
import com.tothferenc.templateFX.Api._

import scala.util.Try

trait TextReader {
  def getText(scene: Scene, inputName: String) = scene.lookup(inputName).asInstanceOf[TextField].getText
}

final case class InsertEh(reactor: Reactor, scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor ! Insert(getText(scene, "#textInput"), Try(getText(scene, "#positionInput").toInt).getOrElse(0))
}

final case class AppendEH(reactor: Reactor, scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor ! Append(getText(scene, "#textInput"))
}

final case class PrependEH(reactor: Reactor, scene: Scene) extends EventHandler[ActionEvent] with TextReader {
  override def handle(event: ActionEvent): Unit =
    reactor ! Prepend(getText(scene, "#textInput"))
}

object View {
  def windowContents(reactor: Reactor, scene: Scene, items: List[(Long, String)]) = List(
    branchL[VBox]() {
      items.map { case (k, s) => k -> leaf[Label](text <~ s) }
    },
    branch[HBox]()(
      leaf[Label](text <~ "New item name:"),
      leaf[TextField](
        id <~ "textInput",
        onActionText <~ InsertEh(reactor, scene)
      )
    ),
    leaf[Button](
      id <~ "prependButton",
      text <~ "Prepend this item!",
      onActionButton <~ PrependEH(reactor, scene)
    ),
    leaf[Button](
      id <~ "appendButton",
      text <~ "Append this item!",
      onActionButton <~ AppendEH(reactor, scene)
    ),
    branch[HBox]()(
      leaf[Label](text <~ "New item position:"),
      leaf[TextField](
        id <~ "positionInput",
        onActionText <~ AppendEH(reactor, scene)
      )
    ),
    leaf[Button](
      id <~ "insertButton",
      text <~ "Insert this item!",
      onActionButton <~ InsertEh(reactor, scene)
    )
  )
}
