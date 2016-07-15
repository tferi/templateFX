package com.tothferenc.templateFX.examples.todo

import javafx.scene.Scene
import javafx.scene.layout.Pane

import com.tothferenc.templateFX.Api._
import com.tothferenc.templateFX.attributes.children
import com.tothferenc.templateFX.base.Fixture
import com.tothferenc.templateFX.examples.todo.model.TodoModel
import com.tothferenc.templateFX.examples.todo.view.TodoView

abstract class Renderer[-Input] {
  def render(input: Input): Unit
}

class ComponentRenderer(scene: Scene, reactor: Reactor[Intent], root: Pane, view: TodoView) extends Renderer[TodoModel] {
  override def render(input: TodoModel): Unit = Fixture(root, children).reconcile(view.windowTemplate(reactor, scene, input.items.toList, input.showCompleted, input.editing))
}
