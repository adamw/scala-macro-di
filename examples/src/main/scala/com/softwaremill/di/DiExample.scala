package com.softwaremill.di

trait DiExample {
  import DiMacro._

  class A()
  class B()
  class C(a: A, b: B) { override def toString = s"${super.toString}(${a}, ${b})" }
  class D(b: B, c: C) { override def toString = s"${super.toString}(${b}, ${c})" }

  val a = wire[A]
  val bDifferentName = wire[B]
  val c = wire[C]
  val d = wire[D]

  /*
  Scopes:
  val a = wire[X]
  def a = wire[X]
  val a = provided(instance)

  val a = wire[X].with(anotherYInstance)

  subtyping

  Factories:
  def a(p1: T, p2: U, ...) = wire[X] // p1, p2 used in parameters - warn if not used

  val a = conf(10) // by-name binding for configuration parameters
   */

  println(a)
  println(bDifferentName)
  println(c)
  println(d)
}

object DiExampleRunner extends DiExample with App
