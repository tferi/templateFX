package com.tothferenc.templateFX.examples.todo

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.stage.Stage

import com.tothferenc.templateFX.examples.todo.model.TodoModel
import com.tothferenc.templateFX.examples.todo.view.TodoView

import scala.collection.mutable.ArrayBuffer

class Main extends Application {

  override def start(primaryStage: Stage) {
    primaryStage.setTitle("TemplateFX example application")

    val rootNode = new VBox()

    val scene: Scene = new Scene(rootNode, 600, 800)
    primaryStage.setScene(scene)
    val cssRef: String = com.tothferenc.templateFX.examples.todo.Main.getClass.getResource("todo.css").toExternalForm
    scene.getStylesheets.add(cssRef)

    val component = new Component(TodoModel(ArrayBuffer.empty, false), reactor => new ComponentRenderer(scene, reactor, rootNode, new TodoView))
    component.render()

    primaryStage.show()
  }
}

object Main {
  def main(args: Array[String]) {
    Application.launch(classOf[Main], args: _*)
  }
}