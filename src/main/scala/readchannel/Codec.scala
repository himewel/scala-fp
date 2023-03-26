trait Codec[A] extends ByteDecoder[A] with ByteEncoder[A] {
  def encode(a: A): Array[Byte]
  def decode(b: Array[Byte]): Option[A]
}

def isomorphism(a: A)(implicit codec: Codec[A]): Boolean = 
  Codec.decode(Codec.encode(a)) == Some(a)