# Arrays & Strings: In-Place Techniques

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md)

## Arrays: contiguous memory and what that buys you

An array stores its elements in one contiguous block of memory. That's the
entire reason index access is O(1): to find element `i`, you compute
`base_address + i * element_size` directly — no searching, no traversal.
This only works *because* the layout is contiguous and every element is
the same fixed size.

That same property is what makes insertion/deletion in the middle O(n):
to insert at index `i`, every element from `i` onward has to shift over by
one to keep the array contiguous. Removing works the same way in reverse.
Appending or removing at the *end* is the cheap case — no shifting needed.

```
insert "X" at index 2:

[ a, b, c, d, e ]          before
        ^ insert here

[ a, b, X, c, d, e ]       after — c, d, e all shifted right by one
```

Kotlin has two array-ish types worth distinguishing:
- `Array<T>` — fixed size, set at creation. No resizing possible.
- `MutableList<T>` (typically backed by `ArrayList`) — resizable. Appending
  is [amortized](../../../../GLOSSARY.md#amortized-complexity) O(1): most
  appends just write into unused backing-array space, but occasionally the
  backing array is full and a resize (allocate bigger array, copy
  everything over) happens, which is O(n) for that one call.

## Strings are immutable

In Kotlin (running on the JVM), `String` is immutable — there is no such
thing as modifying a `String` in place. Every operation that looks like a
mutation (`+`, `replace`, `substring`) actually allocates a brand new
`String` and leaves the original untouched. This has a real cost: building
up a string via repeated concatenation in a loop is O(n²) overall, because
each `+` copies everything accumulated so far into a new object.

For an algorithm that's supposed to work "in place" on a string, the
practical move is to convert to a `CharArray` (`s.toCharArray()`), mutate
that directly — since `CharArray` *is* mutable — and convert back to a
`String` at the end only if needed (`String(charArray)`).

## The two-pointer technique

One of the most common tools for in-place array/string work: instead of
allocating a second structure, use two index variables and walk them
through the same structure — often from opposite ends toward the middle.

```
reversing [1, 2, 3, 4, 5] in place:

  L               R
[ 1,  2,  3,  4,  5 ]      swap arr[L] and arr[R], then move both inward

      L       R
[ 5,  2,  3,  4,  1 ]      swap again, move inward

          ^
[ 5,  4,  3,  2,  1 ]      L and R have met — loop ends, array reversed
```

This is the same *technique* you'll use here for simple reversal/
comparison problems. It becomes its own full *pattern* — with more variants
(fast/slow pointers, partitioning, converging on a target sum) — in
Phase 7. For now, the goal is just getting comfortable moving two indices
through a structure without an off-by-one error.

Speaking of which — the most common bug in this style of code is getting
the loop's stopping condition wrong: `L < R` vs `L <= R`, or using
`arr.size` where you meant [`arr.lastIndex`](../../../../GLOSSARY.md)
(i.e. `arr.size - 1`). Getting this wrong doesn't usually crash — it
silently processes one element too many or too few.

Concretely, this technique (in both its converging and same-direction
forms) shows up constantly: deduplication, the partition step at the
core of quicksort, merging two already-sorted arrays, and finding a
pair in a sorted array that sums to a target value.

## Check Your Understanding

1. Why is array index access O(1) regardless of the array's size — what
   specifically makes that arithmetic possible?
2. Why is inserting or deleting from the *middle* of an array O(n), while
   doing the same at the *end* is O(1)?
3. What actually happens in memory when you write `s = s + "a"` inside a
   loop in Kotlin, and why does that make the loop O(n²) instead of O(n)?
4. What's the practical workaround for needing to mutate a string
   in place, given that `String` itself is immutable?
5. In a two-pointer reversal walking inward from both ends, what should
   the loop's stopping condition be, and what goes wrong (concretely) if
   you use the wrong comparison operator there?
6. What's the difference between `Array<T>` and `MutableList<T>` in terms
   of resizing — what can and can't happen after creation?
7. Why is appending to a dynamic array (`ArrayList`/`MutableList`)
   described as *amortized* O(1) instead of always O(1)?
8. What could go wrong if you remove elements from a `MutableList` while
   iterating over it with an index-based loop (not an iterator)?
