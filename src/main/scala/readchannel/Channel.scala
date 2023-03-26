package com.himewel.readchannel

import java.nio.ByteBuffer
import scala.util.Try

trait Channel {
  def write[A](obj: A, dest: String)(implicit enc: ByteEncoder[A]): Unit
  def read[A](source: String)(implicit dec: ByteDecoder[A]): A
}

trait ByteEncoder[A] {
  def encode(obj: A): Array[Byte]
}
object ByteEncoder {
  implicit val stringEncoder: ByteEncoder[String] =
    instance[String](_.getBytes)
  implicit val intEncoder: ByteEncoder[Int] =
    instance[Int](i =>
      ByteBuffer
        .allocate(8)
        .putInt(i)
        .array
    )

  def apply[A](implicit ev: ByteEncoder[A]): ByteEncoder[A] = ev
  def instance[A](f: A => Array[Byte]): ByteEncoder[A] = new ByteEncoder[A] {
    override def encode(a: A): Array[Byte] = f(a)
  }
}

trait ByteDecoder[A] {
  def decode(bytes: Array[Byte]): Option[A]
}
object ByteDecoder {
  implicit val stringDecoder: ByteDecoder[String] =
    instance[String](s => Try(new String(s)).toOption)
  implicit val intDecoder: ByteDecoder[Int] =
    instance[Int](i => Try(ByteBuffer.wrap(i).getInt).toOption)

  def apply[A](implicit ev: ByteDecoder[A]): ByteDecoder[A] = ev
  def instance[A](f: Array[Byte] => Option[A]): ByteDecoder[A] = new ByteDecoder[A] {
    override def decode(array: Array[Byte]): Option[A] = f(array)
  }
}
