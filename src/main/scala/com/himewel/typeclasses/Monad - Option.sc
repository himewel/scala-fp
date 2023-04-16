import cats._
import cats.implicits._
import com.himewel.writechannel.Main.person

sealed trait MOption[+A]
object MOption {
  case class MSome[+A](value: A) extends MOption[A]
  case object MNone extends MOption[Nothing]

  implicit val monadMOption: Monad[MOption] = new Monad[MOption] {
    def pure[A](x: A): MOption[A] = MSome(x)

    def flatMap[A, B](fa: MOption[A])(f: A => MOption[B]): MOption[B] =
      fa match {
        case MNone => MNone
        case MSome(value) => f(value)
      }

    override def map[A, B](fa: MOption[A])(f: A => B): MOption[B] = 
      flatMap(fa)(a => pure(f(a)))

    override def flatten[A](ffa: MOption[MOption[A]]): MOption[A] =
      flatMap(ffa)(identity)

    def tailRecM[A, B](a: A)(f: A => MOption[Either[A, B]]): MOption[B] = ???
  }
}