package com.somainer.defaults
import scala.language.implicitConversions

import scala.compiletime.{summonFrom, constValue, erasedValue, summonInline}
import scala.deriving.*
import scala.util.NotGiven

/**
 *  The [[Default]] typeclass.
 *  @example You can provice a default value by: {{{
 *    given [T]: Default[Seq[T]] = Seq.empty[T] 
 * }}}
**/
trait Default[T]:
  def defaultValue: T

object Default:
  private inline def value[T](using default: Default[T]): T = default.defaultValue
  /**
   * Summon default value for [[T]]
   * If there is a `Default[T]` instance, then [[default]] will return the default value of [[T]],
   * otherwise if [[T]] is a constant literal, like `42` or `40 + 2`, then [[default]] will return its literal value.
   * @example{{{
   * import com.somainer.Default.default
   * assert(default[42] == 42)
   * assert(default[Int] == 0)
   * }}}
  **/
  inline def default[T]: T = summonFrom {
    case given Default[T] => value[T]
    case given ValueOf[T] => valueOf[T]
  }

  inline implicit def genDefault[T, U <: T](inline value: U): Default[T] =
    new Default[T]:
      override def defaultValue: T = value
  end genDefault
  
  import DefaultValues.given
  export DefaultValues.given
  inline def summonAllDefaults[T <: Tuple]: List[Default[?]] =
    inline erasedValue[T] match
      case _: EmptyTuple => Nil
      case _: (t *: ts) => summonInline[Default[t]] :: summonAllDefaults[ts]
  end summonAllDefaults
  
  /**
   *  Automatic implement Default typeclass for such cases:
   *  For enum classes, the default value is the first case.
   *  For case classes, the default value is all default values of types in the main constructor.
   *  @example {{{
   *  enum Bool derives Default:
   *    case False, True
   *  assert(default[Bool] == Bool.False)
   * }}}
  **/
  inline given derived[T](using m: Mirror.Of[T]): Default[T] =
    lazy val elemsInstances = summonAllDefaults[m.MirroredElemTypes]
    inline m match
      case product: Mirror.ProductOf[T] =>
        new Default[T]:
          def defaultValue: T =
            product.fromProduct(Tuple.fromArray(elemsInstances.map(_.defaultValue).toArray))
      case sum: Mirror.SumOf[T] =>
        elemsInstances.head.asInstanceOf
  end derived
