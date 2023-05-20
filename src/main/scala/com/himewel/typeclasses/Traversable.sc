import cats._
import cats.implicits._

trait MList[+A]

object MList {
  case class MCons[+A](hd: A, tl: MList[A]) extends MList[A]
  case object MNil extends MList[Nothing]

  def mnil[A]: MList[A] = MNil
  def mcons[A](hd: A, tl: MList[A]): MList[A] = MCons(hd, tl)

  def apply[A](elems: A*): MList[A] = 
    elems.foldRight(mnil[A])((a, b) => mcons[A](a, b))
  
  implicit val listFunctor: Functor[MList] = new Functor[MList] {
    def map[A, B](fa: MList[A])(f: A => B): MList[B] = 
      fa match {
        case MCons(hd, tl) => mcons[B](f(hd), map(tl)(f))
        case MNil => mnil[B]
      }
  }

  implicit val listTraverse: Traverse[MList] = new Traverse[MList] {
    def traverse[G[_]: Applicative, A, B](fa: MList[A])(f: A => G[B]): G[MList[B]] =
      fa match {
        case MCons(hd, tl) => (f(hd), traverse(tl)(f)).mapN(MCons.apply)
        case MNil => Applicative[G].pure(mnil[B])
      }
      // sequence(fa.map(f))

    override def sequence[G[_]: Applicative, A](fga: MList[G[A]]): G[MList[A]] =
      traverse(fga)(identity)

    def foldLeft[A, B](fa: MList[A], b: B)(f: (B, A) => B): B =
      fa match {
        case MCons(hd, tl) => foldLeft(tl, f(b, hd))(f)
        case MNil => b
      }

    def foldRight[A, B](fa: MList[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = {
      def loop(as: MList[A]): Eval[B] =
        fa match {
          case MCons(hd, tl) => f(hd, Eval.defer(loop(tl)))
          case MNil => lb
        }
      Eval.defer(loop(fa))
    }
  }
}

Traverse[MList].sequence(MList(Option(5), Option(4)))
Traverse[MList].sequence(MList(Option(5), None))

Traverse[MList].traverse(MList(1, 2, 3))(i => Option(i + 1))
Traverse[MList].traverse(MList(1, 2, 3))(Applicative[Option].pure)

val optionTraverse: Traverse[Option] = new Traverse[Option] {
  def traverse[G[_]: Applicative, A, B](fa: Option[A])(f: A => G[B]): G[Option[B]] =
    fa match {
      case None => Applicative[G].pure(None)
      case Some(value) => (f(value)).map(Some.apply)
    }

  def foldLeft[A, B](fa: Option[A], b: B)(f: (B, A) => B): B =
    fa match {
      case None => b
      case Some(a) => f(b, a)
    }

  def foldRight[A, B](fa: Option[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
    fa match {
      case None => lb
      case Some(a) => f(a, lb)
    }
}

optionTraverse.traverse(Some(5))(i => List(i + 1, i + 2))
optionTraverse.traverse(Some(5))(i => List())

optionTraverse.traverse[List, Int, Int](None)(i => List(i + 1, i + 2))
optionTraverse.traverse[List, Int, Int](None)(i => Nil)