# Task: Arrays & Strings In-Place

Four exercises applying the two-pointer technique and in-place mutation
from [`ARRAYS_AND_STRINGS_NOTES.md`](ARRAYS_AND_STRINGS_NOTES.md).

Create `src/main/kotlin/arrays/ArraysAndStrings.kt` yourself (copy the stub
below in, then implement each function).

## Operations

**`reverseArray(arr: IntArray): Unit`**
Reverses `arr` in place — no new array allocated. `reverseArray([1,2,3])`
leaves `arr` as `[3,2,1]`. Handle empty and single-element arrays (both
should be left unchanged, not throw).

**`reverseString(chars: CharArray): Unit`**
Same idea, on a `CharArray` instead of an `IntArray` — reverses in place.
`reverseString(['h','e','l','l','o'])` leaves it as `['o','l','l','e','h']`.
This takes a `CharArray` rather than a `String` for a reason you already
know from `ARRAYS_AND_STRINGS_NOTES.md` — think about why before you start.

**`isPalindrome(s: String): Boolean`**
Checks whether `s` reads the same forwards and backwards — same contract
as Phase 0's version (empty and single-character strings are palindromes,
case-sensitive), but this time write it **iteratively** with two indices
instead of recursively.
*Framing: what should happen the moment the characters at your two
indices don't match? If they do match, what update do you make to both
indices before checking again — and what tells the loop to stop?*

**`removeDuplicates(arr: IntArray): Int`**
`arr` is sorted in ascending order and may contain duplicates. Remove the
duplicates in place so each unique value appears once, keeping the
original relative order, and return the count of unique values. The
content of `arr` past the returned count doesn't matter — only the first
`count` elements need to be correct. Example: `arr = [1,1,2,2,3]` →
returns `3`, and `arr`'s first three elements are `[1,2,3]` (whatever's
after that is irrelevant). Empty array returns `0`.
*Framing: this isn't the converging-ends pattern from the other three —
both pointers move in the same direction, just not always together. Think
of it as one pointer that scans every element, and a second pointer that
only advances (and writes) when it finds a value different from the last
one it kept. When exactly should that second pointer move?*

## Stub

```kotlin
package arrays

fun reverseArray(arr: IntArray) {
    TODO()
}

fun reverseString(chars: CharArray) {
    TODO()
}

fun isPalindrome(s: String): Boolean {
    TODO()
}

fun removeDuplicates(arr: IntArray): Int {
    TODO()
}
```
