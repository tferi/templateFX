package com.tothferenc.templateFX

import java.lang

import com.tothferenc.templateFX.attribute.{Attribute, SettableFeature}
import javafx.css.Styleable
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.chart.Chart
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.control._
import javafx.scene.input._
import javafx.scene.layout.{AnchorPane, ColumnConstraints, GridPane}
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment

import com.sun.javafx.geom.BaseBounds
import com.sun.javafx.geom.transform.BaseTransform
import com.sun.javafx.jmx.{MXNodeAlgorithm, MXNodeAlgorithmContext}
import com.sun.javafx.sg.prism.NGNode
import com.tothferenc.templateFX.userdata.UserData

import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

package object attributes {

  private type SuperHandler[Whatever] = EventHandler[_ >: Whatever]

  val selected = Attribute.simple[CheckBox, Boolean]("Selected", false)

  val indeterminate = Attribute.simple[CheckBox, Boolean]("Indeterminate", false)

  val text = Attribute.simple[Labeled, String]("Text", null)

  val alignment = Attribute.simple[Labeled, Pos]("Alignment", null)

  val textAlignment = Attribute.simple[Labeled, TextAlignment]("TextAlignment", null)

  val textOverrun = Attribute.simple[Labeled, OverrunStyle]("TextOverrun", null)

  val ellipsisString = Attribute.simple[Labeled, String]("EllipsisString", null)

  val wrapText = Attribute.simple[Labeled, Boolean]("WrapText", false)

  val graphic = Attribute.simple[Labeled, Node]("Graphic", null)

  val underline = Attribute.simple[Labeled, Boolean]("Underline", false)

  val lineSpacing = Attribute.simple[Labeled, Double]("LineSpacing", 0.0)

  val contentDisplay = Attribute.simple[Labeled, ContentDisplay]("ContentDisplay", null)

  val graphicTextGap = Attribute.simple[Labeled, Double]("GraphicTextGap", 4.0)

  val mnemonicParsing = Attribute.simple[Labeled, Boolean]("MnemonicParsing", false)

  val textFill = Attribute.simple[Labeled, Paint]("TextFill", null)

  val font = Attribute.simple[Labeled, Font]("Font", null)

  val title = Attribute.simple[Chart, String]("Title", null)

  final case class user(key: String) extends Attribute[Node, Any] {
    override def read(src: Node): Any = UserData.get[Node, Any](src, key).orNull

    override def set(target: Node, value: Any): Unit = UserData.set(target, key, value)

    override def remove(target: Node): Unit = target.asInstanceOf[mutable.Map[String, Any]].remove(key)
  }

  val styleClasses = Attribute.list[Styleable, String]("StyleClass")

  val inputText = Attribute.simple[TextInputControl, String]("Text", null)

  val onActionText = Attribute.simple[TextField, EventHandler[ActionEvent]]("OnAction", null)

  val onActionButton = Attribute.simple[ButtonBase, EventHandler[ActionEvent]]("OnAction", null)

  val id = Attribute.simple[Node, String]("Id", null)

  val style = Attribute.simple[Node, String]("Style", null)

  val onMouseClicked = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseClicked", null)

  val onMouseEntered = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseEntered", null)

  val onMouseExited = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseExited", null)

  val onMouseMoved = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseMoved", null)

  val onMousePressed = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMousePressed", null)

  val onMouseReleased = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseReleased", null)

  val onDragDetected = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnDragDetected", null)

  val onDragDone = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragDone", null)

  val onDragDropped = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragDropped", null)

  val onDragExited = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragExited", null)

  val onDragEntered = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragEntered", null)

  val onDragOver = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragOver", null)

  val onMouseDragged = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseDragged", null)

  val onMouseDragReleased = Attribute.simple[Node, SuperHandler[MouseDragEvent]]("OnMouseDragReleased", null)

  val onMouseDragExited = Attribute.simple[Node, SuperHandler[MouseDragEvent]]("OnMouseDragExited", null)

  val onMouseDragEntered = Attribute.simple[Node, SuperHandler[MouseDragEvent]]("OnMouseDragEntered", null)

  val onMouseDragOver = Attribute.simple[Node, SuperHandler[MouseDragEvent]]("OnMouseDragOver", null)

  val onContextMenuRequested = Attribute.simple[Node, SuperHandler[ContextMenuEvent]]("OnContextMenuRequested", null)

  val onInputMethodTextChanged = Attribute.simple[Node, SuperHandler[InputMethodEvent]]("OnInputMethodTextChanged", null)

  val onKeyPressed = Attribute.simple[TextInputControl, SuperHandler[KeyEvent]]("OnKeyPressed", null)

  val onKeyReleased = Attribute.simple[Node, SuperHandler[KeyEvent]]("OnKeyReleased", null)

  val onKeyTyped = Attribute.simple[Node, SuperHandler[KeyEvent]]("OnKeyTyped", null)

  val onRotate = Attribute.simple[Node, SuperHandler[RotateEvent]]("OnRotate", null)

  val onRotationStarted = Attribute.simple[Node, SuperHandler[RotateEvent]]("OnRotationStarted", null)

  val onRotateionFinished = Attribute.simple[Node, SuperHandler[RotateEvent]]("OnRotationFinished", null)

  val onScroll = Attribute.simple[Node, SuperHandler[ScrollEvent]]("OnScroll", null)

  val onScrollStarted = Attribute.simple[Node, SuperHandler[ScrollEvent]]("OnScrollStarted", null)

  val onScrollFinished = Attribute.simple[Node, SuperHandler[ScrollEvent]]("OnScrollFinished", null)

  val onZoom = Attribute.simple[Node, SuperHandler[ZoomEvent]]("OnZoom", null)

  val onZoomStarted = Attribute.simple[Node, SuperHandler[ZoomEvent]]("OnZoomStarted", null)

  val onZoomFinished = Attribute.simple[Node, SuperHandler[ZoomEvent]]("OnZoomFinished", null)

  val onSwipeUp = Attribute.simple[Node, SuperHandler[SwipeEvent]]("OnSwipeUp", null)

  val onSwipeRight = Attribute.simple[Node, SuperHandler[SwipeEvent]]("OnSwipeRight", null)

  val onSwipeDown = Attribute.simple[Node, SuperHandler[SwipeEvent]]("OnSwipeDown", null)

  val onSwipeLeft = Attribute.simple[Node, SuperHandler[SwipeEvent]]("OnSwipeLeft", null)

  val onTouchMoved = Attribute.simple[Node, SuperHandler[TouchEvent]]("OnTouchMoved", null)

  val onTouchPressed = Attribute.simple[Node, SuperHandler[TouchEvent]]("OnTouchPressed", null)

  val onTouchReleased = Attribute.simple[Node, SuperHandler[TouchEvent]]("OnTouchReleased", null)

  val onTouchStationary = Attribute.simple[Node, SuperHandler[TouchEvent]]("OnTouchStationary", null)

  val textTab = Attribute.simple[Tab, String]("Text", null)

  val tabClosingPolicy = Attribute.simple[TabPane, TabClosingPolicy]("TabClosingPolicy", null)
}
