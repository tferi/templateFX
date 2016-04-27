package com.tothferenc.templateFX

import java.util
import javafx.css.Styleable
import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.Node
import javafx.scene.chart.Chart
import javafx.scene.control._
import javafx.scene.input.{ KeyEvent, MouseEvent }

import scala.collection.convert.wrapAsScala._

object Attributes {
  private type SuperHandler[Whatever] = EventHandler[_ >: Whatever]

  case object text extends Attribute[Labeled, String] {
    override def read(src: Labeled): String = src.getText

    override def unset(target: Labeled): Unit = target.setText(null)

    override def set(target: Labeled, value: String): Unit = target.setText(value)
  }

  case object title extends Attribute[Chart, String] {
    override def read(src: Chart): String = src.getTitle

    override def unset(target: Chart): Unit = target.setTitle(null)

    override def set(target: Chart, value: String): Unit = target.setTitle(value)
  }

  case object styleClasses extends Attribute[Styleable, List[String]] {
    override def read(src: Styleable): List[String] = src.getStyleClass.toList

    override def unset(target: Styleable): Unit = target.getStyleClass.clear()

    override def set(target: Styleable, value: List[String]): Unit = target.getStyleClass.setAll(value: _*)
  }

  case object inputText extends Attribute[TextInputControl, String] {
    override def read(src: TextInputControl): String = src.getText

    override def unset(target: TextInputControl): Unit = target.setText(null)

    override def set(target: TextInputControl, value: String): Unit = target.setText(value)
  }

  case object onActionText extends Attribute[TextField, EventHandler[ActionEvent]] {
    override def read(src: TextField): EventHandler[ActionEvent] = src.getOnAction

    override def unset(target: TextField): Unit = target.setOnAction(null)

    override def set(target: TextField, value: EventHandler[ActionEvent]): Unit = target.setOnAction(value)
  }

  case object onActionButton extends Attribute[ButtonBase, EventHandler[ActionEvent]] {
    override def read(src: ButtonBase): EventHandler[ActionEvent] = src.getOnAction

    override def unset(target: ButtonBase): Unit = target.setOnAction(null)

    override def set(target: ButtonBase, value: EventHandler[ActionEvent]): Unit = target.setOnAction(value)
  }

  case object onKeyPressed extends Attribute[TextInputControl, SuperHandler[KeyEvent]] {
    override def read(src: TextInputControl): SuperHandler[KeyEvent] = src.getOnKeyPressed

    override def unset(target: TextInputControl): Unit = target.setOnKeyPressed(null)

    override def set(target: TextInputControl, value: SuperHandler[KeyEvent]): Unit = target.setOnKeyPressed(value)
  }

  case object id extends Attribute[Node, String] {
    override def unset(target: Node): Unit = target.setId(null)

    override def read(src: Node): String = src.getId

    override def set(target: Node, value: String): Unit = target.setId(value)
  }

  case object onMouseClick extends Attribute[Node, EventHandler[_ >: MouseEvent]] {
    override def unset(target: Node): Unit = target.setOnMouseClicked(null)

    override def read(src: Node): EventHandler[_ >: MouseEvent] = src.getOnMouseClicked

    override def set(target: Node, value: EventHandler[_ >: MouseEvent]): Unit = target.setOnMouseClicked(value)
  }
}
