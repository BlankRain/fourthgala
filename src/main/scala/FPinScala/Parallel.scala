package FPinScala

import java.util.concurrent._

object Par {
  type Par[A] = ExecutorService => Future[A]

  def unit[A](a: A): Par[A] = (es: ExecutorService) => UnitFuture(a)
  def lazyUnit[A](a: => A): Par[A] = fork(unit(a))
  def asyncF[A, B](f: A => B): A => Par[B] = a => lazyUnit(f(a))

  private case class UnitFuture[A](get: A) extends Future[A] {
    def isDone = true
    def get(timeout: Long, units: TimeUnit) = get
    def isCancelled = false
    def cancel(evenIfRunning: Boolean): Boolean = false
  }

  def map2[A, B, C](a: Par[A], b: Par[B])(f: (A, B) => C)
                   (timeout: Long = 5, unit: TimeUnit = TimeUnit.MINUTES): Par[C] =
    (es: ExecutorService) => {
      val af = a(es)
      val bf = b(es)
      UnitFuture(f(af.get(timeout, unit), bf.get(timeout, unit)))
    }

  def fork[A](a: => Par[A]): Par[A] =
    es => es.submit(new Callable[A] {
      def call(): A = a(es).get
    })

}
