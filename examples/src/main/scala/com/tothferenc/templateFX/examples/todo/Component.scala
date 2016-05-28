package com.tothferenc.templateFX.examples.todo

import com.tothferenc.templateFX.examples.todo.model.TodoItem
import com.tothferenc.templateFX.examples.todo.model.TodoModel
import com.typesafe.scalalogging.LazyLogging

class Component(appModel: TodoModel, protoRenderer: Reactor[Intent] => Renderer[TodoModel]) extends Reactor[Intent] with LazyLogging {
  private def nextId = System.currentTimeMillis()

  private val renderer = protoRenderer(this)

  def render(): Unit = renderer.render(appModel)

  override def handle(message: Intent): Unit = {
    message match {
      case Append(item) if item.nonEmpty =>
        appModel.items.append(TodoItem(nextId, false, item))
      case Prepend(item) if item.nonEmpty =>
        appModel.items.prepend(TodoItem(nextId, false, item))
      case Insert(item, position) if item.nonEmpty =>
        val actualPosition = if (position > appModel.items.length) appModel.items.length else position
        appModel.items.insert(actualPosition, TodoItem(nextId, false, item))
      case Delete(key) =>
        indexOfKey(key).foreach(appModel.items.remove)
      case Move(key, targetPosition) =>
        val index = appModel.items.lastIndexWhere(_.id == key)
        if (index > -1) {
          val item = appModel.items(index)
          appModel.items.remove(index)
          appModel.items.insert(targetPosition, item)
        }
      case ToggleCompleted(key, completed) =>
        appModel.items.find(_.id == key).foreach(_.completed = completed)
      case ToggleShowCompleted(show) =>
        appModel.showCompleted = show
      case Edit(editing, EditType.Ongoing) =>
        appModel.editing = Some(editing.key)
      case Edit(editing, EditType.Finished) =>
        appModel.editing = None
        appModel.items.find(_.id == editing.key).foreach(_.name = editing.currText)
    }
    val renderBegin = System.currentTimeMillis()
    renderer.render(appModel)
    logger.debug(s"Reaction to $message took ${System.currentTimeMillis() - renderBegin} ms.")
  }

  private def indexOfKey(key: Long): Option[Int] = {
    val index = appModel.items.indexWhere(_.id == key)
    if (index > -1) Some(index) else None
  }
}
