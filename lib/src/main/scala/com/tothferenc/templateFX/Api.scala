package com.tothferenc.templateFX

import java.util.{ List => JList }
import javafx.scene.control.Tab

import com.tothferenc.templateFX.base._
import com.tothferenc.templateFX.specs._
import com.tothferenc.templateFX.specs.collection._
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.language.implicitConversions
import scala.reflect.ClassTag

object Api {

  implicit def specs2ordered[Container, T: UserDataAccess](specs: List[Template[T]]): CollectionSpec[T] = OrderedSpecs(specs)
  implicit def specs2orderedWithIds[Key, T: UserDataAccess](specs: List[(Key, Template[T])]): CollectionSpec[T] = OrderedSpecsWithIds(specs)
  def unordered[Key, T: UserDataAccess](specs: List[(Key, Template[T])]) = SpecsWithIds(specs)
  implicit def tuple2ParameterizedFixtures[C, I](tuples: List[(Attribute[C, I], Template[I])]): List[ParameterizedFixture.For[C]] = tuples.map(t => ParameterizedFixture.apply(t._1, Some(t._2)))

  implicit class ReconcilationSyntax[T](reconcilableGroup: CollectionSpec[T]) {
    def changes(items: JList[T]): List[Change] = reconcilableGroup.requiredChangesIn(items)
    def reconcile(items: JList[T]): Unit = changes(items).foreach(_.execute())
  }

  implicit class AttributeBinder[FXType, T](attribute: Attribute[FXType, T]) {
    def ~(value: T) = Binding(attribute, value, maintained = true)
  }

  implicit class ListFixtureBinder[FXType, T](fixture: Attribute[FXType, List[T]]) {
    def ~~(template: Template[List[T]]) = FixtureBinding(fixture, template, maintained = true)
  }

  implicit class FixtureBinder[FXType, T](fixture: Attribute[FXType, T]) {
    def ~~(template: Template[T]) = FixtureBinding(fixture, template, maintained = true)
  }

  implicit class AttributeEnforcer[FXType, Attr](attribute: SettableFeature[FXType, Attr]) {
    def <<(value: Attr) = Enforcement(attribute, value, maintained = true)
    def onInit(value: Attr) = Enforcement(attribute, value, maintained = false)
  }

  def nodeC[N: ClassTag: UserDataAccess](constructorParams: Any*)(constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, constructorParams)

  def node[N: ClassTag: UserDataAccess](constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, Nil)

  def fixture[F, C](constraints: Constraint[F]*)(content: Template[C])(implicit ct: ClassTag[F], ud: UserDataAccess[F], f: Attribute[F, C]): Template[F] =
    FixtureSpec[F](constraints, List(ParameterizedFixture(f, Some(content))))

  def fixtures[F](constraints: Constraint[F]*)(fixtures: ParameterizedFixture.For[F]*)(implicit ct: ClassTag[F], ud: UserDataAccess[F]): Template[F] =
    FixtureSpec[F](constraints, fixtures.toList)

}
