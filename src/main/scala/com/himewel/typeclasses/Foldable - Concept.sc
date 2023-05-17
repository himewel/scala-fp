import cats._
import cats.implicits._

trait MList[+A]
case class MCons[+A](hd: A, tl: MList[A]) extends MList[A]
case object MNil extends MList[Nothing]

object NaiveImplementation {
  def sum(ints: MList[Int]): Int =
    ints match {
      case MCons(hd, tl) => hd + sum(tl)
      case MNil => 0
    }

  def length[A](list: MList[A]): Int =
    list match {
      case MCons(hd, tl) => 1 + length(tl)
      case MNil => 0
    }

  def filterPositive(ints: MList[Int]): MList[Int] =
    ints match {
      case MCons(hd, tl) => 
        if (hd <= 0) filterPositive(tl) 
        else MCons(hd, filterPositive(tl))
      case MNil => MNil
    }
}

object FoldRight {
  def foldRight[A, B](list: MList[A])(z: B)(f: (A, B) => B): B =
    list match {
      case MCons(hd, tl) => f(hd, foldRight(tl, z, f))
      case MNil => z
    }
  
  def sum(ints: MList[Int]): Int = foldRight(ints)(0)((x, y) => x + y)

  def length[A](list: MList[A]): Int = foldRight(list)(0)((x, y) => 1 + y)

  def filterPositive(ints: MList[Int]): MList[Int] = 
    foldRight(ints)(MNil: MList[Int]){ (x, y) => 
      if (x > 0) MCons(x, y) else y
    }
}

object FoldLeft {
  def foldLeft[A, B](list: MList[A])(acc: B)(f: (B, A) => B): B =
    list match {
      case MNil => acc
      case MCons(hd, tl) => foldLeft(tl)(f(acc, hd))(f)
    }

  def sum(ints: MList[Int]): Int = foldLeft(ints)(0)(_ + _)

  def length[A](list: MList[A]): Int = foldLeft(list)(0)((acc, x) => 1 + acc)
}

def foldMap[A, B](fa: F[A])(f: A => B)(implicit M: Monoid[B]): B =
  foldLeft(fa, M.empty)((acc, x) => M.combine(acc, f(a)))