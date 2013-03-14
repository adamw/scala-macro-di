package com.softwaremill.di

import language.experimental.macros

import reflect.macros.Context
import annotation.tailrec

object DiMacro {
  def wire[T]: T = macro wire_impl[T]

  def wire_impl[T: c.WeakTypeTag](c: Context): c.Expr[T] = {
    import c.universe._

    // TODO: this should check if the found value uses wired[], or is just a value of the desired type
    def findWiredOfType(t: Type): Option[Name] = {
      @tailrec
      def doFind(trees: List[Tree]): Option[Name] = trees match {
        case Nil => {
          c.error(c.enclosingPosition, s"Cannot find a wired value of type ${t}")
          None
        }
        case tree :: tail => tree match {
          // TODO: subtyping
          case ValDef(_, name, tpt, _) if tpt.tpe == t => Some(name.encodedName)
          case _ => doFind(tail)
        }
      }

      val ClassDef(_, _, _, Template(_, _, body)) = c.enclosingClass
      doFind(body)
    }

    val tType = implicitly[c.WeakTypeTag[T]]
    val tConstructorOpt = tType.tpe.members.find(_.name.decoded == "<init>")
    val result = tConstructorOpt match {
      case None => {
        c.error(c.enclosingPosition, "Cannot find constructor for " + tType)
        reify { null.asInstanceOf[T] }
      }
      case Some(tConstructor) => {
        val params = tConstructor.asMethod.paramss.flatten

        val newT = Select(New(Ident(tType.tpe.typeSymbol)), nme.CONSTRUCTOR)

        val constructorParams = for (param <- params) yield {
          val wireTo = findWiredOfType(param.typeSignature)
            // If we cannot find a value of the given type, trying a by-name match, using the same name as the
            // constructor's parameter.
            .getOrElse(param.name)

          Ident(wireTo)
        }

        val newTWithParams = Apply(newT, constructorParams)
        c.info(c.enclosingPosition, s"Generated code: ${c.universe.show(newTWithParams)}", force = false)
        c.Expr(newTWithParams)
      }
    }

    result
  }
}
