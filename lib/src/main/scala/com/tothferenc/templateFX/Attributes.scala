package com.tothferenc.templateFX

import java.lang
import com.tothferenc.templateFX.attribute.Attribute

import javafx.css.Styleable
import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.Node
import javafx.scene.chart.Chart
import javafx.scene.control._
import javafx.scene.input.{ KeyEvent, MouseEvent }
import javafx.scene.layout.{ AnchorPane, ColumnConstraints, GridPane }

import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

object Attributes {
  private type SuperHandler[Whatever] = EventHandler[_ >: Whatever]

  object Anchor {

    val top = Attribute.remote[AnchorPane, Node, lang.Double]("TopAnchor")

    val bottom = Attribute.remote[AnchorPane, Node, lang.Double]("BottomAnchor")

    val left = Attribute.remote[AnchorPane, Node, lang.Double]("LeftAnchor")

    val right = Attribute.remote[AnchorPane, Node, lang.Double]("RightAnchor")

  }

  object Grid {

    case object columnConstraints extends Attribute[GridPane, List[ColumnConstraints]] {
      override def read(src: GridPane): List[ColumnConstraints] = src.getColumnConstraints.toList

      override def unset(target: GridPane): Unit = target.getStyleClass.clear()

      override def set(target: GridPane, value: List[ColumnConstraints]): Unit = target.getColumnConstraints.setAll(value: _*)
    }

    val column = Attribute.remote[GridPane, Node, lang.Integer]("ColumnIndex")

    val row = Attribute.remote[GridPane, Node, lang.Integer]("RowIndex")
  }

  val text = Attribute.simple[Labeled, String]("Text")

  val title = Attribute.simple[Chart, String]("Title")

  final case class user(key: String) extends Attribute[Node, Any] {
    override def read(src: Node): Any = UserData.get[Any](src, key).orNull

    override def set(target: Node, value: Any): Unit = UserData.set(target, key, value)

    override def unset(target: Node): Unit = target.asInstanceOf[mutable.Map[String, Any]].remove(key)
  }

  case object styleClasses extends Attribute[Styleable, List[String]] {
    override def read(src: Styleable): List[String] = src.getStyleClass.toList

    override def unset(target: Styleable): Unit = target.getStyleClass.clear()

    override def set(target: Styleable, value: List[String]): Unit = target.getStyleClass.setAll(value: _*)
  }

  val inputText = Attribute.simple[TextInputControl, String]("Text")

  val onActionText = Attribute.simple[TextField, EventHandler[ActionEvent]]("OnAction")

  val onActionButton = Attribute.simple[ButtonBase, EventHandler[ActionEvent]]("OnAction")

  val onKeyPressed = Attribute.simple[TextInputControl, SuperHandler[KeyEvent]]("OnKeyPressed")

  val id = Attribute.simple[Node, String]("Id")

  val style = Attribute.simple[Node, String]("Style")

  val onMouseClick = Attribute.simple[Node, EventHandler[_ >: MouseEvent]]("OnMouseClicked")
}
