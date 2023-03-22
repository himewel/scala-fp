import scala.util.Using
import java.io.FileOutputStream
import java.nio.ByteBuffer

trait ImplicitChannel {
  def write[A](obj: A, objDestination: String)(implicit enc: ByteEncoder[A]): Unit
}

object ImplicitFileChannel extends ImplicitChannel {
  def write[A](obj: A, objDestination: String)(implicit enc: ByteEncoder[A]): Unit = {
    val byteObj = enc.encode(obj)

    Using(new FileOutputStream(objDestination)) { os =>
      os.write(byteObj)
      os.flush()
    }
  }
}