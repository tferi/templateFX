package com.tothferenc.templateFX

import java.util.{List => JList}

import com.tothferenc.templateFX.base._
import com.tothferenc.templateFX.collection.CollectionSpec
import com.tothferenc.templateFX.collection.OrderedSpecs
import com.tothferenc.templateFX.collection.OrderedSpecsWithIds
import com.tothferenc.templateFX.collection.SpecsWithIds
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.language.implicitConversions
import scala.reflect.ClassTag

object Api {

  implicit def specs2ordered[Container, T: UserDataAccess](specs: List[Template[T]]): CollectionSpec[T] = OrderedSpecs(specs)
  implicit def specs2orderedWithIds[Key, T: UserDataAccess](specs: List[(Key, Template[T])]): CollectionSpec[T] = OrderedSpecsWithIds(specs)
  def unordered[Key, T: UserDataAccess](specs: List[(Key, Template[T])]) = SpecsWithIds(specs)

  implicit class ReconciliationSyntax[T](reconcilableGroup: CollectionSpec[T]) {
    def changes(items: JList[T]): List[Change] = reconcilableGroup.requiredChangesIn(items)
    def reconcile(items: JList[T]): Unit = changes(items).foreach(_.execute())
  }

  implicit class AttributeBinder[FXType, T](attribute: Attribute[FXType, T]) {
    def ~(value: T) = SimpleBinding(attribute, value, maintained = true)
  }

  implicit class ListFixtureBinder[FXType, T](fixture: Attribute[FXType, List[T]]) {
    def ~~(template: Template[List[T]]) = ReconciliationBinding(fixture, template, maintained = true)
  }

  implicit class FixtureBinder[FXType, T](fixture: Attribute[FXType, T]) {
    def ~~(template: Template[T]) = ReconciliationBinding(fixture, template, maintained = true)
  }

  implicit class AttributeEnforcer[FXType, Attr](attribute: SettableFeature[FXType, Attr]) {
    def <<(value: Attr) = Enforcement(attribute, value, maintained = true)
    def onInit(value: Attr) = Enforcement(attribute, value, maintained = false)
  }

  def nodeC[N: ClassTag: UserDataAccess](constructorParams: AnyRef*)(constraints: Constraint[N]*): Template[N] =
    TemplateNode[N](constraints, constructorParams)

  def node[N: ClassTag: UserDataAccess](constraints: Constraint[N]*): Template[N] =
    TemplateNode[N](constraints, Nil)

}