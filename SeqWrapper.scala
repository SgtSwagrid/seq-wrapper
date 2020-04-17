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
}

object SeqWrapper {
  implicit def wrapSeq[T](seq: Seq[T]): SeqWrapper[T] =
    new SeqWrapper(seq)
}
