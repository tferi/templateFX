package com.tothferenc.templateFX.attribute

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.util.control.NonFatal

object Attribute {
  val key = "attributes"

  private case class MethodNotFoundException(tpe: String, method: String) extends Exception(s"Method $method was not found on $tpe")

  def simple[Attr, Value](getterSetterName: String): Attribute[Attr, Value] = macro simpleImpl[Attr, Value]

  def simpleImpl[Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(getterSetterName: c.Expr[String]): c.Expr[Attribute[Attr, Value]] = {
    import c.universe._

    val attrType = weakTypeTag[Attr].tpe.typeConstructor

    def getMethodSymbol(methodName: String): c.universe.MethodSymbol = {
      try {
        attrType.member(TermName(methodName)).asMethod
      } catch {
        case NonFatal(t) => throw new MethodNotFoundException(attrType.toString, methodName)
      }
    }
    val Literal(Constant(getset: String)) = getterSetterName.tree
    val name = {
      val (firstChar, rest) = getset.splitAt(1)
      firstChar.toLowerCase + rest
    }
    val getter = getMethodSymbol("get" + getset)
    val setter = getMethodSymbol("set" + getset)
    val valType = weakTypeTag[Value].tpe.typeConstructor
    val expr =
      q"""new Attribute[$attrType, $valType]{
					override def read(src: $attrType): $valType = src.$getter()
          override def unset(target: $attrType): Unit = ()
          override def set(target: $attrType, value: $valType): Unit = target.$setter(value)
          override def toString(): String = $name
				 }"""
    c.Expr[Attribute[Attr, Value]](expr)
  }
}

abstract class Attribute[-FXType, AttrType] extends Unsettable[FXType] {

  def read(src: FXType): AttrType

  def set(target: FXType, value: AttrType): Unit
}