package com.tothferenc.templateFX.attribute

import scala.language.experimental.macros
import scala.reflect.macros.Universe
import scala.reflect.macros.whitebox.Context

object Attribute {
  val key = "attributes"

  private case class MethodNotFoundException(tpe: String, method: String, cause: Throwable) extends Exception(s"Method $method was not found on $tpe", cause)

  def simple[Attr, Value](getterSetterName: String, default: Value): Attribute[Attr, Value] = macro simpleImpl[Attr, Value]

  def simpleImpl[Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(getterSetterName: c.Expr[String], default: c.Expr[Value]): c.Expr[Attribute[Attr, Value]] = {
    import c.universe._

    val scalaBooleanType = weakTypeTag[Boolean].tpe
    val javaBooleanType = weakTypeTag[java.lang.Boolean].tpe

    val attrType = weakTypeTag[Attr].tpe

    val Literal(Constant(getset: String)) = getterSetterName.tree
    val name = {
      val (firstChar, rest) = getset.splitAt(1)
      firstChar.toLowerCase + rest
    }
    val valType = weakTypeTag[Value].tpe
    val getterPrefix = if (scalaBooleanType == valType || javaBooleanType == valType) "is" else "get"
    val getter = TermName(getterPrefix + getset)
    val setter = TermName("set" + getset)
    val expr =
      q"""new Attribute[$attrType, $valType]{
					override def read(src: $attrType): $valType = src.$getter()
          override def remove(target: $attrType): Unit = target.$setter($default)
          override def set(target: $attrType, value: $valType): Unit = target.$setter(value)
          override def toString(): String = $name
				 }"""
    c.Expr[Attribute[Attr, Value]](expr)
  }

  def writeOnly[Attr, Value](getterSetterName: String, default: Value): SettableFeature[Attr, Value] = macro writeOnlyImpl[Attr, Value]

  def writeOnlyImpl[Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(getterSetterName: c.Expr[String], default: c.Expr[Value]): c.Expr[SettableFeature[Attr, Value]] = {
    import c.universe._

    val attrType = weakTypeTag[Attr].tpe

    val Literal(Constant(getset: String)) = getterSetterName.tree
    val name = {
      val (firstChar, rest) = getset.splitAt(1)
      firstChar.toLowerCase + rest
    }
    val valType = weakTypeTag[Value].tpe
    val setter = TermName("set" + getset)
    val expr =
      q"""new SettableFeature[$attrType, $valType]{
          override def remove(target: $attrType): Unit = target.$setter($default)
          override def set(target: $attrType, value: $valType): Unit = target.$setter(value)
          override def toString(): String = $name
				 }"""
    c.Expr[SettableFeature[Attr, Value]](expr)
  }

  def list[Attr, Value](getterName: String): Attribute[Attr, List[Value]] = macro listImpl[Attr, Value]

  def listImpl[Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(getterName: c.Expr[String]): c.Expr[Attribute[Attr, List[Value]]] = {
    import c.universe._

    val attrType = weakTypeTag[Attr].tpe

    val Literal(Constant(getset: String)) = getterName.tree
    val name = {
      val (firstChar, rest) = getset.splitAt(1)
      firstChar.toLowerCase + rest
    }
    val valType = weakTypeTag[Value].tpe
    val getter = TermName("get" + getset)
    val expr =
      q"""new Attribute[$attrType, List[$valType]]{
					override def read(src: $attrType): List[$valType] = src.$getter.toList
          override def remove(target: $attrType): Unit = target.$getter.clear()
          override def set(target: $attrType, value: List[$valType]): Unit = target.$getter.setAll(value: _*)
          override def toString(): String = $name
				 }"""
    c.Expr[Attribute[Attr, List[Value]]](expr)
  }

  def listCustomEquals[Attr, Value](getterName: String, customEquals: ((Value, Value)) => Boolean): Attribute[Attr, List[Value]] = macro listCustomEqualsImpl[Attr, Value]

  def listCustomEqualsImpl[Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(getterName: c.Expr[String], customEquals: c.Expr[((Value, Value)) => Boolean]): c.Expr[Attribute[Attr, List[Value]]] = {
    import c.universe._

    val attrType = weakTypeTag[Attr].tpe

    val Literal(Constant(getset: String)) = getterName.tree
    val name = {
      val (firstChar, rest) = getset.splitAt(1)
      firstChar.toLowerCase + rest
    }
    val valType = weakTypeTag[Value].tpe
    val getter = TermName("get" + getset)
    val expr =
      q"""new Attribute[$attrType, List[$valType]]{
					override def read(src: $attrType): List[$valType] = src.$getter.toList
          override def remove(target: $attrType): Unit = target.$getter.clear()
          override def set(target: $attrType, value: List[$valType]): Unit = target.$getter.setAll(value: _*)
          override def toString(): String = $name
					override def isEqual(item1: List[$valType], item2: List[$valType]): Boolean = {
		        item1.length == item2.length &&
					  item1.zip(item2).forall($customEquals)
	        }
				 }"""
    c.Expr[Attribute[Attr, List[Value]]](expr)
  }

  def remote[Holder, Attr, Value](getterSetterName: String): Attribute[Attr, Value] = macro remoteImpl[Holder, Attr, Value]

  def remoteImpl[Holder: c.WeakTypeTag, Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(getterSetterName: c.Expr[String]): c.Expr[Attribute[Attr, Value]] = {
    import c.universe._

    val holderCompanion = weakTypeTag[Holder].tpe.typeSymbol.companion.name.toTermName

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
      q"""new Attribute[$attrType, $valType] {
					override def read(src: $attrType): $valType = $holderCompanion.$getter(src)
          override def remove(target: $attrType): Unit = $holderCompanion.$setter(target, null)
          override def set(target: $attrType, value: $valType): Unit = $holderCompanion.$setter(target, value)
          override def toString(): String = $name
				 }"""
    c.Expr[Attribute[Attr, Value]](expr)
  }
}

abstract class SettableFeature[-FXType, AttrType] extends RemovableFeature[FXType] {
  def set(target: FXType, value: AttrType): Unit
}

abstract class Attribute[-FXType, AttrType] extends SettableFeature[FXType, AttrType] {
  def read(src: FXType): AttrType
  def isEqual(item1: AttrType, item2: AttrType) = item1 == item2
}