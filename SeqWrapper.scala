import SeqWrapper._

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
    * Create a sequence of sequence pairs for each way
    * to split this sequence into two sub-sequences.
    * @return sequence of possible splits
    */
  def splitAll(): Seq[(Seq[T], Seq[T])] =
    splitMap{(l, c, r) => (l, c +: r)} :+ (seq, Seq())

  /**
    * Create a sequence of sequences for each way to
    * remove only a single element from this sequence.
    * @return sequence of possible element removals
    */
  def removeEach(): Seq[Seq[T]] =
    splitMap{(l, _, r) => l ++ r}

  /**
    * Create a sequence of sequences starting from the original sequence
    * where each subsequent sequence has the first element removed.
    * @return sequence of possible suffixes.
    */
  def stepped(): Seq[Seq[T]] =
    splitMap{(_, c, r) => c +: r}

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
    * Remove all of the elements in a range.
    * @param from index of first element to remove
    * @param to index of last element to remove
    * @return sequence with index removed
    */
  def remove(from: Int, to: Int = -1): Seq[T] = {
    val toIdx = if (to != -1) to else from
    seq.take(from) ++ seq.takeRight(seq.size - toIdx - 1)
  }
}

object SeqWrapper {
  implicit def wrapSeq[T](seq: Seq[T]): SeqWrapper[T] =
    new SeqWrapper(seq.toList)
}
