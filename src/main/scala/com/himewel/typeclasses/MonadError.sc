import cats._
import cats.implicits._

import java.io.IOException
import scala.util._

trait HttpMethod
case object Get extends HttpMethod
case class HttpRequest(method: HttpMethod, url: String)
case class HttpResponse(status: Int)

def doRequest(req: HttpRequest): HttpResponse =
  if (math.random() < 0.5) throw new IOException("boom!")
  else HttpResponse(200)

def executeRequest(req: HttpRequest): Option[HttpResponse] =
  try {
    Some(doRequest(req))
  } catch {
    case _: Exception => None
  }

def executeRequest2(req: HttpRequest): Either[String, HttpResponse] =
  try {
    Right(doRequest(req))
  } catch {
    case _: Exception => Left("Sorry :(")
  }

def executeRequest3(req: HttpRequest): Try[HttpResponse] =
  try {
    Success(doRequest(req))
  } catch {
    case e: Exception => Failure(e)
  }

implicit val optionME: MonadError[Option, Unit] = new MonadError[Option, Unit] {
  def raiseError[A](e: Unit): Option[A] = None

  def handleErrorWith[A](fa: Option[A])(f: Unit => Option[A]): Option[A] =
    fa.orElse(f(()))

  def pure[A](x: A): Option[A] = Some(x)

  def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] = fa.flatMap(f)

  def tailRecM[A, B](a: A)(f: A => Option[Either[A, B]]): Option[B] = ???
}

def executeRequestME[F[_], E](req: HttpRequest)(f: Exception => E)(implicit monadError: MonadError[F, E]): F[HttpResponse] =
  try {
    monadError.pure(doRequest(req))
  } catch {
    case e: Exception => monadError.raiseError(f(e))
  }

val request = HttpRequest(Get, "www.example.com")
println(executeRequestME[Try, Throwable](request)(_))

type ErrorOr[A] = Either[String, A]
executeRequestME[ErrorOr, String](request)((e: Exception) => e.getMessage)

println(executeRequestME[Option, Unit](request)((e: Exception) => ()))