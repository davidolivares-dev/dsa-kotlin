# Singly Linked List

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md) ·
[Arrays & Strings notes](../arrays/ARRAYS_AND_STRINGS_NOTES.md)

## What it is

A linked list stores elements as a chain of separately-allocated **nodes**,
each holding a value and a reference to the next node. This is the
opposite storage strategy from an array: no contiguous block of memory,
no index arithmetic — just a **head** reference to the first node, and
each node pointing to the next, until the last node points to `null`.

```
head
 |
 v
[ 1 | *]--->[ 2 | *]--->[ 3 | null]
```

An empty list is simply `head == null` — no nodes at all.

## Why the complexity is the mirror image of an array's

Because there's no contiguous memory and no index arithmetic, **access by
index is O(n)** — the only way to reach the 5th element is to walk the
chain from `head`, one `next` at a time. Compare that to the array notes:
this is exactly the property arrays have for free, and linked lists don't.

But the trade goes the other way for insertion at the front:

```
inserting 0 at the head:

new node        old head
[ 0 | *]  ---->  [ 1 | *]--->[ 2 | *]--->[ 3 | null]

then head is reassigned to point at the new node — O(1), no shifting
```

**Insertion/deletion at the head is O(1)** — just relink a couple of
references, nothing has to move. This is the mirror image of the array,
where inserting at the *front* is the expensive O(n) case (everything
shifts) and appending at the *end* is the cheap one.

For a **singly** linked list specifically (no reference back to the
previous node), insertion/deletion at the **tail** is O(n) *unless* you
separately track a tail reference — and even then, deletion of the tail
is still O(n), because removing the last node requires updating the
*second-to-last* node's `next` to `null`, and there's no way to reach "the
node before this one" without walking from `head`. Insertion at a known
position in the middle is O(n) to reach that position, but O(1) once
you're there — same relink, no shifting.

## The invariant

The chain must stay connected and acyclic: starting from `head`, following
`next` repeatedly must eventually reach a node whose `next` is `null`,
visiting every node in the list exactly once. Every insert/delete has to
preserve this. The classic way to break it: reassigning a pointer in the
wrong order, before you've captured a reference to the part of the chain
you still need.

```
deleting node 2 from  1 -> 2 -> 3 -> null

WRONG order: set node1.next = null first
  1 -> null    2 -> 3 -> null      <- lost access to 2 and 3 entirely

RIGHT order: set node1.next = node3 first (using a reference you
already had to node3 via node2.next), then node2 can be discarded
  1 -> 3 -> null
```

This is the linked-list equivalent of the off-by-one bugs from arrays —
it doesn't crash, it silently orphans part of your structure (a memory
leak in languages without garbage collection; in Kotlin/JVM it just means
those nodes become unreachable and get garbage collected, but your list
is now wrong).

## Space cost

Both an array and a linked list holding `n` elements are O(n)
[auxiliary space](../../../../GLOSSARY.md#auxiliary-space) — but a linked
list uses more memory *per element*, since every node needs to store a
reference/pointer in addition to the value itself. An array of `n` ints
is `n` ints; a linked list of `n` ints is `n` ints *plus* `n` references.

## When to reach for it

Favor a linked list when you're doing frequent insertions/deletions at
the front (or at a position you already have a reference to) and don't
need random access by index. Favor an array (or `MutableList`) when you
need O(1) index access, or when you're mostly appending to the end, where
a dynamic array's amortized O(1) append is just as good with less memory
overhead per element.

Concretely: linked lists show up as the backing structure for stacks and
queues (Phase 1 — always adding/removing from one end), an LRU cache's
ordering structure (paired with a hash map for O(1) lookup — Phase 2),
undo/redo history, or an OS's free-memory block list. Arrays
win whenever something needs lookup-by-index (a leaderboard by rank,
pixel data, a matrix) or backs an algorithm that assumes O(1) random
access — binary search doesn't work efficiently on a linked list, since
"jump to the middle" requires index access.

One wrinkle worth knowing for interviews: even when Big-O says two
operations are "the same," arrays often win in practice due to **cache
locality** — contiguous memory lets the CPU prefetch and cache
sequentially, while linked list nodes can be scattered anywhere in
memory, causing a cache miss on every `next` traversal. This isn't
reflected in complexity analysis at all, but it's a common follow-up
question ("if both are O(n), why is the array faster in practice?").

## Check Your Understanding

1. Why is index-based access O(n) for a linked list but O(1) for an
   array? What's fundamentally different about how each is laid out in
   memory?
2. Why is insertion at the *head* of a linked list O(1), while insertion
   at the *front* of an array is O(n)?
3. Even with a tracked tail reference, why is *deleting* the tail node
   still O(n) for a singly linked list?
4. What's the core invariant that must be preserved during any insert or
   delete, and what specifically goes wrong if you reassign pointers in
   the wrong order?
5. If you accidentally set a node's `next` to `null` before capturing a
   reference to the rest of the chain you still needed, what happens to
   the rest of the list? Is this the kind of bug that crashes, or the
   kind that fails silently?
6. Why does a linked list use more total memory than an array holding the
   same `n` values, even though both are O(n) space?
7. What does an empty linked list look like (what is `head`), and why do
   insert/delete operations typically need a special case for it?
8. Give one scenario where a linked list is clearly the better choice
   over a dynamic array, and one where the array is clearly better. What
   specifically makes the difference in each case?
