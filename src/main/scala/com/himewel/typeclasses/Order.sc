import cats._
import cats.implicits._
import scala.collection.immutable.Range.Partial

case class Account(id: Long, number: String, balance: Double, owner: String)

object Account {
  implicit val orderById: Order[Account] = 
    Order.from((a, b) => Order.compare(a.id, b.id))

  object Instances {
    implicit val orderByNumber: Order[Account] = Order.by(_.number)
    //   Order.from((a, b) => orderString.compare(a.number, b.number))
      
    implicit val orderByBalance: Order[Account] = Order.by(_.balance)
  }
}

def sort[A](list: List[A])(implicit orderA: Order[A]): List[A] = {
  list.sorted(orderA.toOrdering)
}

val account1 = Account(0, "1234:5678:bcdf:acd0", 10.5, "Sylvester Stallone")
val account2 = Account(1, "1234:5678:bcdf:acd0", 100.0, "Arnold Schwarzenegger")
val list = List(account2, account1)

sort[Account](list)
sort[Account](list)(Account.Instances.orderByBalance)
sort[Account](list)(Account.Instances.orderByNumber)

account1 compare account2
account1 min account2
account1 max account2

implicit def orderByIdDesc: Order[Account] = Order.reverse(Account.orderById) 
sort[Account](list)


