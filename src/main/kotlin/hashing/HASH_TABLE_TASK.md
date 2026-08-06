# Task: Hash Table

Build a hash table from scratch — from
[`HASH_TABLE_NOTES.md`](HASH_TABLE_NOTES.md). Keys and values are both
plain `Int`s. Collisions are resolved with **chaining**: each bucket is a
`MutableList<Pair<Int, Int>>` holding every `(key, value)` pair that
hashed to that index — leaning on the standard library for the
per-bucket list itself, since the lesson here is the hash table
mechanics, not reimplementing a linked list.

Create `src/main/kotlin/hashing/HashTable.kt` yourself (copy the stub
below in, then implement each function).

## Operations

**`put(key: Int, value: Int): Unit`**
Inserts `value` under `key`. If `key` is already present, this
**updates** its value in place rather than adding a duplicate entry —
after `put(5, 1)` then `put(5, 2)`, there should be exactly one entry
for key `5`, holding `2`. Only a genuinely new key increments `size`.
This is also where the resize check happens, as the last step — see
[Resizing](#resizing) below.
*Framing: within a bucket's chain, how do you tell "this key is already
here, update it" apart from "this key is new, append it"?*

**`get(key: Int): Int`**
Returns the value stored under `key`. Throws `NoSuchElementException` if
`key` isn't present (same convention used throughout the linked
list/stack/queue tasks).

**`remove(key: Int): Int`**
Removes the entry for `key` and returns the value it held. Throws
`NoSuchElementException` if `key` isn't present.

**`containsKey(key: Int): Boolean`**
Returns whether `key` is currently stored, in average O(1) — this should
reuse the same "compute the bucket, scan its chain" logic as `get`,
without needing a full linear scan of every bucket.

**`isEmpty(): Boolean`**
Returns whether the table currently holds any entries, in O(1).

**`size(): Int`**
Returns how many entries are currently stored (not the bucket array's
`capacity` — those are different numbers once any resizing has
happened), in O(1).

## Resizing

Resizing isn't a public operation a caller invokes — it's internal
housekeeping that `put` triggers on itself. There's no separate entry
point for it in the stub's function list, and nothing else in the API
needs to know it happened.

As its **last** step, after a `put` has added a genuinely new key and
incremented `size`, check whether the load factor (`size / capacity`)
now **exceeds** `0.75`. If it does, double `capacity` and reinsert every
existing entry into the new, larger bucket array — the notes' rehashing
point made concrete: this isn't a raw copy, every entry's bucket index
has to be recomputed against the new `capacity`.

(Strictly `>` rather than `>=`, matching `java.util.HashMap`, which
resizes when size exceeds its threshold rather than on reaching it.
Either would be correct — it only shifts the resize one insertion — but
it's worth being deliberate about which one you meant.)

An update to an already-present key can't trigger this: `size` didn't
change, so the load factor didn't either.

*Framing: what does "reinsert every existing entry" actually require you
to iterate over — the old bucket array, or something else? And once
you've built the new bucket array, how does the table's `buckets`
reference get swapped over to it?*

You're free to add `private` helpers beyond what the stub shows —
pulling the rebuild into a `private fun resize()` is the conventional
shape, as is a `private fun bucketIndex(key: Int): Int` for the index
math that `put`, `get`, `remove`, and `containsKey` all need. Note the
stub declares `capacity` and `buckets` as `var` rather than `val`
precisely because resizing has to reassign both.

## Two Kotlin edge cases worth knowing about

### Integer division in the load factor

`/` on two `Int`s is **integer division** — it truncates rather than
producing a fraction. As a generic example unrelated to this task:

```kotlin
val a = 7 / 2      // 3, not 3.5 — both operands are Int
val b = 7 / 2.0    // 3.5 — one operand is Double, so Double division
```

Since `size` and `capacity` are both `Int`, a load factor computed
naively as `size / capacity` truncates to `0` for anything short of a
completely full table — and `0` never exceeds `0.75`, so the resize
would silently never fire. Nothing crashes and every entry stays
retrievable; the table just quietly degrades toward O(n) as chains grow.
Either force the division to `Double`, or avoid floating point entirely
by rearranging the comparison into integer arithmetic (`0.75` is `3/4`,
and `x / y > 3.0 / 4` is equivalent to `x * 4 > y * 3` for positive `y`).

### Negative keys

Kotlin's `%` operator does **not** behave like mathematical modulo for
negative numbers — it's truncated division, so the result can be
negative. As a generic example unrelated to this task:

```kotlin
val a = -3 % 8   // -3, not 5
```

A bucket index computed as `key % capacity` for a negative `key` would
come out negative too — and a negative array index is a bug (an array
can't be indexed at `-3`). Since `key` here is a plain `Int` and nothing
stops a caller from passing a negative one, your bucket-index calculation
needs to always land in the valid `0 until capacity` range regardless of
the key's sign.

## Edge cases to handle

- `get`/`remove` on a missing key — both should throw.
- `put` on an existing key updates the value without creating a second
  entry or incrementing `size`.
- `remove` actually removes the entry, not just returns its value —
  `containsKey` should be `false` and a later `get` should throw.
- Two keys that collide (hash to the same bucket) must both be
  retrievable independently — removing one shouldn't disturb the other.
- Negative keys must hash to a valid, non-negative bucket index.
- Enough `put` calls to cross the load factor threshold should trigger a
  resize *without losing or misplacing any existing entry* — this is the
  case that actually exercises rehashing, the same way the queue task's
  wraparound case was the one that actually exercised the modulo.

## Stub

```kotlin
package hashing

class HashTable {
    private var capacity = 8
    private var buckets: Array<MutableList<Pair<Int, Int>>> = Array(capacity) { mutableListOf() }
    private var size = 0

    fun put(key: Int, value: Int) {
        TODO()
    }

    fun get(key: Int): Int {
        TODO()
    }

    fun remove(key: Int): Int {
        TODO()
    }

    fun containsKey(key: Int): Boolean {
        TODO()
    }

    fun isEmpty(): Boolean {
        TODO()
    }

    fun size(): Int {
        TODO()
    }
}
```
