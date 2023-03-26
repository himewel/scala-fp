package com.himewel.readchannel

object Main extends App {
  val arrayTest: Array[Byte] = Array(98, 105, 101, 110, 32, 58, 41)
  val message = ByteDecoder[String].decode(arrayTest)
  message match {
    case Some(m) => println(m)
    case None => println("error decoding message")
  }
}
