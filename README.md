## Scala3 Default

The Default typeclass written in `Scala3`.

A trait for giving a type a useful default value.

Sometimes, you want to fall back to some kind of default value, and don’t particularly care what it is. This comes up often with `class`es that define a set of options:

```scala
case class SomeOptions(foo: Int, bar: Double)
```

How can we define some default values? You can use `Default`:
```scala
import com.somainer.defaults.Default
import com.somainer.defaults.Default.default

case class SomeOptions(foo: Int, bar: Double) derives Default

enum Maybe[+A] derives Default:
  case Nothing
  case Just(value: A)

enum Bool:
  case False, True

// Same as enum Bool derives Default: ...
given Default[Bool] = Default.derived

def hello: Unit =
  val options: SomeOptions = default
  val nothing = default[Maybe[SomeOptions]]
  val bool: Bool = default
  assert(options.foo == 0)
  assert(options.bar == 0.0)
  assert(nothing == Maybe.Nothing)
  assert(bool == Bool.False)
```

### Examples
Using default values:

```scala
import com.somainer.defaults.Default.default
val (x, y) = default[(Option[String], Double)]
val (a, b, (c, d)) = default[(Int, Long, (Boolean, BigInt))]

import scala.compiletime.ops.int.* 
val two: 2 = default
val anotherTwo: 1 + 1 = default
assert(two == 2)
assert(anotherTwo == 2)
val answer: ToString[40 + 2] = default
assert(answer == "42")
```

Making your own:

```scala
enum Kind:
  case A, B, C
given Default[Kind] = Kind.B

assert(default == Kind.B)
```
