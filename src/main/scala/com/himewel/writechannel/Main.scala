package com.himewel.writechannel

object DestinationEnum extends Enumeration {
  val any = "./output/AnyFileChannel.txt"
  val inheritence = "./output/InheritanceFileChannel.txt"
  val typeClass = "./output/TypeClassFileChannel.txt"
  val implicitTypeClass = "./output/ImplicitClassFileChannel.txt"
}

object Main extends App {
  val person = Person("Milly", "Brown", Company("Netflix", "Actor", 1000))

  AnyFileChannel.write(person.toString, DestinationEnum.any)
  InheritanceFileChannel.write(person, DestinationEnum.inheritence)
  TypeClassFileChannel.write(person, ByteEncoder.PersonByteEncoder, DestinationEnum.typeClass)
  ImplicitFileChannel.write(person, DestinationEnum.implicitTypeClass)
  ImplicitFileChannel.write(Switch(true), "./output/switch.txt")

  println(ByteEncoder.apply[Int])
  println(ByteEncoder[Int])
}
