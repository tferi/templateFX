package com.tothferenc.templateFX.base

import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.reflect.ClassTag

final case class TemplateNode[T](
    constraints: Seq[Constraint[T]],
    protected val constructorParams: Seq[AnyRef]
)(
    implicit
    classTag: ClassTag[T],
    protected val userDataAccess: UserDataAccess[T]
) extends ReflectiveSpec[T] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[T]]

  override def initNodesBelow(instance: T): Unit = ()

  override def reconcilationSteps(other: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(other)
  }

}
