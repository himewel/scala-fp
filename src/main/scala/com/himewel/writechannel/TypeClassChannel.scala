package com.himewel.writechannel

import scala.util.Using
import java.io.FileOutputStream
import java.nio.ByteBuffer

trait ByteEncoder[A] {
  def encode(a: A): Array[Byte]
}

object ByteEncoder {
  implicit val stringByteEncoder: ByteEncoder[String] = instance[String](_.getBytes)

  implicit object IntByteEncoder extends ByteEncoder[Int] {
    def encode(a: Int): Array[Byte] =
      ByteBuffer
        .allocate(8)
        .putInt(a)
        .array
  }

  implicit object PersonByteEncoder extends ByteEncoder[Person] {
    def encode(a: Person): Array[Byte] = ByteEncoder[String].encode(a.toString)
  }

  def apply[A](implicit ev: ByteEncoder[A]): ByteEncoder[A] = ev

  def instance[A](f: A => Array[Byte]): ByteEncoder[A] = new ByteEncoder[A] {
    def encode(a: A): Array[Byte] = f(a)
  }
}

trait TypeClassChannel {
  def write[A](obj: A, enc: ByteEncoder[A], objDestination: String): Unit
}

object TypeClassFileChannel extends TypeClassChannel {
  def write[A](obj: A, enc: ByteEncoder[A], objDestination: String): Unit = {
    val byteObj = enc.encode(obj)

    Using(new FileOutputStream(objDestination)) { os =>
      os.write(byteObj)
      os.flush()
    }
  }
}
