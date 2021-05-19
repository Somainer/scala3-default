import org.junit.Test
import org.junit.Assert.*

class DefaultTest:
  import com.somainer.defaults.Default.default
  import com.somainer.defaults.Default

  @Test def `default of numbers should be 0`(): Unit = 
    assertEquals(default[Int], 0)
    assertEquals(default[Boolean], false)
    assertEquals(default[BigInt], BigInt(0))
    assertEquals(default[(Int, Int)], (0, 0))

  @Test def `singleton constant values should have default`(): Unit =
    import scala.compiletime.ops.int.* 
    assertEquals(default[42], 42) 
    assertEquals(default[3 + 2], 5) 
    assertEquals(default[ToString[2 * 10 * 2 + 2]], "42") 

  @Test def `default value of sum types should be first case`(): Unit =
    enum Op[+A] derives Default:
      case Nn
      case Sm(value: A)
    
    assertEquals(default[Op[Int]], Op.Nn)
    assertEquals(default[Op.Sm[Int]], Op.Sm(0))

    enum Bool derives Default:
      case False, True
    assertEquals(default[Bool], Bool.False)
  
  @Test def `default value of product types should be each defaults of elements`(): Unit =
    case class P(name: Option[String], age: Int)

    assertEquals(default[P], P(None, 0))
  
  @Test def `custom default value should be possible`(): Unit =
    given [T: Default]: Default[Option[T]] = Some(default[T])
    assertEquals(default[Option[String]], Some(""))
  
  @Test def `default value of mutable classes should not be same`(): Unit =
    import scala.collection.mutable.ListBuffer
    given [T]: Default[ListBuffer[T]] = ListBuffer.empty

    assertTrue(default[ListBuffer[Int]] ne default[ListBuffer[Int]])
    val lb = default[ListBuffer[Int]]
    assertTrue(lb.isEmpty)
    lb.addOne(default)
    assertTrue(lb.nonEmpty)
    val nlb = default[ListBuffer[Int]]
    assertTrue(nlb.isEmpty)
