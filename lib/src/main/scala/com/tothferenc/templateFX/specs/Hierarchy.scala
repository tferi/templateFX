package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

import scala.reflect._

final case class Hierarchy[FXType <: Node](
    constraints: Seq[Constraint[FXType]],
    children: ChildrenSpec
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends ReflectiveSpec[FXType] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[FXType]]

  override def addChildren(instance: FXType): Unit = instance match {
    case container: TFXParent =>
      container.getChildren.addAll(children.materializeAll(): _*)
    case _ =>
      ()
  }

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    if (node.getClass == specifiedClass) {
      calculateMutation(node.asInstanceOf[FXType])
    } else {
      List(Replace(container, this, position))
    }
  }
}

