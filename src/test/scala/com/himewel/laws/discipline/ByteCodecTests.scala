package com.himewel.laws.discipline

import com.himewel.bytecodec._
import com.himewel.laws._

import org.scalacheck.Arbitrary
import org.scalacheck.Prop.forAll
import org.typelevel.discipline.Laws


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