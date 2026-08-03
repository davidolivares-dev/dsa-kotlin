# Glossary

Terms used across this repo's `*_NOTES.md` files that aren't self-explanatory.
Alphabetical. Add to this as new topics introduce new vocabulary.

## Amortized complexity

The average cost of an operation over a sequence of operations, even if
occasional individual calls are expensive. Example: appending to a dynamic
array is amortized O(1), even though a resize is O(n), because resizes
happen rarely enough that their cost "spreads out" over many cheap appends.

## Auxiliary space

Extra space an algorithm uses beyond the input itself (e.g. a temporary
array, a recursion call stack). Distinguished from total space complexity,
which includes the input.

## Base case

The condition in a recursive function that stops further recursive calls
and returns a direct answer instead. Every recursive function needs at
least one, or it recurses forever.

## Degenerate case

An input that technically satisfies a structure's definition but breaks
the assumptions an algorithm implicitly relies on. Example: a "balanced"
binary search tree that's been fed sorted input and degenerates into a
straight line (effectively a linked list), losing its O(log n) guarantees.

## In-place

An algorithm that transforms its input using O(1) (or very low) auxiliary
space, rather than allocating a new structure to hold the result.

## Invariant

A property that stays true throughout an algorithm's execution or a data
structure's lifetime, no matter what operations are performed on it.
Example: a min-heap's invariant is "every parent node is less than or
equal to its children" — every insert/delete must preserve this, and it's
what makes reasoning about correctness possible.

## Load factor

For a hash table, the ratio of stored elements to bucket count
(`size / capacity`). Used to decide when to resize — a high load factor
means more collisions and slower operations.

## Memoization

Caching the results of expensive function calls (usually in recursion) so
repeated calls with the same inputs return instantly instead of
recomputing. The "top-down" approach to dynamic programming.

## Mutable / immutable

Whether a value can be changed after creation (mutable) or not
(immutable). Relevant to DSA because immutable structures often require
different (sometimes less efficient, sometimes safer) approaches than
in-place mutation.

## Sentinel node

A dummy node (e.g. a fake head or tail) added to a structure like a
linked list purely to simplify edge-case handling, so insert/delete logic
doesn't need special-case branches for "the list is empty" or "this is
the first/last node."

## Stable sort

A sorting algorithm that preserves the relative order of elements that
compare as equal. Matters when sorting by one key but caring about a
previously-established order among ties.

## Tabulation

Building up a solution iteratively from the smallest subproblems to the
full problem, typically using a table/array. The "bottom-up" approach to
dynamic programming (contrast with memoization).

## Time complexity notation (Big-O / Big-Θ / Big-Ω)

Big-O describes an upper bound on growth rate (worst case, most commonly
used informally). Big-Θ (Theta) describes a tight bound (both upper and
lower — the actual growth rate). Big-Ω (Omega) describes a lower bound
(best case). In casual interview conversation "Big-O" is often used
loosely to mean Big-Θ.
