package com.tothferenc.templateFX.examples.todo

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.{AnchorPane, VBox, Pane}
import javafx.stage.Stage

import com.typesafe.scalalogging.LazyLogging
import com.tothferenc.templateFX.Api._

import scala.collection.mutable.ArrayBuffer

abstract class Reactor {
  def !(message: Any): Unit
}

class ComponentReactor(scene: Scene, root: Pane) extends Reactor with LazyLogging {
  def nextId = System.currentTimeMillis()

  override def !(message: Any): Unit = {
    logger.debug(s"Reaction to $message.")
    message match {
      case Append(item) if item.nonEmpty =>
        Model.items.append(nextId -> item)
      case Prepend(item) if item.nonEmpty =>
        Model.items.prepend(nextId -> item)
      case Insert(item, position) if item.nonEmpty =>
        val actualPosition = if (position > Model.items.length) Model.items.length else position
        Model.items.insert(actualPosition, nextId -> item)
      case Delete(key) =>
        val index = Model.items.indexWhere(_._1 == key)
        if (index > -1) Model.items.remove(index)
      case _ =>
        ()
    }
    View.windowContents(this, scene, Model.items.toList).reconcile(root)
    logger.debug(s"Reaction to $message executed!")
  }
}

object Model {
  val items: ArrayBuffer[(Long, String)] = ArrayBuffer.empty
}

class Main extends Application {

  override def start(primaryStage: Stage) {
    primaryStage.setTitle("Sup!")

    val root = new AnchorPane()

    val scene: Scene = new Scene(root, 600, 800)
    primaryStage.setScene(scene)
    primaryStage.show()

    val reactor = new ComponentReactor(scene, root)
    View.windowContents(reactor, scene, Model.items.toList).reconcile(root)
  }
}

object Main {
  def main(args: Array[String]) {
    Application.launch(classOf[Main], args: _*)
  }
}