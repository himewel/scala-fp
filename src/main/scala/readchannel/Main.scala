package com.himewel.readchannel

object Main extends App {
  val arrayTest: Array[Byte] = Array(98, 105, 101, 110, 32, 58, 41)
  println(ByteDecoder[String].decode(arrayTest))
}
