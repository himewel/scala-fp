package com.himewel.writechannel

case class Switch(isOn: Boolean)
object Switch {
  implicit object SwitchByteEncoder extends ByteEncoder[Switch] {
    def encode(a: Switch): Array[Byte] =
      (if (a.isOn) "1" else "0").getBytes
  }

  object Instances {
    implicit val rot3StringByteEncoder: ByteEncoder[String] =
      ByteEncoder.instance[String](_.getBytes.map(i => (i + 3).toByte))
  }
}
