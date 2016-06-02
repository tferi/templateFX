package com.tothferenc.templateFX.specs

import com.tothferenc.templateFX._
import com.tothferenc.templateFX.base.Change
import com.tothferenc.templateFX.specs.base.ReflectiveSpec
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.reflect._
import scala.collection.convert.decorateAsJava._

final case class Hierarchy[Parent, Children](
    constraints: Seq[Constraint[Parent]],
    children: CollectionSpec[Parent, Children],
    protected val constructorParams: Seq[Any]
)(
    implicit
    classTag: ClassTag[Parent],
    protected val userDataAccess: UserDataAccess[Parent],
    collectionAccess: CollectionAccess[Parent, Children]
) extends ReflectiveSpec[Parent] {

  implicit val specifiedClass = classTag.runtimeClass.asInstanceOf[Class[Parent]]

  override def initNodesBelow(instance: Parent): Unit = collectionAccess.getCollection(instance).addAll(children.build().asJavaCollection)

  override def reconcilationSteps(other: Any): Option[List[Change]] = {
    reconcilationStepsForThisNode(other).map {
      _ ::: children.requiredChangesIn(collectionAccess.getCollection(other.asInstanceOf[Parent]))
    }
  }

}
