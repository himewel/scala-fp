import cats._
import cats.implicits._
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

case class Secret[A](value: A) {
  private def hashed: String = {
    val bytes = value.toString.getBytes(StandardCharsets.UTF_8)
    val hashBytes = MessageDigest.getInstance("SHA-1").digest(bytes)
    new String(hashBytes, StandardCharsets.UTF_8)
  }

  override def toString(): String = hashed
}

object Secret {
  implicit val secretFunctor: Functor[Secret] = new Functor[Secret] {
    def map[A, B](secret: Secret[A])(f: A => B): Secret[B] = 
      new Secret[B](f(secret.value))
  }
}

val mySecret = new Secret("this is my secret")
mySecret.value

val upperMySecret = Functor[Secret].map(mySecret)(_.toUpperCase)
upperMySecret.value

val optionFunctor: Functor[Option] = new Functor[Option] {
  def map[A, B](fa: Option[A])(f: A => B): Option[B] =
    fa match {
      case None => None
      case Some(value) => Some(f(value))
    }
    
}
val listFunctor: Functor[List] = new Functor[List] {
  def map[A, B](fa: List[A])(f: A => B): List[B] =
    // if (fa.isEmpty) 
    //   List[B]()
    // else 
    //   f(fa.head) :: map(fa.tail)(f)
    fa match {
      case Nil => Nil
      case head :: next => f(head) :: map(next)(f)
    }
}

optionFunctor.map(Some(3))(_ + 2)
listFunctor.map(List(3, 5, 8))(_ + 2)

optionFunctor.as(Some(2), 10)
listFunctor.as(List(1, 2, 3), 10)