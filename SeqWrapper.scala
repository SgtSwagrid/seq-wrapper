import SeqWrapper._
import scala.annotation.tailrec

/**
  * Additional features for Scala's sequences.
  * @param seq sequence to augment
  * @tparam T sequence element type
  * @author Alec Dorrington
  */
class SeqWrapper[T](seq: Seq[T]) {

  /**
    * Map the elements of this sequence with access to the
    * sub-sequences which come before and after each element.
    * @param mapper (left, current, right) => result
    * @tparam U resulting element type
    * @return mapped sequence
    */
  def splitMap[U](mapper: ((Seq[T], T, Seq[T]) => U)): Seq[U] =
    (1 to seq.size).foldLeft(Seq[T](), seq, Seq[U]()) {
      case ((l, c :: r, o), _) =>
        (l :+ c, r, o :+ mapper(l, c, r))
    }._3

  /**
    * Create a sequence of sequences for each way to
    * map only a single element of this sequence.
    * @param mapper element => result
    * @return sequence of possible singular mappings
    */
  def lensMap(mapper: (T => T)): Seq[Seq[T]] =
    splitMap{(l, c, r) => (l :+ mapper(c)) ++ r}

  /**
    * Map only the elements in a range,
    * keeping everything else the same.
    * @param from index at which to start mapping
    * @param to index at which to end mapping
    * @param mapper  element => result
    * @return sequence of partially mapped values
    */
  def mapRange(from: Int = 0, to: Int = -1)(mapper: T => T): Seq[T] = {
    val toIdx = if (to != -1) to else from
    seq.take(from) ++ seq.range(from, to).map(mapper) ++
      seq.takeRight(seq.size - toIdx - 1)
  }

  /**
    * Map only the elements which match a predicate,
    * keeping everything else the same.
    * @param cond condition for mapping to occur
    * @param mapper element => result
    * @return sequence of partially mapped values
    */
  def mapIf(cond: T => Boolean)(mapper: T => T): Seq[T] =
    seq.map{e => if(cond(e)) mapper(e) else e}

  /**
    * Create a sequence of sequence pairs for each way
    * to split this sequence into two sub-sequences.
    * @return sequence of possible splits
    */
  def splitAll(): Seq[(Seq[T], Seq[T])] =
    splitMap{(l, c, r) => (l, c +: r)} :+ (seq, Seq())

  /**
    * Find all ways to partition the elements of this
    * sequence into n sets of particular sizes.
    * @param sizes the sizes of each respective set
    * @return sequence of all possible partitions
    */
  def partitionAll(sizes: Int*): Seq[Seq[Seq[T]]] =
    if(sizes.toSeq.anyMatch{_ < 0}) Seq()
    else if(seq.isEmpty) Seq(Seq.fill(sizes.size)(Seq()))
    else sizes.toSeq.zipWithIndex.flatMap {case (s, i) =>
      seq.tail.partitionAll(sizes.updated(i, s-1) :_*)
        .map{_.mapRange(i){seq.head +: _}}
    }

  /**
    * Create a sequence of sequences for each way to
    * remove only a single element from this sequence.
    * @return sequence of possible element removals
    */
  def removeEach(): Seq[Seq[T]] =
    splitMap{(l, _, r) => l ++ r}

  /**
    * Fold so long as a condition is met.
    * @param base the starting fold value
    * @param cond the condition to keep folding
    * @param fold the folding function
    * @tparam U resulting type
    * @return result of partial fold
    */
  def foldWhile[U](base: U)(cond: U => Boolean)(fold: ((U, T) => U)): U =
    seq.foldLeft(base){(r, e) => if(cond(r)) fold(r, e) else r}


  /**
    * Fold until a condition is met.
    * @param base the starting fold value
    * @param cond the condition to stop folding
    * @param fold the folding function
    * @tparam U resulting type
    * @return result of partial fold
    */
  def foldUntil[U](base: U)(cond: U => Boolean)(fold: ((U, T) => U)): U =
    foldWhile(base)(!cond(_))(fold)

  /**
    * Create a list of each element k paired with element n-k-1.
    * In an odd-sized list, the central element will be paired with itself.
    * @return list of element pairs
    */
  def pairEnds(): Seq[(T, T)] =
    (1 to seq.size).foldLeft((seq, seq.reverse, Seq[(T, T)]())) {
      case ((e1 +: p1, e2 +: p2, o), _) => (p1, p2, o :+ (e1, e2))
    }._3

  /**
    * Create a sequence of sequences starting from the original sequence
    * where each subsequent sequence has the first element removed.
    * @return sequence of possible suffixes.
    */
  def stepped(): Seq[Seq[T]] =
    splitMap{(_, c, r) => c +: r}

  /**
    * Create a sequence of sequences starting from the original sequence
    * where each subsequent sequence has the last element removed.
    * @return sequence of possible prefixes.
    */
  def steppedRight(): Seq[Seq[T]] =
    splitMap{(l, c, _) => l :+ c}

  /**
    * Create a sequence of sequences for each possible
    * unordered subset of the elements in this sequence.
    * Element order is preserved, and elements will not be repeated.
    * @param min minimum number of elements in each subset
    * @param max maximum number of elements in each subset
    * @return sequence of possible subsets
    */
  def subsets(min: Int = 0, max: Int = seq.size): Seq[Seq[T]] =
    if(min > seq.size) Seq()
    else if(max == 0 || seq.isEmpty) Seq(Seq())
    else seq.tail.subsets(min-1, max-1).map{seq.head +: _} ++
      seq.tail.subsets(min, max)

  /**
    * Create a sequence of sequences for each possible
    * unordered multiset of the elements in this sequence.
    * Element order is preserved, and elements may be repeated.
    * @param min minimum number of elements in each multiset
    * @param max maximum number of elements in each multiset
    * @return sequence of possible multisets
    */
  def multisets(min: Int = 0, max: Int): Seq[Seq[T]] =
    if(min > 0 && seq.isEmpty) Seq()
    else if(max == 0 || seq.isEmpty) Seq(Seq())
    else multisets(min-1, max-1).map{seq.head +: _} ++
      seq.tail.multisets(min, max)

  /**
    * Create a sequence with the elements of this sequence rotated by n spaces.
    * @param n number of spaces to rotate, with sign for direction
    * @return rotated sequence
    */
  def rotate(n: Int = 1): Seq[T] =
    if(n >= 0) seq.takeRight(n % seq.size) ++ seq.dropRight(n % seq.size)
    else seq.drop(-n % seq.size) ++ seq.take(-n % seq.size)

  /**
    * Create a sequence of sequences for every way to rotate this sequence.
    * @return sequence of possible rotations
    */
  def rotateAll(): Seq[Seq[T]] =
    (0 until seq.size).map{rotate(_)}

  /**
    * Remove all of the elements not in a range.
    * @param from index of first element to keep
    * @param to index of last element to keep
    * @return sequence with range kept
    */
  def range(from: Int, to: Int = -1): Seq[T] = {
    val toIdx = if (to != -1) to else from
    seq.drop(from).dropRight(seq.size - toIdx - 1)
  }

  /**
    * Remove all of the elements in a range.
    * @param from index of first element to remove
    * @param to index of last element to remove
    * @return sequence with range removed
    */
  def remove(from: Int, to: Int = -1): Seq[T] = {
    val toIdx = if (to != -1) to else from
    seq.take(from) ++ seq.takeRight(seq.size - toIdx - 1)
  }

  /**
    * Determines whether this sequences contains an element matching a predicate.
    * @param cond the condition to test for
    * @return whether this condition is met by any element
    */
  def anyMatch(cond: T => Boolean): Boolean =
    seq.find(cond).isDefined

  /**
    * Search for an element which matches a predicate so
    * long as some bound isn't met, otherwise give up.
    * @param loopCond the condition for stopping
    * @param matchCond the condition to test for
    * @return whether this condition is met by any element
    */
  def anyWhile(loopCond: T => Boolean)(matchCond: T => Boolean): Boolean =
    seq.foldWhile((true, false)) {case (c, r) => c && !r} {
      (_, e) => (loopCond(e), matchCond(e))
    }._2

  /**
    * Create a sequence of a given size, cyclically
    * using the elements of this sequence.
    * @param size elements in new sequence
    * @return cyclic sequence
    */
  def cyclicPad(size: Int): Seq[T] =
    if(size <= seq.size) seq.take(size)
    else (seq ++ seq).cyclicPad(size)

  /**
    * Determine how many elements in a row from the start
    * from both sequences correspond with each other.
    * @param that list to check correspondence with
    * @param cond condition for correspondence
    * @tparam U type of other list
    * @return the number of prefix elements which correspond
    */
  def prefixCorrespondence[U](that: Seq[U])(cond: (T, U) => Boolean): Int =
    repeat((0, seq, that))
      {case (_, h1 :: _, h2 :: _) => cond(h1, h2)}
      {case (i, _ :: t1, _ :: t2) => (i+1, t1, t2)}._1
}

object SeqWrapper {

  implicit def wrapSeq[T](seq: Seq[T]): SeqWrapper[T] =
    new SeqWrapper(seq.toList)

  /**
    * Repeat an action until a condition is met.
    * @param base the starting fold value
    * @param cond the condition for continuing
    * @param fold the folding function
    * @tparam T the value type being folded
    * @return the resulting value
    */
  @tailrec
  def repeat[T](base: T)(cond: T => Boolean)(fold: (T => T)): T =
    if(!cond(base)) base
    else repeat(fold(base))(cond)(fold)

  /**
    * Repeat an action until a condition is met.
    * No check is made prior to the first iteration.
    * @param base the starting fold value
    * @param cond the condition for continuing
    * @param fold the folding function
    * @tparam T the value type being folded
    * @return the resulting value
    */
  def repeatFirst[T](base: T)(fold: (T => T))(cond: T => Boolean): T = {
    repeat(fold(base))(cond)(fold)
  }
}
