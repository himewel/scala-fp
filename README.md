# Functional programming with Scala Cats

## Eq
Eq objects can be created through `by` and `instance` methods. They are useful to implement comparison methods for an object

```scala
case class Account(id: Int)

val eqIdBy: Eq[Account] = Eq.by(_.id)

implicit val eqIdInstance: Eq[Account] = 
  Eq.instance[Account]((a, b) => Eq[Int].eqv(a.id, b.id))

// testing
val a = Account(1)
val b = Account(2)
a === b // false
a.eqv(b) // false
```
## Order

Order objects are used to make comparisons between objects of same type and return a positive value when first > second, negative when first < second and 0 in case of both has the same value. Another ways to invoke Order implementations of an object are using compare, max and min methods. Order can be created using `by` and `from` methods.

```scala
case class Account(id: Int)

val orderByIdBy: Order[Account] = Order.by(_.id)

implicit val orderByIdFrom: Order[Account] = 
  Order.from((a, b) => Order.compare(a.id, b.id))

// testing
val a = Account(1)
val b = Account(2)
a compare b // -1
a min b // Account(1)
a max b // Account(2)
```

## Show

Show its a typeclass with a goal similar to toString. When you need multiple implementations of a string representation for an object, it can be useful. Different from Eq and Order, this typeclass can be implemented with `Show.show` or `Show.fromToString`.

```scala
case class Account(id: Int, owner: String, balance: Double)

val defaultShow: Show[Account] = Show.fromToString
implicit val showOwnerAndBalance: Show[Account] =
  Show.show(account => s"${account.owner} -> $$${account.balance}")

val account = Account(1, "Leia", 1.9)
Show[Account].show(account) // Leia -> 1.9
account.show // Leia -> 1.9
defaultShow.show(account) // Account(1, Leia, 1.9)
```

## Monoid

Monoid its a typeclass with 2 methods to be implemented: `empty` and `combine`. In short, `combine` provides a way to combine two instances of a same class and `empty` represents a default instance of that object. For example, in a Int implementation of Monoid if we combine 1 and 2, we can have 3 as result. 0 can be the empty representation of Int.

```scala
case class Account(id: Int, owner: String, balance: Double)
object Account {
  def mergeAccounts(a1: Account, a2: Account): Account = 
    a1.copy(balance=a1.balance + a2.balance)

  implicit val combineAccount: Monoid[Account] = 
    Monoid.instance(Account(0, "none", 0), mergeAccounts)
}

val lukeAccount = Account(1, "Luke", 2.5)
val leiaAccount = Account(0, "Leia", 1000.5)

leiaAccount |+| lukeAccount // Account(0, "Leia", 1003.0)
List(leiaAccount, lukeAccount).combineAll // Account(0, "Leia", 1003.0)
leiaAccount.combine(lukeAccount)  // Account(0, "Leia", 1003.0)
```

## Functor

Functor trait provides a function called `map`. Functor fits with a group of classes named High Kinded Typeclasses because its parametrized by more then one type. This function is parametrized by types A and B and receives a container of A and a function that transforms type A to B. Finally, the function returns a container of B. Its behavior is described by the following signature: `map[A, B](fa: F[A])(f: A -> B): F[B]`.

```scala
val listFunctor: Functor[List] = new Functor[List] {
  def map[A, B](fa: List[A])(f: A -> B): List[B] =
    fa match {
      case Nil => Nil
      case head :: tail => f(head) :: map(tail)(f)
    }
}

listFunctor.map(List(1, 2, 3))(_ + 1) // List(2, 3, 4)
listFunctor.as(List(1, 2, 3), 1) // List(1, 1, 1)
```

## Applicative

Applicative uses a concept of high kinded type in its implementation. The main goal of this typeclass is to allow the use methods like map to higher-kinded types. Kind its a classification saying how much type parameters a type requires. In this case, an ordinary type like `String` or `Int` has a proper kind represented as `*`. Beyond that, derived types like `List[Int]` has a kind of `* -> *`. In other example, `Either[Int, String]` has a kind of `* -> * -> *`. This cases are called first-order kinds. After that, higher-kinded types represents a dependency of many degrees like in curried representation `(* -> *) -> *`.

In Applicative, two methods needs to be implemented: pure and ap. All that its used to implement a different approach for a multi dimensional map. Pure its used to create a container of a received value. An Applicative has also an ap method. This method receives an container of functions from A to B and a container of A values to be transformed in a container of B.

```scala
val optionApplicative: Applicative[Option] = new Applicative[Option] {
  def pure[A](x: A): Option[A] = Some(x)
  
  def ap[A, B](ff: Option[A => B])(fa: Option[A]): Option[B] = 
    (ff, fa) match {
      case (Some(f), Some(a)) => Some(f(a))
      case _ => None
    }
}

val stringOption = optionApplicative.pure("hello world") // Some(hello world)

val intOption = optionApplicative.pure(5) // Some(5)
val intOption2 = optionApplicative.pure(10) // Some(10)

(intOption, intOption2).mapN((a, b) => a + b) // Some(15)
(intOption, intOption2).map2((a, b) => a + b) // Some(15)
```

## Monad

My extending Functor and Applicative, Monads are very similar to them. The main addition of a Monad is the `flatMap` method. This method its similar to `map` but,instead of receiving a function to other type (`A => B`), it receives a function to a container (`A => F[B]`). Of course, by extending Applicative it obligates the implementation of pure. `Map` comes as an implementation of `flatMap` and `pure`.

```scala
trait MOption[+A]

object MOption {
  case class MSome[A] extends MOption[A]
  case object MNone extends MOption[Nothing]

  implicit val monadMOption: Monad[MOption] = new Monad {
    def pure[A](x: A): MOption[A] = MSome(x)

    def flatMap[A, B](fa: MOption[A])(f: A => MOption[B]): MOption[B] =
      fa match {
        case MSome(value) => f(fa)
        case MNone => MNone
      }
  }
}
```

In this case, the function can be implemented as the following example:

```scala
def map[A, B](fa: MOption[A])(f: A => B): MOption[B] =
  flatMap(fa)(a => pure(f(a)))
```

## MonadError

A MonadError presents a bit more of power to a normal Monad: the ability to handle errors. So, beyond the `pure` and `flatMap`, MonadError requires the implementation of other two methods, `handleErrorWith` and `raiseError`.

```scala
implicit val tryME: MonadError[Try, Throwable] = new MonadError[Try, Throwable] {
  def raiseError[A](e: Throwable): Try[A] = Failure(e)

  def handleErrorWith[A](fa: Try[A])(f: Throwable => Try[A]): Try[A] =
    fa match {
      case Success(value) => Success(value)
      case Failure(value) => f(a)
    }

  def pure[A](a: A): Try[A] = ???
  def flatMap[A, B](fa: Try[A])(f: A => Try[B]): Try[B] = ???
}
```

So, in case we want to generalize an error handling for different kinded types (i.e. Try, Option, Either), we can use a signature similar to the following code. In this example, the type `F[_]` its used to represent a kinded type and `E` represents the error type of the MonadError. In addition, a function handling the error its used to transform an `Exception` into an object of type `E` (compatible with the MonadError signature).

```scala
trait HttpMethod
object GET extends HttpMethod
case class HttpRequest(method: HttpMethod, url: String)
case class HttpResponse(status: Int)

def executeRequest[F, E](req: HttpRequest)(f: Exception => E)(implicit ME: MonadError[F, E]): F[HttpResponse] =
  try {
    ME.pure(doRequest(req))
  } catch {
    case e: Exception => ME.raiseError(f(e))
  }

type ErrorOn[A] = Either[String, A]
executeRequest[ErrorOn[A], String](HttpRequest(GET, "www.example.com"))((e: Exception) => e.getMessage())
```

MonadError provides other helper methods such as `attempt` and `ensure`. `attempt` ensures that all non-fatal errors should be handled by this method. `ensure` turns a value into an error if it doesn`t matches with a provided condition.

```scala
MonadError[Option, Unit].attempt(Some(3)) // Some(Right(3))
MonadError[Option, Unit].attempt(None) // Some(Left(())))

MonadError[Option, Unit].ensure(Some(3))(())(_ => _ % 2 == 0) // None
MonadError[Option, Unit].ensure(Some(2))(())(_ => _ % 2 == 0) // Some(2)
```

## Foldable

Foldable its a typeclass extending Monoid, so it inherits methods like `empty` and `combine`.
A Foldable implementation provides the methods `foldRight` and `foldLeft`. Both them receives a function to be executed recursively carrying an accumulator and the item to be iterated. `foldRight` puts the recursion tree at right side by calling the function using the iteration value and the recursion stack as the result of the method. In other hand, `foldLeft` does the opposite by executing the recursion using the empty value as an accumulator, so each iteration updates the accumulator for each recursion call. Because of these behaviors, `foldLeft` iterates throw the container starting by the last item so depending of the function, the result will be reversed.

Recursion stack example for foldRight:

```scala
val f = (a, b) => a + b
val list = List(1, 2, 3)
val z = 0

foldRight(list, z)(f):
  f(1, foldRight(List(2, 3), z)(f))
  f(1, f(2, foldRight(List(3), z)(f)))
  f(1, f(2, f(3, foldRight(List(3), z)(f))))
  f(1, f(2, f(3, z)))
  f(1, f(2, 3))
  f(1, 5)
  6
```

Recursion stack example for foldLeft:

```scala
val f = (a, b) => a + b
val list = List(1, 2, 3)
val z = 0

foldLeft(list, z)(f):
  foldLeft(List(2, 3), f(z, 1))(f)
  foldLeft(List(3), f(f(z, 1), 2))(f)
  foldLeft(List(), f(f(f(z, 1),  2), 3))(f)
  foldLeft(List(), f(f(1, 2), 3))(f)
  foldLeft(List(), f(3, 3))(f)
  6
```
Other useful methods from Foldable:
- `fold`: applies combine to the values
- `foldMap`: applies a function and `fold` the result
