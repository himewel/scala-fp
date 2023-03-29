package readchannel

import java.nio.ByteBuffer
import scala.util.Try

trait Channel {
  def write[A](obj: A, dest: String)(implicit enc: ByteEncoder[A]): Unit
  def read[A](source: String)(implicit dec: ByteDecoder[A]): A
}
