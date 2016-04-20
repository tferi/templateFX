package com.tothferenc.templateFX.examples.todo

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.{ VBox, Pane }
import javafx.stage.Stage

import com.typesafe.scalalogging.LazyLogging
import com.tothferenc.templateFX.Api._

import scala.collection.mutable.ArrayBuffer

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
        Model.items.insert(position, nextId -> item)
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

    val root = new VBox()

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