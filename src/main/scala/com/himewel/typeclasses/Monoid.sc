import cats._
import cats.implicits._

case class Speed(metersPerSecond: Double) {
  def kilometersPerSec: Double = metersPerSecond / 1000.0
  def milesPerSec: Double = metersPerSecond / 1609.34
}

object Speed {
  def addSpeeds(s1: Speed, s2: Speed): Speed = 
    Speed(s1.metersPerSecond + s2.metersPerSecond)
  
  implicit val monoidSpeed: Monoid[Speed] = new Monoid[Account] {
    def combine(x: Speed, y: Speed): Speed = addSpeeds(x, y)
    def empty: Speed = Speed(0)
  }
  implicit val monoidSpeedInstance: Monoid[Speed] = Monoid.instance(Speed(0), addSpeeds)
  implicit val eqSpeed: Eq[Speed] = Eq.fromUniversalEquals
}

Monoid[Speed].combine(Speed(0), Speed(1000))
Monoid[Speed].combine(Speed(1000), Speed(2000))
Monoid.combine(Speed(1000), Speed(2000))
Monoid.combine(Speed(1000), Monoid[Speed].empty)

Speed(1000).combine(Speed(2000))
Speed(1000) |+| Speed(2000)

Monoid[Speed].combineAll(List(Speed(1), Speed(2)))
List(Speed(1), Speed(2)).combineAll

Monoid.isEmpty(Speed(1))


val sumMonoid: Monoid[Int] = Monoid.instance(0, _ + _)
val minMonoid: Monoid[Int] = Monoid.instance(Int.MaxValue, _ min _)
def listMonoid[A]: Monoid[List[A]] = Monoid.instance(Nil, _ ++ _)
val stringMonoid: Monoid[String] = Monoid.instance("", _ + _)

sumMonoid.combine(3, 7)
minMonoid.combine(7, minMonoid.empty)
listMonoid[Int].combine(List(1, 2), List(3, 4))
stringMonoid.combine("Hello ", "World")