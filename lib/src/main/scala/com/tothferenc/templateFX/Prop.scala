package com.tothferenc.templateFX

import java.lang.reflect.Constructor
import javafx.scene.Node
import javafx.scene.layout.Pane

import scala.reflect._

class NoConstructorForParams(clazz: Class[_], params: Seq[Any])
  extends Exception(s"No ${clazz.getSimpleName} constructor was found for parameters: ${params.mkString(", ")}")

abstract class Spec[FXType <: Node] {
  def constraints: Seq[Constraint[FXType]]
  def materialize(): FXType
  def children: ChildrenSpecification
  def clazz: Class[_]

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    if (node.getClass == clazz) {
      node match {
        case container: TFXParent =>
          constraints.flatMap(_.apply(container.asInstanceOf[FXType])).toList ::: children.requiredChangesIn(container)
        case leaf =>
          constraints.flatMap(_.apply(leaf.asInstanceOf[FXType])).toList
      }
    } else {
      List(Replace(container, this, position))
    }
  }
}

final case class Definition[FXType <: Node](
    val constraints: Seq[Constraint[FXType]],
    val children: ChildrenSpecification
)(constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends Spec[FXType] {

  val clazz = classTag.runtimeClass

  private def instantiate(): FXType = {
    val constructors: Array[Constructor[_]] = clazz.getConstructors
    val constructor: Constructor[FXType] =
      constructors.find { constructor =>
        constructor.getParameterCount == constructorParams.length && constructorParams.zipWithIndex.forall {
          case (param, index) => param.getClass == constructor.getParameterTypes()(index)
        }
      }.getOrElse(throw new NoConstructorForParams(clazz, constructorParams)).asInstanceOf[Constructor[FXType]]
    constructor.newInstance()
  }

  def materialize(): FXType = {
    val instance = instantiate()
    val changes: Seq[Change] = constraints.flatMap(_.apply(instance))
    changes.foreach(_.execute())
    instance match {
      case container: TFXParent =>
        container.getChildren.addAll(children.materializeAll(): _*)
      case _ =>
        ()
    }
    instance
  }
}

