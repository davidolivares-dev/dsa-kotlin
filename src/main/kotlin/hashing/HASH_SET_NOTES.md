# Hash Set

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md) ·
[Hash Table notes](HASH_TABLE_NOTES.md) ·
[Arrays & Strings notes](../arrays/ARRAYS_AND_STRINGS_NOTES.md)

## What it is

A hash set stores a collection of **distinct values** and answers one
question fast: *is this value in here?* `add`, `contains`, and `remove`
are all average O(1).

The machinery is the hash table's, unchanged — an array of buckets, a
hash function turning a value into a bucket index, chaining for
collisions, resizing when the load factor climbs. The difference is what
each bucket holds:

```
hash table                        hash set
bucket -> (key, value) pairs      bucket -> values

  index                             index
    0   [ ]                           0   [ ]
    1   [ ] --> (5,50)                1   [ ] --> 5
    2   [ ] --> (10,100)              2   [ ] --> 10
    3   [ ] --> (3,30) --> (7,70)     3   [ ] --> 3 --> 7
```

Everything else carries over. Read
[`HASH_TABLE_NOTES.md`](HASH_TABLE_NOTES.md) for hash functions,
collisions, load factor, and rehashing — none of it changes here. These
notes cover only what's genuinely different.

## Is a set just a map with the values thrown away?

Essentially, yes — and that's not a shortcut, it's how the standard
library does it. `java.util.HashSet` holds a private `HashMap` internally
and stores every element as a key mapped to one shared dummy object.
Kotlin's `hashSetOf` produces exactly that.

So there are two honest ways to build one:

1. **Wrap a hash table.** `add(v)` becomes `put(v, ignored)`,
   `contains(v)` becomes `containsKey(v)`. Almost no new code.
2. **Build the buckets directly**, storing bare values instead of pairs.

Option 1 is what you'd reach for in production — reusing a tested
structure beats duplicating it. Option 2 is worth doing once, because
writing the bucket logic against plain values (rather than pairs whose
`.first` you keep reaching through) makes the underlying mechanism
clearer, and it's how you'd have to think about it if asked to implement
one from scratch in an interview.

## What's actually different

**No values, so no update case.** The hash table's `put` had two
branches: key present (overwrite the value) or key absent (append,
increment `size`). A set has nothing to overwrite. Adding a value that's
already present is a **no-op** — the set is unchanged.

**`add` reports whether anything changed.** Since adding a duplicate does
nothing, the useful return is a `Boolean`: `true` if the value was new,
`false` if it was already there. That single bit is what makes
deduplication a one-liner and lets a caller detect "have I seen this
before?" without a separate `contains` call. `remove` mirrors it — `true`
if something was removed, `false` if the value wasn't present.

This is a genuine API design difference, not a detail. Compare:

```
hash table:  put(k, v) -> Unit      get(k) -> V (or throws)
hash set:    add(v)    -> Boolean   contains(v) -> Boolean
```

A set never throws for a missing value, because "not present" is a
perfectly ordinary answer to every question a set can be asked. The hash
table's `get` had to throw because there was no `V` it could honestly
return.

**Set operations.** Sets support combinations that maps don't naturally
have:

```
A = {1, 2, 3, 4}      B = {3, 4, 5}

union(A, B)         = {1, 2, 3, 4, 5}     everything in either
intersection(A, B)  = {3, 4}              only what's in both
difference(A, B)    = {1, 2}              in A but not in B
```

Each is O(n + m) if you use the sets themselves for lookup — walk one
set's elements and ask the other for O(1) membership. Done naively with
lists instead, each becomes O(n × m), since every element would need a
linear scan of the other collection. That gap is the whole reason to
reach for a set here.

Note **`difference` is not symmetric**: `difference(A, B)` is `{1, 2}`
but `difference(B, A)` is `{5}`. Union and intersection are.

## Complexity

Same as the hash table, for the same reasons:

- `add`, `contains`, `remove`: average O(1), worst case O(n) when
  everything collides into one bucket.
- `union`, `intersection`, `difference`: O(n + m) average.

## The invariant

**No duplicates, ever.** Every value appears at most once. `add` is
responsible for enforcing it — which means it must scan the target
bucket's chain before appending, exactly as the hash table's `put`
scanned before deciding to overwrite or insert.

The hash table's determinism requirement carries over unchanged: the same
value must always hash to the same bucket, or `contains` will search
somewhere `add` never wrote.

## Space cost

O(n) for `n` values, plus the bucket array sized to `capacity`. Slightly
cheaper per element than the equivalent hash table, since each slot holds
a bare value rather than a pair — though on the JVM that saving is
smaller than it looks, since the elements are still boxed objects inside
a list.

## When to reach for it

Whenever the question is **membership** rather than lookup-by-key:

- **Deduplication** — add everything, keep whatever returns `true`.
- **"Have I seen this before?"** — the classic use, and the reason a
  seen-set turns many O(n²) nested-scan problems into O(n). Phase 7's
  two-pointer and sliding-window problems lean on this constantly.
- **Visited tracking in graph traversal** (Phase 5). BFS and DFS both
  need to know whether a node has already been explored; without O(1)
  membership they'd rescan a list every step, and cycles would loop
  forever.

The tell is the same one as for the hash table: if you'd otherwise write
"scan this collection looking for a match," a set collapses that O(n)
scan into O(1).

Reach for a hash **table** instead when you need to associate something
*with* each key. A set only remembers that a value is present, not
anything about it.

## Check Your Understanding

1. What does a hash set store in each bucket, compared to what a hash
   table stores? Which parts of the machinery are identical?
2. Why does `add` return a `Boolean` when the hash table's `put` returned
   nothing useful? What can a caller do with that bit?
3. What happens when you `add` a value that's already present, and why is
   that different from `put`-ing an existing key into a hash table?
4. A hash table's `get` throws for a missing key, but a hash set's
   `contains` just returns `false`. Why is throwing the right call in one
   case and the wrong call in the other?
5. Why is `intersection` O(n + m) using sets, but O(n × m) if you did the
   same thing with two lists? What specifically causes the difference?
6. Which of union, intersection, and difference are symmetric, and which
   isn't? Give an example showing the asymmetry.
7. `java.util.HashSet` is implemented as a `HashMap` internally. What is
   it storing as the map's values, and why is that not wasteful in the
   way it first appears?
8. What invariant does a hash set have to maintain that a plain list
   doesn't, and which operation is responsible for enforcing it?
9. Describe a problem where using a set turns an O(n²) solution into
   O(n). What is the set actually keeping track of?
