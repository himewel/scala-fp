import cats._
import cats.implicits._

case class Account(id: Int, number: String, balance: Double, owner: String)

object Account {
  implicit val toStringShow: Show[Account] = Show.fromToString

  object Instances {
    implicit val byOwnerAndbalance: Show[Account] = Show.show{ account =>
      s"${account.owner} - $$${account.balance}"
    }

    implicit val belongsToOwner: Show[Account] = Show.show{ account =>
      s"This account belongs to ${account.owner}"
    }
  }
}

val account = Account(1, "usd1-5.5", 2.3, "Logan")

Show[Account].show(account)
Account.toStringShow.show(account)
Account.Instances.byOwnerAndbalance.show(account)
Account.Instances.belongsToOwner.show(account)

import Account.Instances.byOwnerAndbalance

account.show