import cats._
import cats.implicits._

object MonadEither {
  implicit def eitherMonad[E]: Monad[Either[E, *]] = new Monad[Either[E, *]] {
    def pure[A](x: A): Either[E, A] = Right(x)

    def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] =
      fa match {
        case Left(value)  => Left(value)
        case Right(value) => f(value)
      }

    def tailRecM[A, B](a: A)(f: A => Either[E, B]): Either[E, B] = ???
  }

  def main(args: Array[String]): Unit = {
    val x = 5.asRight[String].flatMap(a => (a + 1).asRight[String])
    val y =
      5.asRight[String]
        .flatMap(a => "boom".asLeft[Int])
        .flatMap(a => "boom 2".asLeft[Int])

    println(x)
    println(y)
  }
}
