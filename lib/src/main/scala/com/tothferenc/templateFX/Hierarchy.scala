package com.tothferenc.templateFX

import java.lang.reflect.Constructor
import javafx.scene.Node

import com.tothferenc.templateFX.attribute.{Attribute, RemovableFeature}

import scala.collection.mutable.ListBuffer
import scala.reflect._

class NoConstructorForParams(clazz: Class[_], params: Seq[Any])
  extends Exception(s"No ${clazz.getSimpleName} constructor was found for parameters: ${params.mkString(", ")}")

final case class Hierarchy[FXType <: Node](
    constraints: Seq[Constraint[FXType]],
    children: ChildrenSpec
)(constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends Spec[FXType] {

  private val clazz = classTag.runtimeClass

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
    val setting = Mutation(instance, constraints.flatMap(_(instance)), Nil)
    setting.execute()
    instance match {
      case container: TFXParent =>
        container.getChildren.addAll(children.materializeAll(): _*)
      case _ =>
        ()
    }
    instance
  }

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    if (node.getClass == clazz) {
      val nodeAsExpectedType: FXType = node.asInstanceOf[FXType]
      val featuresToRemove = UserData.get[ListBuffer[RemovableFeature[Node]]](node, Attribute.key) match {
        case Some(features) =>
          features.filterNot { checked =>
            constraints.exists(_.attribute == checked)
          }
        case _ =>
          Nil
      }

      val setters = constraints.flatMap(_.apply(nodeAsExpectedType))
      val mutation = if (setters.nonEmpty || featuresToRemove.nonEmpty)
        List(Mutation[FXType](nodeAsExpectedType, constraints.flatMap(_.apply(nodeAsExpectedType)), featuresToRemove))
      else
        Nil
      mutation ::: {
        node match {
          case container: TFXParent =>
            children.requiredChangesIn(container)
          case leaf =>
            Nil
        }
      }
    } else {
      List(Replace(container, this, position))
    }
  }
}

