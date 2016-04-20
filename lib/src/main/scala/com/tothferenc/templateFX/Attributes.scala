package com.tothferenc.templateFX

import java.util
import javafx.css.Styleable
import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.Node
import javafx.scene.chart.Chart
import javafx.scene.control._
import javafx.scene.input.{ KeyEvent, MouseEvent }

object Attributes {
  private type SuperHandler[Whatever] = EventHandler[_ >: Whatever]

  case object text extends Attribute[Labeled, String] {
    override def readFrom(src: Labeled): String = src.getText

    override def set(target: Labeled, value: String): Unit = target.setText(value)
  }

  case object title extends Attribute[Chart, String] {
    override def readFrom(src: Chart): String = src.getTitle

    override def set(target: Chart, value: String): Unit = target.setTitle(value)
  }

  case object styleClasses extends Attribute[Styleable, java.util.List[String]] {
    override def readFrom(src: Styleable): util.List[String] = src.getStyleClass

    override def set(target: Styleable, value: util.List[String]): Unit = target.getStyleClass.setAll(value)
  }

  case object inputText extends Attribute[TextInputControl, String] {
    override def readFrom(src: TextInputControl): String = src.getText

    override def set(target: TextInputControl, value: String): Unit = target.setText(value)
  }

  case object onActionText extends Attribute[TextField, EventHandler[ActionEvent]] {
    override def readFrom(src: TextField): EventHandler[ActionEvent] = src.getOnAction

    override def set(target: TextField, value: EventHandler[ActionEvent]): Unit = target.setOnAction(value)
  }

  case object onActionButton extends Attribute[ButtonBase, EventHandler[ActionEvent]] {
    override def readFrom(src: ButtonBase): EventHandler[ActionEvent] = src.getOnAction

    override def set(target: ButtonBase, value: EventHandler[ActionEvent]): Unit = target.setOnAction(value)
  }

  case object onKeyPressed extends Attribute[TextInputControl, SuperHandler[KeyEvent]] {
    override def readFrom(src: TextInputControl): SuperHandler[KeyEvent] = src.getOnKeyPressed

    override def set(target: TextInputControl, value: SuperHandler[KeyEvent]): Unit = target.setOnKeyPressed(value)
  }

  case object id extends Attribute[Node, String] {
    override def readFrom(src: Node): String = src.getId

    override def set(target: Node, value: String): Unit = target.setId(value)
  }

  case object onMouseClick extends Attribute[Node, EventHandler[_ >: MouseEvent]] {
    override def readFrom(src: Node): EventHandler[_ >: MouseEvent] = src.getOnMouseClicked

    override def set(target: Node, value: EventHandler[_ >: MouseEvent]): Unit = target.setOnMouseClicked(value)
  }
}
