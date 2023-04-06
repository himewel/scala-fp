package com.himewel.bytecodec

import java.nio.ByteBuffer
import scala.util.Try

trait ByteCodec[A] extends ByteDecoder[A] with ByteEncoder[A] {
  def encode(a: A): Array[Byte]
  def decode(b: Array[Byte]): Option[A]
}
object ByteCodec {
  def apply[A](implicit codec: ByteCodec[A]): ByteCodec[A] = codec
}

implicit object IntByteCodec extends ByteCodec[Int] {
  def encode(a: Int): Array[Byte] =
    ByteBuffer
      .allocate(4)
      .putInt(a)
      .array()

  def decode(a: Array[Byte]): Option[Int] =
    if (a.length != 4) None
    else {
      val bb = ByteBuffer.allocate(4)
      bb.put(a)
      bb.flip()
      Some(bb.getInt)
    }
}

implicit object StringByteCodec extends ByteCodec[String] {
  def encode(a: String): Array[Byte] = a.getBytes()
  def decode(a: Array[Byte]): Option[String] = Try(new String(a)).toOption
}

implicit def optionByteEncoder[A](implicit enc: ByteEncoder[A]): ByteEncoder[Option[A]] =
  new ByteEncoder[Option[A]] {
    def encode(obj: Option[A]): Array[Byte] = obj match {
      case None        => Array[Byte]()
      case Some(value) => enc.encode(value)
    }
  }
