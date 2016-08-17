package com.tothferenc.templateFX.base

import com.tothferenc.templateFX.Constraint

import scala.reflect.ClassTag

final case class TemplateNode[T](
    constraintsToApply: Seq[Constraint[T]],
    protected val constructorParams: Seq[AnyRef]
)(
    implicit
    classTag: ClassTag[T]
) extends ReflectiveSpec[T] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[T]]

  override def initNodesBelow(instance: T): Unit = ()

  override def reconciliationSteps(other: Any): Option[Iterable[Change]] = {
    reconciliationStepsForThisNode(other)
  }

}
