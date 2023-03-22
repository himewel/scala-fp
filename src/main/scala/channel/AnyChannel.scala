import scala.util.Using
import java.io.FileOutputStream
import java.nio.ByteBuffer

trait AnyChannel {
  def write(obj: Any, objDestination: String): Unit
}

object AnyFileChannel extends AnyChannel {
  def write(obj: Any, objDestination: String): Unit = {
    val byteArray: Array[Byte] = obj match {
      case (s: String) => s.getBytes()
      case (n: Int) =>
        val buffer = ByteBuffer.allocate(4)
        buffer.putInt(n)
        buffer.array()
      case anyValue => throw Exception("Type not handled")
    }

    Using(new FileOutputStream(objDestination)) { os =>
      os.write(byteArray)
      os.flush()
    }
  }
}
