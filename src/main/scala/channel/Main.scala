object DestinationEnum extends Enumeration {
  val any = "./AnyFileChannel.txt"
  val inheritence = "./InheritanceFileChannel.txt"
  val typeClass = "./TypeClassFileChannel.txt"
  val implicitTypeClass = "./TypeClassFileChannel.txt"
}

object Main extends App {
  val person = Person("Milly", "Brown", Company("Netflix", "Actor", 1000))

  AnyFileChannel.write(person.toString, DestinationEnum.any)
  InheritanceFileChannel.write(person, DestinationEnum.inheritence)
  TypeClassFileChannel.write(person, PersonByteEncoder, DestinationEnum.typeClass)
  ImplicitFileChannel.write(person, DestinationEnum.implicitTypeClass)
}
