import cats._
import cats.implicits._

case class Account(id: Long, number: String, balance: Double, owner: String)

object Account {
  implicit val universalEq: Eq[Account] = Eq.fromUniversalEquals // ==

  object Instances {
    implicit def byIdEq(implicit eqLong: Eq[Long]): Eq[Account] =
      Eq.instance[Account]((a, b) => eqLong.eqv(a.id, b.id))
    implicit def byIdEq2(implicit eqLong: Eq[Long]): Eq[Account] = Eq.by(_.id)

    implicit def byNumber(implicit eqString: Eq[String]): Eq[Account] =
      Eq.by(_.number)
  }
}

val account1 = Account(0, "1234:5678:bcdf:acd0", 10.5, "Sylvester Stallone")
val account2 = Account(1, "1234:5678:bcdf:acd0", 100.0, "Arnold Schwarzenegger")

account1 === account2


Eq[Account].eqv(account1, account2)
Account.Instances.byIdEq.eqv(account1, account2)
Account.Instances.byNumber.eqv(account1, account2)


implicit def eq: Eq[Account] = Account.Instances.byNumber
Eq[Account].eqv(account1, account2)

import Account.Instances.byIdEq2
Eq[Account].eqv(account1, account2)
