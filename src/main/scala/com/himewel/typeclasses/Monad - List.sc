import cats._
import cats.implicits._


implicit val listMonad: Monad[List] = new Monad[List] {
  def pure[A](x: A): List[A] = List(x)

  def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] =
    fa match {
      case (head :: next) => f(head) ::: flatMap(next)(f)
      case Nil => Nil
    }

  def tailRecM[A, B](a: A)(f: A => List[Either[A, B]]): List[B] = ???
}

Monad[List].flatMap(List(1, 2, 3))(a => List(4, 5, 6).map(_ + a))