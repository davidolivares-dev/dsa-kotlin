# Task: Hash Set

Build a hash set from scratch — from
[`HASH_SET_NOTES.md`](HASH_SET_NOTES.md). Values are plain `Int`s.

Build the **buckets directly** rather than wrapping your `HashTable`.
The notes describe both approaches and note that wrapping is what you'd
do in production; the point of doing it the long way once is that the
bucket logic reads differently against bare values than against pairs
whose `.first` you keep reaching through, and it's the version you'd
have to produce if asked to implement one cold.

Each bucket is a `MutableList<Int>` holding every value that hashed to
that index. Create `src/main/kotlin/hashing/HashSet.kt` yourself (copy
the stub below in, then implement each function).

**Naming note:** the class is `HashSet`, which shares a name with
`kotlin.collections.HashSet` from the default imports. A declaration in
the current package beats a default import, so anything in package
`hashing` — your file and the test file both — gets yours with no
import or qualification. Verified, not assumed. The only place it would
matter is code in a *different* package, which would have to import
`hashing.HashSet` explicitly; and if you ever wanted the stdlib one
inside this package, you'd have to spell out
`kotlin.collections.HashSet`.

## Core operations

**`add(value: Int): Boolean`**
Adds `value` if it isn't already present. Returns `true` if the set
changed (the value was new), `false` if it was already there. Adding a
duplicate must not grow `size` or store a second copy.
*Framing: this is the hash table's `put` with the update branch deleted
— there's no value to overwrite. What does the "already present" branch
do instead of overwriting, and what does each branch return?*

**`contains(value: Int): Boolean`**
Returns whether `value` is in the set, in average O(1). Never throws —
absence is an ordinary answer.

**`remove(value: Int): Boolean`**
Removes `value` if present. Returns `true` if the set changed, `false`
if the value wasn't there. Like `add`, this never throws.

**`isEmpty(): Boolean`**
Returns whether the set holds any values, in O(1).

**`size(): Int`**
Returns how many values are in the set, in O(1).

**`toList(): List<Int>`**
Returns every value in the set as a list, in any order. A set has no
meaningful ordering — bucket order is an implementation detail, not a
promise — so the tests sort before comparing rather than assuming one.
*Framing: the values are spread across an array of lists. Same traversal
shape as the hash table's resize.*

## Resizing

Same rules as the hash table: after an `add` that stores a genuinely new
value, resize if the load factor now exceeds `0.75` — double the
capacity and redistribute every existing value into the new bucket
array, recomputing each index against the new capacity.

The same trap applies: redistribute by appending directly to the new
array's chains, not by calling your own `add`, which would recount every
relocated value as new.

## Set operations

Each returns a **new** `HashSet` and leaves both operands unmodified.

**`union(other: HashSet): HashSet`**
Every value in either set.

**`intersection(other: HashSet): HashSet`**
Only values present in both.

**`difference(other: HashSet): HashSet`**
Values in this set but not in `other`. Not symmetric — `a.difference(b)`
and `b.difference(a)` generally differ.

*Framing for all three: each should be O(n + m), which means using the
sets' own O(1) `contains` rather than scanning a list. You already have
every public operation you need — these can be written entirely in terms
of `toList`, `contains`, and `add`, without touching `buckets` directly.
If you find yourself reaching into the bucket array here, step back.*

## Edge cases to handle

- `add` of a value already present — returns `false`, `size` unchanged,
  no duplicate stored.
- `remove` of a value never added — returns `false`, `size` unchanged.
- `contains` on an empty set — `false`, no throw.
- Negative values, and `0` as a stored value. Same `%` sign trap as the
  hash table: a negative value must still land in `0 until capacity`.
- Enough distinct values to cross the load factor threshold — everything
  must survive rehashing, with no duplicates introduced and no values
  stranded.
- Set operations where the two sets are disjoint, identical, or one is
  empty. `union` with an empty set should equal the original;
  `intersection` with an empty set should be empty.
- Set operations must not mutate either operand — build and return a new
  set.

## Stub

```kotlin
package hashing

class HashSet {
    private var capacity = 8
    private var buckets = Array(capacity) { mutableListOf<Int>() }
    private var size = 0

    fun add(value: Int): Boolean {
        TODO()
    }

    fun contains(value: Int): Boolean {
        TODO()
    }

    fun remove(value: Int): Boolean {
        TODO()
    }

    fun isEmpty(): Boolean {
        TODO()
    }

    fun size(): Int {
        TODO()
    }

    fun toList(): List<Int> {
        TODO()
    }

    fun union(other: HashSet): HashSet {
        TODO()
    }

    fun intersection(other: HashSet): HashSet {
        TODO()
    }

    fun difference(other: HashSet): HashSet {
        TODO()
    }
}
```
