package com.tothferenc.templateFX.examples.todo

import javafx.scene.Scene
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.Api._

abstract class Renderer[-Input] {
  def render(input: Input): Unit
}

class ComponentRenderer(scene: Scene, reactor: Reactor, root: Pane, view: AppView) extends Renderer[AppModel] {
  override def render(input: AppModel): Unit = view.windowContents(reactor, scene, input.items.toList, input.hovered).reconcile(root)
}
