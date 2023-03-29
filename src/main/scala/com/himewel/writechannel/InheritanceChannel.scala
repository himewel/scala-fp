package com.himewel.writechannel

import scala.util.Using
import java.io.FileOutputStream
import java.nio.ByteBuffer

trait ByteEncodable {
  def encode(): Array[Byte]
}

trait InheritanceChannel {
  def write(obj: ByteEncodable, objDestination: String): Unit
}

object InheritanceFileChannel extends InheritanceChannel {
  def write(obj: ByteEncodable, objDestination: String): Unit = {
    val byteObj = obj.encode()

    Using(new FileOutputStream(objDestination)) { os =>
      os.write(byteObj)
      os.flush()
    }
  }
}

case class Company(companyName: String, position: String, salary: Float) extends ByteEncodable {
  def encode(): Array[Byte] = this.toString.getBytes
  override def toString(): String =
    "Company -> (" +
      Seq(companyName, position, salary)
        .map(_.toString)
        .reduce(_ + ", " + _) +
      ")"
}

case class Person(firstName: String, lastName: String, company: Company) extends ByteEncodable {
  def encode(): Array[Byte] = this.toString.getBytes
  override def toString(): String =
    "Person -> (" +
      Seq(firstName, lastName, company)
        .map(_.toString)
        .reduce(_ + ", " + _) +
      ")"
}
