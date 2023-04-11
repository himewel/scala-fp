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

Functor trait provides a function called `map`. Functor fits with a group of classes named High Kinded Typeclassed because its paremetrised by more then one type. This function is pametrised by types A and B and receives a container of A and a function that transforms type A to B. Finally, the function returns a container of B. Its behavior is described by the following signature: `map[A, B](fa: F[A])(f: A -> B): F[B]`.

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