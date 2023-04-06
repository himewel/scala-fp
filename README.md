# Functional programming with Scala Cats

## Eq
Eq objects can be created through `by` and `instance` methods. They are useful to implement comparison methods for an object

```scala
case class Account(id: Int)

val eqIdBy: Eq[Account] = Eq.by(_.id)

implicit val eqIdInstance: Eq[Account] = Eq.instance[Account]((a, b) => Eq[Int].eqv(a.id, b.id))

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

implicit val orderByIdFrom: Order[Account] = Order.from((a, b) => Order.compare(a.id, b.id))

// testing
val a = Account(1)
val b = Account(2)
a compare b // -1
a min b // Account(1)
a max b // Account(2)
```

