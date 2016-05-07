package com.tothferenc.templateFX.examples.todo

import com.typesafe.scalalogging.LazyLogging

class Component(appModel: AppModel, protoRenderer: Reactor => Renderer[AppModel]) extends Reactor with LazyLogging {
  private def nextId = System.currentTimeMillis()

  private val renderer = protoRenderer(this)

  def render(): Unit = renderer.render(appModel)

  override def handle(message: Any): Unit = {
    val begin = System.currentTimeMillis()
    message match {
      case Append(item) if item.nonEmpty =>
        appModel.items.append(nextId -> item)
      case Prepend(item) if item.nonEmpty =>
        appModel.items.prepend(nextId -> item)
      case Insert(item, position) if item.nonEmpty =>
        val actualPosition = if (position > appModel.items.length) appModel.items.length else position
        appModel.items.insert(actualPosition, nextId -> item)
      case Delete(key) =>
        val index = appModel.items.indexWhere(_._1 == key)
        if (index > -1) appModel.items.remove(index)
      case _ =>
        ()
    }
    renderer.render(appModel)
    logger.debug(s"Reaction to $message took ${System.currentTimeMillis() - begin} ms.")
  }
}
