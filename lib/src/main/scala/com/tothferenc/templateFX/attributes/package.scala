package com.tothferenc.templateFX

import java.lang

import com.tothferenc.templateFX.attribute.{ Attribute, SettableFeature }
import javafx.css.Styleable
import javafx.event.{ ActionEvent, EventHandler }
import javafx.scene.Node
import javafx.scene.chart.Chart
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control._
import javafx.scene.input._
import javafx.scene.layout.{ AnchorPane, ColumnConstraints, GridPane }

import com.sun.javafx.geom.BaseBounds
import com.sun.javafx.geom.transform.BaseTransform
import com.sun.javafx.jmx.{ MXNodeAlgorithm, MXNodeAlgorithmContext }
import com.sun.javafx.sg.prism.NGNode

import scala.collection.convert.wrapAsScala._
import scala.collection.mutable

package object attributes {

  private type SuperHandler[Whatever] = EventHandler[_ >: Whatever]

  val text = Attribute.simple[Labeled, String]("Text")

  val title = Attribute.simple[Chart, String]("Title")

  final case class user(key: String) extends Attribute[Node, Any] {
    override def read(src: Node): Any = UserData.get[Any](src, key).orNull

    override def set(target: Node, value: Any): Unit = UserData.set(target, key, value)

    override def remove(target: Node): Unit = target.asInstanceOf[mutable.Map[String, Any]].remove(key)
  }

  val styleClasses = Attribute.list[Styleable, String]("StyleClass")

  val inputText = Attribute.simple[TextInputControl, String]("Text")

  val onActionText = Attribute.simple[TextField, EventHandler[ActionEvent]]("OnAction")

  val onActionButton = Attribute.simple[ButtonBase, EventHandler[ActionEvent]]("OnAction")

  val id = Attribute.simple[Node, String]("Id")

  val style = Attribute.simple[Node, String]("Style")

  val onMouseClicked = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseClicked")

  val onMouseEntered = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseEntered")

  val onMouseExited = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseExited")

  val onMouseMoved = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseMoved")

  val onMousePressed = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMousePressed")

  val onMouseReleased = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseReleased")

  val onDragDetected = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnDragDetected")

  val onDragDone = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragDone")

  val onDragDropped = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragDropped")

  val onDragExited = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragExited")

  val onDragEntered = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragEntered")

  val onDragOver = Attribute.simple[Node, SuperHandler[DragEvent]]("OnDragOver")

  val onMouseDragged = Attribute.simple[Node, SuperHandler[MouseEvent]]("OnMouseDragged")

  val onMouseDragReleased = Attribute.simple[Node, SuperHandler[MouseDragEvent]]("OnMouseDragReleased")

  val onMouseDragExited = Attribute.simple[Node, SuperHandler[MouseDragEvent]]("OnMouseDragExited")

  val onMouseDragEntered = Attribute.simple[Node, SuperHandler[MouseDragEvent]]("OnMouseDragEntered")

  val onMouseDragOver = Attribute.simple[Node, SuperHandler[MouseDragEvent]]("OnMouseDragOver")

  val onContextMenuRequested = Attribute.simple[Node, SuperHandler[ContextMenuEvent]]("OnContextMenuRequested")

  val onInputMethodTextChanged = Attribute.simple[Node, SuperHandler[InputMethodEvent]]("OnInputMethodTextChanged")

  val onKeyPressed = Attribute.simple[TextInputControl, SuperHandler[KeyEvent]]("OnKeyPressed")

  val onKeyReleased = Attribute.simple[Node, SuperHandler[KeyEvent]]("OnKeyReleased")

  val onKeyTyped = Attribute.simple[Node, SuperHandler[KeyEvent]]("OnKeyTyped")

  val onRotate = Attribute.simple[Node, SuperHandler[RotateEvent]]("OnRotate")

  val onRotationStarted = Attribute.simple[Node, SuperHandler[RotateEvent]]("OnRotationStarted")

  val onRotateionFinished = Attribute.simple[Node, SuperHandler[RotateEvent]]("OnRotationFinished")

  val onScroll = Attribute.simple[Node, SuperHandler[ScrollEvent]]("OnScroll")

  val onScrollStarted = Attribute.simple[Node, SuperHandler[ScrollEvent]]("OnScrollStarted")

  val onScrollFinished = Attribute.simple[Node, SuperHandler[ScrollEvent]]("OnScrollFinished")

  val onZoom = Attribute.simple[Node, SuperHandler[ZoomEvent]]("OnZoom")

  val onZoomStarted = Attribute.simple[Node, SuperHandler[ZoomEvent]]("OnZoomStarted")

  val onZoomFinished = Attribute.simple[Node, SuperHandler[ZoomEvent]]("OnZoomFinished")

  val onSwipeUp = Attribute.simple[Node, SuperHandler[SwipeEvent]]("OnSwipeUp")

  val onSwipeRight = Attribute.simple[Node, SuperHandler[SwipeEvent]]("OnSwipeRight")

  val onSwipeDown = Attribute.simple[Node, SuperHandler[SwipeEvent]]("OnSwipeDown")

  val onSwipeLeft = Attribute.simple[Node, SuperHandler[SwipeEvent]]("OnSwipeLeft")

  val onTouchMoved = Attribute.simple[Node, SuperHandler[TouchEvent]]("OnTouchMoved")

  val onTouchPressed = Attribute.simple[Node, SuperHandler[TouchEvent]]("OnTouchPressed")

  val onTouchReleased = Attribute.simple[Node, SuperHandler[TouchEvent]]("OnTouchReleased")

  val onTouchStationary = Attribute.simple[Node, SuperHandler[TouchEvent]]("OnTouchStationary")
}
