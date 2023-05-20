import cats._
import cats.implicits._

trait MList[+A]
case class MCons[+A](hd: A, tl: MList[A]) extends MList[A]
case object MNil extends MList[Nothing]

case class Person(name: String)
object Person {
  def findPersonByName(name: String)(people: MList[Person]): Option[Person] = 
    people match {
      case MCons(hd, tl) => 
        if (hd.name == name) Some(hd) 
        else findPersonByName(name)(tl)
      case MNil => None
    }

  def findPeopleByNames(names: MList[String])(people: MList[Person]): Option[MList[Person]] = 
    names match {
      case MNil => Some(MNil)
      case MCons(hd, tl) => MCons(findPersonByName(hd)(people), findPeopleByNames(tl)(people))
    }
}

def traverse[F[_]: Applicative, A, B](as: MList[A])(f: A => F[A]): F[MList[B]] =
  as match {
    case MCons(hd, tl) => (f(hd), traverse(tl)(f)).mapN(MCons.apply)
    case MNil => Applicative[F].pure(MNil)
  }