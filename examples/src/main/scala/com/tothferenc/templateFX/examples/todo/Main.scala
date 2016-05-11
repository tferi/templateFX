package com.tothferenc.templateFX.examples.todo

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.{AnchorPane, VBox}
import javafx.stage.Stage

import scala.collection.mutable.ArrayBuffer

final case class AppModel(items: ArrayBuffer[(Long, String)])

class Main extends Application {

  override def start(primaryStage: Stage) {
    primaryStage.setTitle("Sup!")

    val rootNode = new VBox()

    val scene: Scene = new Scene(rootNode, 600, 800)
    primaryStage.setScene(scene)
    primaryStage.show()

    val component = new Component(AppModel(ArrayBuffer.empty), reactor => new ComponentRenderer(scene, reactor, rootNode, new AppView))
    component.render()
  }
}

object Main {
  def main(args: Array[String]) {
    Application.launch(classOf[Main], args: _*)
  }
}