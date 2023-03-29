package com.himewel.laws

import com.himewel.bytecodec._

trait ByteCodecLaws[A] {
  def codec: ByteCodec[A]

  def isomorphism(a: A): Boolean =
    codec.decode(codec.encode(a)) == Some(a)
}