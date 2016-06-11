package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

import com.tothferenc.templateFX.base._
import com.tothferenc.templateFX.collectionaccess._
import com.tothferenc.templateFX.fixtures._
import com.tothferenc.templateFX.specs._
import com.tothferenc.templateFX.specs.collection._
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.language.implicitConversions
import scala.reflect.ClassTag

object Api {

  // Children Accessors
  implicit val paneChildrenAccess = new PaneNodesAccess
  implicit val tabPaneChildrenAccess = new TabPaneTabsAccess
  implicit val contextMenuMenuItemsAccess = new ContextMenuMenuItemsAccess

  implicit def specs2ordered[Container, T: UserDataAccess](specs: List[Template[T]]): CollectionSpec[Container, T] = OrderedSpecs(specs)
  implicit def specs2orderedWithIds[Key](specs: List[(Key, Template[Node])]): CollectionSpec[TFXParent, Node] = OrderedSpecsWithIds(specs)
  def unordered[Key](specs: List[(Key, Template[Node])]) = SpecsWithIds(specs)
  implicit def tabs2ordered(tabs: List[Template[Tab]]): CollectionSpec[TabPane, Tab] = OrderedSpecs(tabs)
  implicit def tuple2ParameterizedFixtures[C, I](tuples: List[(Attribute[C, I], Template[I])]): List[ParameterizedFixture.For[C]] = tuples.map(t => ParameterizedFixture.apply(t._1, Some(t._2)))

  implicit class ReconcilationSyntax[Container](reconcilableGroup: CollectionSpec[Container, Node])(implicit collectionAccess: CollectionAccess[Container, Node]) {
    def changes(container: Container): List[Change] = reconcilableGroup.requiredChangesIn(collectionAccess.getCollection(container))
    def reconcile(container: Container): Unit = changes(container).foreach(_.execute())
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

  def leafC[N: ClassTag: UserDataAccess](constructorParams: Any*)(constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, constructorParams)

  def leaf[N: ClassTag: UserDataAccess](constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, Nil)

  def fixture[F, C](constraints: Constraint[F]*)(content: Template[C])(implicit ct: ClassTag[F], ud: UserDataAccess[F], f: Attribute[F, C]): Template[F] =
    FixtureSpec[F](constraints, List(ParameterizedFixture(f, Some(content))))

  def fixtures[F](constraints: Constraint[F]*)(fixtures: ParameterizedFixture.For[F]*)(implicit ct: ClassTag[F], ud: UserDataAccess[F]): Template[F] =
    FixtureSpec[F](constraints, fixtures.toList)

}
