package com.himewel.bytecodec

import java.nio.ByteBuffer
import scala.util.Try

trait ByteDecoder[A] {
  def decode(bytes: Array[Byte]): Option[A]
}

object ByteDecoder {
  implicit val stringDecoder: ByteDecoder[String] =
    instance[String](s => Try(new String(s)).toOption)
  implicit val intDecoder: ByteDecoder[Int] =
    instance[Int](i =>
      if (i.length != 4) None
      else {
        val bb = ByteBuffer.allocate(4)
        bb.put(i)
        bb.flip()
        Some(bb.getInt)
      }
    )

  def apply[A](implicit ev: ByteDecoder[A]): ByteDecoder[A] = ev
  def instance[A](f: Array[Byte] => Option[A]): ByteDecoder[A] = new ByteDecoder[A] {
    override def decode(array: Array[Byte]): Option[A] = f(array)
  }

  implicit class ByteDecoderOps(val bytes: Array[Byte]) extends AnyVal {
    def decode[A](implicit dec: ByteDecoder[A]): Option[A] =
      dec.decode(bytes)
  }
}
