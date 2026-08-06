# Hash Table

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md) ·
[Arrays & Strings notes](../arrays/ARRAYS_AND_STRINGS_NOTES.md) ·
[Singly Linked List notes](../linkedlist/SINGLY_LINKED_LIST_NOTES.md)

## What it is

A hash table stores **key-value pairs** and gives near-instant lookup by
key — `put(key, value)`, `get(key)`, `remove(key)` are all O(1) on
average, versus O(n) for scanning a plain array or list looking for a
matching key.

The trick: a **hash function** takes a key and deterministically produces
an array index. Storage is just an array of **buckets**; `put`/`get`
compute the key's index and go straight there instead of searching.

```
put(key, value):
    index = hash(key) % capacity
    store (key, value) in buckets[index]

get(key):
    index = hash(key) % capacity
    search buckets[index] for a matching key, return its value
```

The key never gets compared against every stored key — it gets *turned
into* a location. With `capacity = 5`:

```
  key 7   --hash-->   7 % 5 = 2  -->  buckets[2]
  key 12  --hash-->  12 % 5 = 2  -->  buckets[2]   <-- same slot as key 7: collision
  key 9   --hash-->   9 % 5 = 4  -->  buckets[4]
```

That "search `buckets[index]`" step matters: two different keys can hash
to the *same* index. That's a **collision**, and it's not a bug or a sign
of a bad hash function — with a fixed number of buckets and an unbounded
number of possible keys, collisions are mathematically guaranteed to
happen eventually (more keys than slots). A hash table's whole design is
about making collisions rare and cheap to resolve when they do happen,
not about pretending they can't occur.

## Collision handling: chaining

This repo's implementation resolves collisions with **separate
chaining**: each bucket holds a small list of *all* the key-value pairs
that hashed to that index, not just one. `get` computes the index, then
scans that bucket's short list for the matching key.

Taking the three keys from above, `buckets` is an array of slots, each
holding a chain that grows sideways:

```
capacity 5

  index
    0   [ ]
    1   [ ]
    2   [ ] --> (7,70) --> (12,120)
    3   [ ]
    4   [ ] --> (9,90)
```

Keys `7` and `12` collided at index `2`, so both pairs live in that
bucket's chain. `get(12)` computes index `2`, then walks the chain past
`7` before finding `12`. Note what's stored is the **whole pair**, not
just the value — without the key alongside it, there'd be no way to tell
which of the two entries in bucket 2 you'd landed on.

That chain length is the thing to keep an eye on. Every bucket in this
picture holds zero or one entries except bucket 2, which holds two — so
a lookup costs one or two comparisons, not five. Keeping that number
small is what the next section is about.

The other well-known strategy is **open addressing** (e.g. linear
probing: on a collision, try the next slot over instead of chaining) —
worth knowing it exists, but not what's implemented here. Chaining is
simpler to reason about and doesn't need the "mark a slot as deleted
instead of actually clearing it" bookkeeping open addressing requires
when removing entries.

## Load factor & resizing

[Load factor](../../../../GLOSSARY.md#load-factor) is `size / capacity` —
how full the bucket array is. As it climbs, chains get longer, and `get`
degrades from "check one short chain" toward "scan a long list" — the
O(1) average starts to erode.

The fix is the same shape as a dynamic array's growth strategy: once load
factor crosses a threshold (commonly `0.75`), **resize** — allocate a
bigger bucket array (commonly double the capacity) and reinsert every
existing entry into it.

That reinsertion step is the one place a hash table's resize is *more*
expensive than a dynamic array's: a dynamic array resize is just a raw
copy of existing elements into the new array. A hash table resize has to
**rehash** every entry, because `index = hash(key) % capacity` depends on
`capacity` — every single index is potentially different in the bigger
array, not just relocated.

Concretely, here's a table at `capacity = 4` holding four entries. Load
factor is `4 / 4 = 1.0`, well past `0.75`, so a resize triggers:

```
BEFORE — capacity 4, size 4

  index
    0   [ ]
    1   [ ] --> (5,50)
    2   [ ] --> (10,100)
    3   [ ] --> (3,30) --> (7,70)
```

```
AFTER — capacity 8, size 4 (unchanged)

  index
    0   [ ]
    1   [ ]
    2   [ ] --> (10,100)
    3   [ ] --> (3,30)
    4   [ ]
    5   [ ] --> (5,50)
    6   [ ]
    7   [ ] --> (7,70)
```

Follow each key across the two pictures:

| key | old index (`% 4`) | new index (`% 8`) |     |
| --- | ----------------- | ----------------- | --- |
| 3   | 3                 | 3                 | stayed |
| 10  | 2                 | 2                 | stayed |
| 5   | 1                 | 5                 | **moved** |
| 7   | 3                 | 7                 | **moved** |

Two entries kept their index and two didn't — and *which* is which isn't
predictable without doing the arithmetic. That's precisely why this can't
be a bulk copy: `5` and `7` would end up in the wrong buckets and become
unreachable, since `get` would compute their new indices and look in
slots the copy never touched.

Notice also what resizing bought: keys `3` and `7` shared bucket 3
before, and now sit alone in separate buckets. The chain got shorter,
which is the entire point — load factor dropped from `1.0` to `0.5`, and
lookups for those keys went from "walk a two-item chain" back to "check
one slot." `size` never changed; only the distribution did.

Despite that heavier per-resize cost, the
same [amortized complexity](../../../../GLOSSARY.md#amortized-complexity)
argument still applies: resizes happen rarely enough (halving in
frequency each time capacity doubles) that their cost still averages out
to O(1) per `put` over a long sequence of calls.

## Complexity

- `put`, `get`, `remove`: **average** O(1), assuming a reasonable hash
  function keeps chains short and resizing keeps load factor bounded.
- **Worst case** O(n): if every key happens to hash to the *same* bucket,
  the chain there degenerates into a plain list, and every operation has
  to walk the whole thing. This is a
  [degenerate case](../../../../GLOSSARY.md#degenerate-case) in the same
  spirit as a BST fed sorted input — the structure's happy-path guarantee
  depends on an assumption (here, "the hash function spreads keys out
  reasonably evenly") that a pathological input can violate.

## The invariant

A hash function must be **deterministic**: the same key always produces
the same index, on every call, for as long as `capacity` doesn't change.
Break that, and `get` might compute a different bucket than `put` used to
store the value in the first place — the entry becomes unreachable even
though it's still sitting in the array. This is also exactly why
resizing has to rehash rather than just copy: every entry's index is a
function of `capacity`, so changing `capacity` without recomputing
indices would violate this same invariant for every entry at once.

## Space cost

O(n) [auxiliary space](../../../../GLOSSARY.md#auxiliary-space) for `n`
stored entries, plus the bucket array itself sized to `capacity` — some
of that capacity sits unused whenever load factor is below 1, which is
the deliberate trade of a bit of extra memory for keeping chains short.

## When to reach for it

Anywhere "look this up by key, fast" is the actual problem: dictionaries
and maps, caching, deduplicating a collection, counting frequencies. The
general tell is any problem you'd otherwise solve by scanning a list
looking for a match — a hash table turns that O(n) scan into an O(1)
average lookup.

Looking ahead: hash tables (and the Hash Set built on the same idea,
next in this phase) become a default supporting tool in later phases —
graph traversal needs a "visited" set (Phase 3/5), and a large share of
Phase 7's array/string pattern problems (e.g. "have I seen this value
before?") lean on O(1) lookup to avoid an O(n²) nested scan.

## Check Your Understanding

1. What advantage does a hash table have over a plain array or list for
   looking something up by key? Be specific about the Big-O contrast.
2. What is a hash function, and what property must it have for a hash
   table to work correctly at all?
3. What is a collision, and why is *some* collision-handling strategy
   always necessary, no matter how good the hash function is?
4. Explain chaining: when two keys collide, what actually happens to
   where and how they're stored?
5. What is load factor, and why does crossing a threshold trigger a
   resize?
6. Why is a hash table's resize more expensive, entry-for-entry, than a
   dynamic array's resize? Despite that, why is it still fair to call
   `put` amortized O(1)?
7. What has to be true about the keys or the hash function for `get` to
   degrade to its O(n) worst case?
8. Give a concrete scenario where a hash table is exactly the right
   tool, and describe what you'd have to do instead — and how much
   slower it'd be — without one.
