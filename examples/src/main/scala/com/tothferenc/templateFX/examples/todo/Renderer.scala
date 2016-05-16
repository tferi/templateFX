package com.tothferenc.templateFX.examples.todo

import javafx.scene.Scene
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.examples.todo.view.TodoView

abstract class Renderer[-Input] {
  def render(input: Input): Unit
}

class ComponentRenderer(scene: Scene, reactor: Reactor[Intent], root: Pane, view: TodoView) extends Renderer[TodoModel] {
  override def render(input: TodoModel): Unit = view.windowContents(reactor, scene, input.items.toList).reconcile(root)
}
