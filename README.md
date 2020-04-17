# seq-wrapper
Augmentations for Scala's collections library.

### SeqWrapper[T](seq: Seq[T])
Additional features for Scala's sequences.<br>
@param seq sequence to augment<br>
@tparam T sequence element type<br>
@author Alec Dorrington<br>

#### splitMap[U](mapper: ((Seq[T], T, Seq[T]) => U)): Seq[U]
Map the elements of this sequence with access to the sub-sequences which come before and after each element.<br>
@tparam U resulting element type<br>
@return mapped sequence<br>

#### lensMap(mapper: (T => T)): Seq[Seq[T]]
Create a sequence of sequences for each way to map only a single element of this sequence.<br>
@param mapper element => result<br>
@return sequence of possible singular mappings<br>

#### splitAll(): Seq[(Seq[T], Seq[T])]
Create a sequence of sequence pairs for each way to split this sequence into two sub-sequences.<br>
@return sequence of possible splits<br>

#### removeEach(): Seq[Seq[T]]
Create a sequence of sequences for each way to remove only a single element from this sequence.<br>
@return sequence of possible element removals<br>
