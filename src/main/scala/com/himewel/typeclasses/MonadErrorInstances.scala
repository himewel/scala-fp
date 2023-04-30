import cats._
import cats.implicits._
import scala.util._

object MonadErrorInstances {
  def eitherME[E]: MonadError[Either[E, *], E] = new MonadError[Either[E, *], E] {
    def raiseError[A](e: E): Either[E, A] = Left(e)

    def handleErrorWith[A](fa: Either[E, A])(f: E => Either[E, A]): Either[E, A] =
      fa match {
        case Left(value)  => f(value)
        case Right(value) => Right(value)
      }

    def pure[A](a: A): Either[E, A] = Right(a)

    override def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] =
      fa match {
        case Left(value)  => Left(value)
        case Right(value) => f(value)
      }

    def tailRecM[A, B](a: A)(f: A => Either[E, Either[A, B]]): Either[E, B] = ???
  }

  val tryME: MonadError[Try, Throwable] = new MonadError[Try, Throwable] {
    def raiseError[A](e: Throwable): Try[A] = Failure(e)

    def handleErrorWith[A](fa: Try[A])(f: Throwable => Try[A]): Try[A] =
      fa match {
        case Success(value) => Success(value)
        case Failure(value) => f(value)
      }

    def pure[A](a: A): Try[A] = Success(a)

    def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] =
      fa match {
        case Success(value) => f(value)
        case Failure(value) => Failure(value)
      }

    def tailRecM[A, B](a: A)(f: A => Try[Either[A, B]]): Try[B] = ???
  }
}
