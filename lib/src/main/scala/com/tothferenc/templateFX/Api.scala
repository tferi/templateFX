package com.tothferenc.templateFX

import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane

import com.tothferenc.templateFX.base._
import com.tothferenc.templateFX.collectionaccess._
import com.tothferenc.templateFX.fixtures._
import com.tothferenc.templateFX.specs._
import com.tothferenc.templateFX.userdata.UserDataAccess

import scala.language.implicitConversions
import scala.reflect.ClassTag

object Api {

  // Children Accessors
  implicit val paneChildrenAccess = new PaneNodesAccess
  implicit val tabPaneChildrenAccess = new TabPaneTabsAccess
  implicit val contextMenuMenuItemsAccess = new ContextMenuMenuItemsAccess

  implicit def specs2ordered(specs: List[Template[Node]]): CollectionSpec[TFXParent, Node] = OrderedSpecs(specs)
  implicit def specs2orderedWithIds[Key](specs: List[(Key, Template[Node])]): CollectionSpec[TFXParent, Node] = OrderedSpecsWithIds(specs)
  def unordered[Key](specs: List[(Key, Template[Node])]) = SpecsWithIds(specs)
  implicit def tabs2ordered(tabs: List[Template[Tab]]): CollectionSpec[TabPane, Tab] = OrderedSpecs(tabs)
  implicit def tuple2ParameterizedFixtures[C, I](tuples: List[(Attribute[C, I], Template[I])]): List[ParameterizedFixture.For[C]] = tuples.map(t => ParameterizedFixture.apply(t._1, Some(t._2)))

  implicit class ReconcilationSyntax[Container](reconcilableGroup: CollectionSpec[Container, Node])(implicit collectionAccess: CollectionAccess[Container, Node]) {
    def changes(container: Container): List[Change] = reconcilableGroup.requiredChangesIn(collectionAccess.getCollection(container))
    def reconcile(container: Container): Unit = changes(container).foreach(_.execute())
  }

  implicit class AttributeBinder[FXType, Attr](attribute: Attribute[FXType, Attr]) {
    def ~(value: Attr) = Binding(attribute, value, maintained = true)
  }

  implicit class FixtureBinder[FXType, T](fixture: Attribute[FXType, T]) {
    def ~~(template: Template[T]) = FixtureBinding(fixture, template, maintained = true)
  }

  implicit class AttributeEnforcer[FXType, Attr](attribute: SettableFeature[FXType, Attr]) {
    def <<(value: Attr) = Enforcement(attribute, value, maintained = true)
    def onInit(value: Attr) = Enforcement(attribute, value, maintained = false)
  }

  def branchC[N <: TFXParent: ClassTag](constructorParams: Any*)(constraints: Constraint[N]*)(specGroup: CollectionSpec[TFXParent, Node]): Template[N] =
    Hierarchy[N, Node](constraints, specGroup, constructorParams)

  def branch[Container, Item](constraints: Constraint[Container]*)(children: Template[Item]*)(
    implicit
    collectionAccess: CollectionAccess[Container, Item],
    itemUserDataAccess: UserDataAccess[Item],
    containerUserDataAccess: UserDataAccess[Container],
    classTag: ClassTag[Container]
  ): Template[Container] =
    Hierarchy[Container, Item](constraints, OrderedSpecs[Container, Item](children.toList), Nil)

  def branchL[N <: TFXParent: ClassTag](constraints: Constraint[N]*)(specGroup: CollectionSpec[TFXParent, Node]): Template[N] =
    Hierarchy[N, Node](constraints, specGroup, Nil)

  def leafC[N: ClassTag: UserDataAccess](constructorParams: Any*)(constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, constructorParams)

  def leaf[N: ClassTag: UserDataAccess](constraints: Constraint[N]*): Template[N] =
    Leaf[N](constraints, Nil)

  def fixture[F, C](constraints: Constraint[F]*)(content: Template[C])(implicit ct: ClassTag[F], ud: UserDataAccess[F], f: Attribute[F, C]): Template[F] =
    FixtureSpec[F](constraints, List(ParameterizedFixture(f, Some(content))))

  def fixtures[F](constraints: Constraint[F]*)(fixtures: ParameterizedFixture.For[F]*)(implicit ct: ClassTag[F], ud: UserDataAccess[F]): Template[F] =
    FixtureSpec[F](constraints, fixtures.toList)

  def tabs[T <: TabPane: ClassTag](constraints: Constraint[T]*)(children: Template[Tab]*): Template[T] =
    Hierarchy[T, Tab](constraints, children.toList, Nil)

}
