package com.tothferenc.templateFX.base

import scala.reflect.macros.whitebox.Context

private[base] object MacroHelper {

  def prepare[Attr: c.WeakTypeTag, Value: c.WeakTypeTag](c: Context)(getterSetterName: c.Expr[String]) = {
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
    (getter, setter, attrType, valType, name)
  }
}
