import cats._
import cats.implicits._
import scala.util._

implicit val tryMonad: Monad[Try] = new Monad[Try] {
  def pure[A](x: A): Try[A] = Success(x)

  def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] =
    fa match {
      case Failure(exception) => Failure(exception)
      case Success(value) => f(value)
    }

  def tailRecM[A, B](a: A)(f: A => Try[Either[A, B]]): Try[B] = ???
}

tryMonad.pure(5)
tryMonad.pure(5).flatMap(i => tryMonad.pure(1 + i))
tryMonad.pure(5).flatMap(i => Failure(new Exception("error")))
tryMonad.pure(5)
  .flatMap(i => Failure(new Exception("error")))
  .flatMap(j => Failure(new Exception("error 2")))