package com.tothferenc.templateFX

import java.lang.reflect.Constructor
import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{ Attribute, Unsettable }

import scala.collection.mutable.ListBuffer
import scala.reflect._

class NoConstructorForParams(clazz: Class[_], params: Seq[Any])
  extends Exception(s"No ${clazz.getSimpleName} constructor was found for parameters: ${params.mkString(", ")}")

final case class Definition[FXType <: Node](
    constraints: Seq[Constraint[FXType]],
    children: ChildrenSpecification
)(constructorParams: Any*)(implicit classTag: ClassTag[FXType]) {

  val clazz = classTag.runtimeClass

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    if (node.getClass == clazz) {
      (UserData.get[ListBuffer[Unsettable[Node]]](node, Attribute.key) match {
        case Some(attributes) =>
          val toUnset: ListBuffer[Unsettable[Node]] = attributes.filterNot { checked =>
            constraints.exists(_.attribute == checked)
          }
          if (toUnset.isEmpty)
            Nil
          else
            List(UnsetAttributes(node, toUnset))
        case _ =>
          Nil
      }) :::
        constraints.flatMap(_.apply(node.asInstanceOf[FXType])).toList :::
        (node match {
          case container: TFXParent =>
            children.requiredChangesIn(container)
          case leaf =>
            Nil
        })
    } else {
      List(Replace(container, this, position))
    }
  }

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
    val changes: Seq[Change] = constraints.flatMap { constraint =>
      constraint.apply(instance)
    }
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

