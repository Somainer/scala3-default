package com.somainer.defaults

import scala.util.NotGiven

object DefaultValues:
  inline given [T](using num: Numeric[T]): Default[T] = num.zero
  given Default[Boolean] = false
  given Default[Unit] = ()
  given Default[String] = ""
  given [T]: Default[Option[T]] = None
  given [T]: Default[Seq[T]] = Nil

object SubclassDefaults:
  given [T, U <: T](using defU: Default[U], _noDefaultForT: NotGiven[Default[T]]): Default[T] = defU.defaultValue