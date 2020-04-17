# seq-wrapper
Augmentations for Scala's collections library.

### Sequences

#### splitMap[U](mapper: ((Seq[T], T, Seq[T]) => U)): Seq[U]
Map the elements of this sequence with access to the sub-sequences which come before and after each element.

#### lensMap(mapper: (T => T)): Seq[Seq[T]]
Create a sequence of sequences for each way to map only a single element of this sequence.

#### splitAll(): Seq[(Seq[T], Seq[T])]
Create a sequence of sequence pairs for each way to split this sequence into two sub-sequences.

#### removeEach(): Seq[Seq[T]]
Create a sequence of sequences for each way to remove only a single element from this sequence.
