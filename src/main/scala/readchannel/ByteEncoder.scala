package readchannel

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
        .allocate(8)
        .putInt(i)
        .array
    )

  def apply[A](implicit ev: ByteEncoder[A]): ByteEncoder[A] = ev
  def instance[A](f: A => Array[Byte]): ByteEncoder[A] = new ByteEncoder[A] {
    override def encode(a: A): Array[Byte] = f(a)
  }
}