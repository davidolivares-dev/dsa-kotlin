# Doubly Linked List

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md) ·
[Singly Linked List notes](SINGLY_LINKED_LIST_NOTES.md)

## What it is

Same chain-of-nodes idea as a singly linked list, but each node holds
*two* references instead of one: `next` (the node after it) and `prev`
(the node before it). That second reference is what changes everything
about this structure's complexity profile — it's now cheap to walk
backward, and cheap to reach "the node before this one" without having
walked the whole list to find it.

Because tracking a `tail` reference is now actually useful (see below),
doubly linked lists typically track both `head` and `tail`:

```
     head                      tail
       |                         |
       v                         v
[null| 1 |*]<-->[*| 2 |*]<-->[*| 3 |null]
```

An empty list is `head == null` and `tail == null`. A single-node list
has `head` and `tail` both pointing at that same one node, whose `prev`
and `next` are both `null`.

## Why the extra pointer changes the complexity picture

Index-based access is still O(n) — there's still no contiguous memory, so
there's no arithmetic shortcut to "jump to index `i`." (One constant-factor
trick: since you have both ends, you can walk from whichever of `head` or
`tail` is closer to the target index. Still O(n) in the worst case, just a
smaller constant.)

Insertion/deletion at the **head** is O(1), same as a singly linked list.
The real change is at the **tail**: because you track a `tail` reference
*and* every node has a `prev` pointer, insertion/deletion at the tail is
now O(1) too — no walking required to find "the node before the last
one," since `tail.prev` gives it to you directly. Compare this to the
singly linked list notes, where tail deletion stayed O(n) even with a
tracked tail reference, precisely because there was no way back.

That same trick generalizes: **deleting a node you already hold a direct
reference to** (as opposed to searching for it by value) is O(1) here,
full stop — `node.prev` and `node.next` are both already in hand, so
relinking around it needs no traversal at all. For a singly linked list,
the equivalent deletion is O(n), because finding "the node before this
one" requires walking from `head`. This one difference is the whole
reason doubly linked lists show up as the backbone of structures like
LRU caches (see "When to reach for it" below).

```
inserting X at the tail:

old tail                new node
[*| 3 |*]  <-------->  [*| X |null]
                  ^ tail reassigned to the new node — O(1), no shifting
```

## The invariant

Same connectivity requirement as a singly linked list, but doubled: for
every adjacent pair of nodes, **both** directions have to agree.
`node.next.prev` must be `node`, and `node.prev.next` must be `node`. Every
insert/delete has to keep both links consistent, not just one.

The new failure mode this creates: updating `next` correctly but
forgetting the matching `prev` update (or vice versa). Unlike a fully
broken singly-linked chain, this can produce a list that looks *fine*
walking forward from `head` but is corrupted walking backward from
`tail` — or the reverse. It's a quieter bug than the singly linked list's
"lost the rest of the chain" failure, which is exactly what makes it
easy to miss.

```
deleting node 2 from  1 <-> 2 <-> 3

forgetting the prev update:
  1.next = 3        (forward chain now looks right: 1 -> 3)
  3.prev = 2         <- still points at the removed node!

  walking forward from head: 1, 3        looks correct
  walking backward from tail: 3, 2, ...  wrong — 2 was supposed to be gone
```

## Space cost

Still O(n) [auxiliary space](../../../../GLOSSARY.md#auxiliary-space), but
with even more overhead per element than a singly linked list — every
node now stores two references instead of one. A doubly linked list of
`n` ints is `n` ints *plus* `2n` references.

## When to reach for it

Reach for a doubly linked list specifically when you need O(1) operations
at *both* ends, or O(1) removal of a node you already hold a reference
to (not one you have to search for). If you only ever add/remove from the
front, the extra `prev` pointer's memory cost buys you nothing — a singly
linked list is strictly cheaper per element for that case.

Concretely: this is the classic backing structure for an **LRU cache**
(Phase 2 — paired with a hash map that maps key → node, so a lookup gives
you a direct node reference, and eviction/move-to-front-on-access are
both O(1) thanks to the doubly linked property above). It also shows up
in deque implementations needing O(1) push/pop at both ends, and anywhere
you need to walk a sequence in either direction (browser back/forward
history, undo/redo where you may need to re-apply in either order).

One implementation detail worth knowing about, even if you don't need it
yet: many real doubly-linked-list implementations use
[sentinel nodes](../../../../GLOSSARY.md#sentinel-node) — permanent dummy
nodes at both ends — specifically to eliminate the `null` special-casing
that empty-list and single-node operations otherwise require.

## Check Your Understanding

1. Why does adding a `prev` pointer change tail insertion/deletion from
   O(n) to O(1), when a singly linked list with a tracked tail reference
   still can't do that?
2. What's the complexity of deleting a node you already hold a direct
   reference to (not searching for it by value)? Why is this the main
   reason doubly linked lists back structures like LRU caches?
3. What's different about the invariant here compared to a singly linked
   list — what *two* things have to stay consistent for every adjacent
   pair of nodes, instead of just one?
4. If you update a node's `next` pointer correctly during a deletion but
   forget to update the corresponding `prev` pointer, what does the list
   look like walking forward from `head`? What does it look like walking
   backward from `tail`? Is this bug louder or quieter than the singly
   linked list's "lost the rest of the chain" failure?
5. Does a doubly linked list make index-based access any faster,
   Big-O-wise? What's the one constant-factor trick available here that
   isn't available on a singly linked list?
6. Why does a doubly linked list use more memory per element than a
   singly linked list holding the same values, even though both are O(n)
   auxiliary space?
7. What do `head` and `tail` look like for an empty list? What should
   `prev` and `next` be for the one node in a single-node list?
8. Give a concrete scenario where you'd choose a doubly linked list over
   a singly linked list, and name specifically which operation's
   complexity is the deciding factor.
