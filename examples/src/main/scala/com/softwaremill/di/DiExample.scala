package com.softwaremill.di

trait DiExample {
  import DiMacro._

  case class A()
  case class B()
  case class C(a: A, b: B)

  val a = wire[A]
  val bDifferentName = wire[B]
  val c = wire[C]

  /*
  Scopes:
  val a = wire[X]
  def a = wire[X]
  val a = provided(instance)

  Factories:
  def a(p1: T, p2: U, ...) = wire[X] // p1, p2 used in parameters - warn if not used

  val a = conf(10) // by-name binding for configuration parameters
   */

  println(a, bDifferentName, c)
}

object DiExampleRunner extends DiExample with App
