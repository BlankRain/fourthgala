package FPinScala

object _f extends App {

  trait Functor[F[_]] {

    def map[A, B](fa: F[A])(f: A => B): F[B]

    // unzip
    def distribute[A, B](fab: F[(A, B)]): (F[A], F[B]) =
      (map(fab)(_._1), map(fab)(_._2))

    def codistribute[A, B](e: Either[F[A], F[B]]): F[Either[A, B]] = e match {
      case Left(fa) => map(fa)(Left(_))
      case Right(fa) => map(fa)(Right(_))
    }
  }

}