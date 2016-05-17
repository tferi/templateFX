package com.tothferenc.templateFX.specs

import javafx.scene.Node

import com.tothferenc.templateFX._

import scala.reflect._

final case class Hierarchy[FXType <: Node](
    constraints: Seq[Constraint[FXType]],
    children: ChildrenSpec
)(protected val constructorParams: Any*)(implicit classTag: ClassTag[FXType]) extends ReflectiveSpec[FXType] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[FXType]]

  override def initNodesBelow(instance: FXType): Unit = instance match {
    case container: TFXParent =>
      container.getChildren.addAll(children.materializeAll(): _*)
    case _ =>
      ()
  }

  override def mutationsIfTypeMatches(otherItem: Node): Option[List[Change]] = {
    super.mutationsIfTypeMatches(otherItem).map {
      _ ::: (otherItem match {
        case container: TFXParent =>
          children.requiredChangesIn(container)
        case leaf =>
          Nil
      })
    }

  }

  def reconcileWithNode(container: TFXParent, position: Int, node: Node): List[Change] = {
    mutationsIfTypeMatches(node).getOrElse(List(Replace(container, this, position)))
  }
}

