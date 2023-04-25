package com.himewel.bytecodec

import java.nio.ByteBuffer

trait ByteEncoder[A] {
  def encode(obj: A): Array[Byte]
}

object ByteEncoder {
  implicit val stringEncoder: ByteEncoder[String] =
    instance[String](_.getBytes)
  implicit val intEncoder: ByteEncoder[Int] =
    instance[Int](i =>
      ByteBuffer
        .allocate(4)
        .putInt(i)
        .array
    )

  def apply[A](implicit ev: ByteEncoder[A]): ByteEncoder[A] = ev
  def instance[A](f: A => Array[Byte]): ByteEncoder[A] = new ByteEncoder[A] {
    override def encode(a: A): Array[Byte] = f(a)
  }

  implicit def optionByteEncoder[A](implicit enc: ByteEncoder[A]): ByteEncoder[Option[A]] =
    new ByteEncoder[Option[A]] {
      def encode(obj: Option[A]): Array[Byte] = obj match {
        case None        => Array[Byte]()
        case Some(value) => enc.encode(value)
      }
    }

  implicit class ByteEncoderOps[A](val a: A) extends AnyVal {
    def encode(implicit enc: ByteEncoder[A]): Array[Byte] =
      enc.encode(a)
  }
}
