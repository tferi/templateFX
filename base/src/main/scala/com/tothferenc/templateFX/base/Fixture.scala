package com.tothferenc.templateFX.base

import scala.language.existentials
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

final case class SetFixture[Container, FixedItem](container: Container, fixture: Fixture[Container, FixedItem], spec: Option[Template[FixedItem]]) extends Change {
  override protected def exec(): Unit = spec match {
    case Some(template) => fixture.set(container, template.build())
    case _ => fixture.remove(container)
  }
}

abstract class Fixture[-Container, FixedItem] extends SettableFeature[Container, FixedItem] {

  override def remove(item: Container): Unit

  def read(container: Container): FixedItem

  def set(container: Container, fixed: FixedItem): Unit

  def reconcile(container: Container, specOption: Option[Template[FixedItem]]): List[Change] = {
    Option(read(container)) -> specOption match {
      case (Some(existing), Some(specified)) =>
        specified.reconcilationSteps(existing).getOrElse(List(SetFixture(container, this, specOption)))

      case (None, None) =>
        Nil

      case _ =>
        List(SetFixture(container, this, specOption))
    }
  }
}

object Fixture {

  def simple[Attr, Value](getterSetterName: String, default: Value): Fixture[Attr, Value] = macro simpleImpl[Attr, Value]

  def simpleImpl[Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(getterSetterName: c.Expr[String], default: c.Expr[Value]): c.Expr[Fixture[Attr, Value]] = {
    import c.universe._

    val attrType = weakTypeTag[Attr].tpe

    val Literal(Constant(getset: String)) = getterSetterName.tree
    val name = {
      val (firstChar, rest) = getset.splitAt(1)
      firstChar.toLowerCase + rest
    }
    val valType = weakTypeTag[Value].tpe
    val getter = TermName("get" + getset)
    val setter = TermName("set" + getset)
    val expr =
      q"""new Fixture[$attrType, $valType]{
					override def read(src: $attrType): $valType = src.$getter()
          override def remove(target: $attrType): Unit = target.$setter($default)
          override def set(target: $attrType, value: $valType): Unit = target.$setter(value)
          override def toString(): String = $name
				 }"""
    c.Expr[Fixture[Attr, Value]](expr)
  }
}