import cats._
import cats.implicits._

sealed trait MOption[+A] {
  def flatMap[A, B](f: A => MOption[B]): MOption[B] =
    this match {
      case MSome(value) => f(value)
      case MNone        => MNone
    }
}
case class MSome[+A](value: A) extends MOption[A]
case object MNone extends MOption[Nothing]

case class Person(name: String)
case class Account(balance: Double, owner: Person)
case class Transfer(source: Account, destination: Account, amount: Double)

def findPersonByName(name: String): MOption[Person]
def findAccountbyPerson(person: Person): MOption[Account]
def findLastTransferBySourceAccount(account: Account): MOption[Transfer]
def findLastTransferByPersonName(name: String): MOption[Transfer] =
//   findPersonByName(name) match {
//     case MSome(person) =>
//       findAccountByPerson(person) match {
//         case MSome(account) => findLastTransferBySourceAccount(account)
//         case MNone          => MNone
//       }
//     case MNone => MNone
//   }

//   findPersonByName(name).flatMap { person =>
//     findAccountbyPerson(person).flatMap { acc =>
//       findLastTransferBySourceAccount(acc)
//     }
//   }

  for {
    person <- findPersonByName(name)
    acc <- findAccountByPerson(person)
    transfer <- findLastTransferBySourceAccount(acc)
  } yield transfer
