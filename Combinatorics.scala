import scala.math._

object Combinatorics {

  def factorial(n: Long): Long =
    (1L to n).product

  def falling(n: Long, k: Long): Long =
    (n-k+1 to n).product

  def rising(n: Long, k: Long): Long =
    (n to n+k-1).product

  def subfactorial(n: Long): Long =
    (0L to n).foldLeft((-2L, 1L))
      {case ((u, v), k) => (v, (k-1)*(u+v))}._2

  def multifactorial(n: Long, k: Long): Long =
    ((n+1)%k+1 to n by k).product

  def permutations(n: Long, k: Long): Long =
    (n-k+1 to n).product

  def choose(n: Long, k: Long): Long =
    (max(k+1, n-k+1) to n).product /
      (1L to min(k, n-k)).product

  def multichoose(n: Long, k: Long): Long =
    choose(n+k-1, k)

  def figurate(n: Long, k: Long): Long =
    choose(n+k-1, n)

  def stirling1(n: Long, k: Long): Long =
    ???

  def stirling2(n: Long, k: Long): Long =
    (0L to k).map{i => (1-2*(i%2)) * choose(k, i) *
      pow(k-i, n).toLong}.sum / factorial(k)

  def lah(n: Long, k: Long): Long =
    choose(n-1, k-1) * (k+1 to n).product

  def bell(n: Long): Long =
    (0L to n).map{stirling2(n, _)}.sum

  def fubini(n: Long): Long =
    (0L to n).map{i => factorial(i) * stirling2(n, i)}.sum

  def eulerian1(n: Long, k: Long): Long =
    (0L to k).map{i => (1-2*(i%2)) * choose(n+1, i) *
      pow(k+1-i, n).toLong}.sum

  def eulerian2(n: Long, k: Long): Long =
    ???
}
