package com.tothferenc.templateFX

import java.lang
import java.util
import javafx.collections.ObservableList
import javafx.css.Styleable
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.chart.Chart
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.scene.control._
import javafx.scene.input._
import javafx.scene.layout._
import javafx.scene.paint.Paint
import javafx.scene.shape.Shape
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment

import com.tothferenc.templateFX.base.Attribute

import scala.collection.mutable
import scala.collection.convert.wrapAsScala._

package object attributes {

  private type SuperHandler[Whatever] = EventHandler[_ >: Whatever]

  val tabs = new Attribute[TabPane, java.util.List[Tab]] {
    override def read(src: TabPane): util.List[Tab] = src.getTabs

    override def set(target: TabPane, value: util.List[Tab]): Unit = target.getTabs.setAll(value)

    override def remove(item: TabPane): Unit = item.getTabs.clear()
  }

  val menuItems = new Attribute[ContextMenu, java.util.List[MenuItem]] {
    override def read(src: ContextMenu): util.List[MenuItem] = src.getItems

    override def set(target: ContextMenu, value: util.List[MenuItem]): Unit = target.getItems.setAll(value)

    override def remove(item: ContextMenu): Unit = item.getItems.clear()
  }

  object text {
    val label = Attribute.simple[Labeled, String]("Text", null)
    val inputControl = Attribute.simple[TextInputControl, String]("Text", null)
  }

  val alignment = Attribute.simple[Labeled, Pos]("Alignment", null)

  object textAlignment {
    val borderPane = Attribute.remote[BorderPane, Node, Pos]("Alignment")
    val labeled = Attribute.simple[Labeled, TextAlignment]("TextAlignment", null)
  }

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

  object font {
    val labeled = Attribute.simple[Labeled, Font]("Font", null)
    val textInputControl = Attribute.simple[TextInputControl, Font]("Font", null)
  }

  val title = Attribute.simple[Chart, String]("Title", null)

  val styleClasses = Attribute.list[Styleable, String]("StyleClass")

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

  val tabClosingPolicy = Attribute.simple[TabPane, TabClosingPolicy]("TabClosingPolicy", null)

  // Pane

  val children = new Attribute[Pane, java.util.List[Node]] {
    override def read(src: Pane): ObservableList[Node] = src.getChildren

    override def set(target: Pane, value: java.util.List[Node]): Unit = target.getChildren.setAll(value)

    override def remove(item: Pane): Unit = item.getChildren.clear()
  }

  // BorderPane

  val margin = Attribute.remote[BorderPane, Node, Insets]("Margin")

  val top = Attribute.simple[BorderPane, Node]("Top", null)

  val right = Attribute.simple[BorderPane, Node]("Right", null)

  val bottom = Attribute.simple[BorderPane, Node]("Bottom", null)

  val left = Attribute.simple[BorderPane, Node]("Left", null)

  val center = Attribute.simple[BorderPane, Node]("Center", null)

  // Region

  val background = Attribute.simple[Region, Background]("Background", null)

  val border = Attribute.simple[Region, Border]("Border", null)

  val minWidth = Attribute.simple[Region, Double]("MinWidth", 0.0)

  val minHeight = Attribute.simple[Region, Double]("MinHeight", 0.0)

  val prefWidth = Attribute.simple[Region, Double]("PrefWidth", 0.0)

  val prefHeight = Attribute.simple[Region, Double]("PrefHeight", 0.0)

  val maxWidth = Attribute.simple[Region, Double]("MaxWidth", 0.0)

  val maxHeight = Attribute.simple[Region, Double]("MaxHeight", 0.0)

  val cacheShape = Attribute.simple[Region, Boolean]("CacheShape", true)

  val centerShape = Attribute.simple[Region, Boolean]("CenterShape", true)

  val scaleShape = Attribute.simple[Region, Boolean]("ScaleShape", true)

  val padding = Attribute.simple[Region, Insets]("Padding", null)

  val opaqueInsets = Attribute.simple[Region, Insets]("OpaqueInsets", null)

  val snapToPixel = Attribute.simple[Region, Boolean]("SnapToPixel", true)

  val shape = Attribute.simple[Region, Shape]("Shape", null)

  // Control

  val contextMenu = Attribute.simple[Control, ContextMenu]("ContextMenu", null)

  val skin = Attribute.simple[Control, Skin[_]]("Skin", null)

  val tooltip = Attribute.simple[Control, Tooltip]("Tooltip", null)

  // TextInputControl

  val editable = Attribute.simple[TextInputControl, Boolean]("Editable", true)

  val textFormatter = Attribute.simple[TextInputControl, TextFormatter[_]]("TextFormatter", null)
}
