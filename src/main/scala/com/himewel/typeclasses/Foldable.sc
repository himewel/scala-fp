import cats._
import cats.implicits._

trait MList[+A]

object MList {
  def apply[A](elems: A*): MList[A] =
    elems.foldRight(mnil[A])((a, b) => mcons(a, b))

  case object MNil extends MList[Nothing]
  case class MCons[+A](hd: A, tl: MList[A]) extends MList[A]

  def mnil[A]: MList[A] = MNil
  def mcons[A](hd: A, tl: MList[A]): MList[A] = MCons(hd, tl)

  implicit val listFoldable: Foldable[MList] = new Foldable[MList] {
    def foldLeft[A, B](fa: MList[A], b: B)(f: (B, A) => B): B = fa match {
      case MCons(hd, tl) => foldLeft(tl, f(b, hd))(f)
      case MNil => b
    }

    def foldRight[A, B](fa: MList[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = {
      def loop(as: MList[A]): Eval[B] =
        as match {
          case MCons(hd, tl) => f(hd, Eval.defer(loop(tl)))
          case MNil => lb
        }

      Eval.defer(loop(fa))
    }
  }
}

MList(1, 2, 3, 4)

import MList._

def sum(ints: MList[Int]): Int = Foldable[MList].foldLeft(ints, 0)(_ + _)

def length[A](list: MList[A]): Int = Foldable[MList].foldLeft(list, 0)((acc, x) => 1 + acc)

def filterPositive(ints: MList[Int]): MList[Int] =
  Foldable[MList].foldRight(ints, Eval.now(mnil[Int])){ (a, b) => 
    if (a > 0) Eval.now(mcons(a, b.value))
    else b
  }.value

sum(MList(1, 2, 3))
length(MList(1, 2, 3))
length(MList("a", "b", "c"))
filterPositive(MList(-1, 0, 1, 3, -2))

MList(1, 2, 3).foldMap(_.show)
MList(1, 2, 3).foldMap(i => i * 2)
MList(1, 2, 3).fold

def find[F[_]: Foldable, A](fa: F[A])(p: A => Boolean): Option[A] =
  fa.foldLeft[Option[A]](None)((acc, x) => if (p(x)) Some(x) else acc)

find(List(1, 2, 3))(_ % 2 == 0)

def exists[F[_]: Foldable, A](fa: F[A])(p: A => Boolean): Boolean =
  fa.foldLeft[Boolean](false)((acc, x) => if (p(x)) true else acc)

exists(List(1, 2, 3))(_ == 2)

def toList[F[_]: Foldable, A](fa: F[A]): MList[A] = 
  fa.foldRight[MList[A]](Eval.now(mnil[A]))(
    (x, acc) => Eval.now(MCons(x, acc.value))
  ).value

toList(List(1, 2, 3))

def forall[F[_]: Foldable, A, B](fa: F[A])(p: A => Boolean): Boolean =
  fa.foldLeft(true)((acc, x) => acc & p(x))

forall(List(1, 2, 3))((x: Int) => x % 2 == 0)