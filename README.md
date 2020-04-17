# seq-wrapper
Augmentations for Scala's collections library.

### Sequences
Additional features for Scala's sequences.
@param seq sequence to augment
@tparam T sequence element type
@author Alec Dorrington

#### splitMap[U](mapper: ((Seq[T], T, Seq[T]) => U)): Seq[U]
Map the elements of this sequence with access to the sub-sequences which come before and after each element.
@tparam U resulting element type
@return mapped sequence

#### lensMap(mapper: (T => T)): Seq[Seq[T]]
Create a sequence of sequences for each way to map only a single element of this sequence.
@param mapper element => result
@return sequence of possible singular mappings

#### splitAll(): Seq[(Seq[T], Seq[T])]
Create a sequence of sequence pairs for each way to split this sequence into two sub-sequences.
@return sequence of possible splits

#### removeEach(): Seq[Seq[T]]
Create a sequence of sequences for each way to remove only a single element from this sequence.
@return sequence of possible element removals
