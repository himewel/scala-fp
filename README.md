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

Functor trait provides a function called `map`. Functor fits with a group of classes named High Kinded Typeclassed because its paremetrised by more then one type. This function is pametrised by types A and B and receives a container of A and a function that transforms type A to B. Finally, the function returns a container of B. Its behavior is described by the following signature: `map[A, B](fa: F[A])(f: A => B): F[B]`.

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