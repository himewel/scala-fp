package com.himewel

import com.himewel.bytecodec._
import com.himewel.bytecodec.ByteDecoder._
import com.himewel.bytecodec.ByteEncoder._
import com.himewel.laws.discipline._

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.prop.Configuration
import org.typelevel.discipline.scalatest.FunSuiteDiscipline

class ByteCodecSpec extends AnyFunSuite with Configuration with FunSuiteDiscipline {
  checkAll("ByteCodec[Int]", ByteCodecTests[Int].byteCodec)
  checkAll("ByteCodec[String]", ByteCodecTests[String].byteCodec)
}

class ByteEncoder extends AnyFunSuite {
  test("should encode same value in explicit call") {
    val intEncoding = 5.encode
    assert(intEncoding sameElements ByteCodec[Int].encode(5))

    val stringEncoding = "my test".encode
    assert(stringEncoding sameElements ByteCodec[String].encode("my test"))
  }
}

class ByteDecoder extends AnyFunSuite {
  test("should decode same value in explicit call") {
    val arrayTest: Array[Byte] = Array(98, 105, 101, 110, 32, 58, 41)
    assert(arrayTest.decode[String] == Some("bien :)"))
  }

  test("should return None for invalid array") {
    val invalidArray: Array[Byte] = Array(0, 0, 0, 0, 5)
    assert(invalidArray.decode[Int] == None)
  }
}