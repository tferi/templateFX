package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX.Change
import com.tothferenc.templateFX.Constraint
import com.tothferenc.templateFX.specs.base.ReflectiveSpec
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.reflect.ClassTag

final case class Leaf[T](
    constraints: Seq[Constraint[T]],
    protected val constructorParams: Seq[Any])(
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
