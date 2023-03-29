package readchannel

import java.nio.ByteBuffer
import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.Configuration
import org.typelevel.discipline.Laws
import org.typelevel.discipline.scalatest.FunSuiteDiscipline
import scala.util.Try


trait ByteCodec[A] extends ByteDecoder[A] with ByteEncoder[A] {
  def encode(a: A): Array[Byte]
  def decode(b: Array[Byte]): Option[A]
}
object ByteCodec {
  def apply[A](implicit codec: ByteCodec[A]): ByteCodec[A] = codec
}

trait ByteCodecLaws[A] {
  def codec: ByteCodec[A]

  def isomorphism(a: A): Boolean =
    codec.decode(codec.encode(a)) == Some(a)
}

trait ByteCodecTests[A] extends Laws {
  def laws: ByteCodecLaws[A]

  def byteCodec(implicit ab: Arbitrary[A]): RuleSet = new DefaultRuleSet(
    name = "byteCodec",
    parent = None,
    props = "isomorphism" -> forAll(laws.isomorphism _)
  )
}

object ByteCodecTests {
  def apply[A](implicit bc: ByteCodec[A]): ByteCodecTests[A] = new ByteCodecTests[A]{
    def laws: ByteCodecLaws[A] = new ByteCodecLaws[A]{
      def codec: ByteCodec[A] = bc
    }
  }
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
      case None => Array[Byte]()
      case Some(value) => enc.encode(value)
    }
  }