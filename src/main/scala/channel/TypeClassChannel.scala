import scala.util.Using
import java.io.FileOutputStream
import java.nio.ByteBuffer

trait ByteEncoder[A] {
  def encode(a: A): Array[Byte]
}

object ByteEncoder {
  implicit object StringByteEncoder extends ByteEncoder[String] {
    def encode(a: String): Array[Byte] = a.getBytes
  }

  implicit object IntByteEncoder extends ByteEncoder[Int] {
    def encode(a: Int): Array[Byte] =
      ByteBuffer
      .allocate(8)
      .putInt(a)
      .array
    }

  implicit object PersonByteEncoder extends ByteEncoder[Person] {
    def encode(a: Person): Array[Byte] = StringByteEncoder.encode(a.toString)
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
