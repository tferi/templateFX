package com.tothferenc.templateFX.macros

import com.tothferenc.templateFX.attribute.Attribute

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.util.control.NonFatal


object Gen {

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
    val getter = getMethodSymbol("get" + getset)
    val setter = getMethodSymbol("set" + getset)
    val valType = weakTypeTag[Value].tpe.typeConstructor
    val expr =
			q"""new Attribute[$attrType, $valType]{
					override def read(src: $attrType): $valType = src.$getter()
          override def unset(target: $attrType): Unit = ()
          override def set(target: $attrType, value: $valType): Unit = target.$setter(value)
				 }"""
    c.Expr[Attribute[Attr, Value]](expr)
  }
//
//  def expand_impl(c: Context)(annottees: c.Expr[Any]*) = {
//    import c.universe._
//
//    //    def gen(objectName: Name, attrName: Name, fxType: Tree, attrType: Tree) = {
//    //        q"""
//    //          case object $objectName extends Attribute[$fxType, $attrType] {
//    //           override def readFrom(src: $fxType): $attrType = src.get$attrName
//    //           override def set(target: $fxType, value: $attrType): Unit = target.set$attrName(value)
//    //          }
//    //        """
//    //    }
//
//    annottees.map(_.tree).toList match {
//      case q"Attr[$fxType,$attrType]($objectName, $attrName)" :: Nil =>
//        val getter: String = "get" + showCode(attrName)
//        val setter: String = "set" + showCode(attrName)
//
//        val fxt: String = showCode(fxType)
//        val attrt: String = showCode(attrType)
//        val on: String = showCode(objectName)
//
//
//        c.Expr[Any](
//        q"""
//          case object $on extends Attribute[$fxt, $attrt] {
//           override def readFrom(src: $fxt): $attrt = src.$getter
//           override def set(target: $fxt, value: $attrt): Unit = target.$setter(value)
//          }
//        """
//      )
//      // Add validation and error handling here.
//    }
//  }
}