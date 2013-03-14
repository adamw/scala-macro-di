package com.softwaremill.di

trait DiExample {
  import DiMacro._

  class A()
  class B()
  class C(a: A, b: B) { override def toString = s"${super.toString}(${a}, ${b})" }
  class D(b: B, c: C) { override def toString = s"${super.toString}(${b}, ${c})" }

  val a = wire[A]
  val theB = wire[B]
  val c = wire[C]
  val d = wire[D]

  println(a)
  println(theB)
  println(c)
  println(d)
}

object DiExampleRunner extends DiExample with App
