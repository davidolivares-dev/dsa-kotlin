# Binary Search Tree

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md) ·
[Binary Tree notes](BINARY_TREE_NOTES.md) ·
[Hash Table notes](../hashing/HASH_TABLE_NOTES.md)

## What it is

A binary search tree is a binary tree with **one ordering rule** added:

> For every node, every value in its **left** subtree is smaller, and
> every value in its **right** subtree is larger.

That single rule is the whole topic. Everything a BST can do that a
plain binary tree can't follows from it.

```
      50
    /   \
  30      70
 / \     / \
20  40  60  80
```

Read the rule at the root: everything under `30` is less than `50`,
everything under `70` is greater. Then check it again at `30` — `20` is
less, `40` is greater. It has to hold at **every** node, not just the
root. That distinction matters more than it looks; see the invariant
section.

## What the ordering buys: O(log n) search

In a plain binary tree, `contains` was O(n) — with no rule about where
values live, you might have to look everywhere. Here, comparing against
a node tells you which way to go, and **the other subtree is eliminated
entirely**:

```
searching for 40 in the tree above:

  at 50: 40 < 50, go left      -> everything under 70 discarded
  at 30: 40 > 30, go right     -> everything under 20 discarded
  at 40: found
```

Three comparisons instead of seven. Each step throws away roughly half
of what's left, which is what makes it **O(h)** — proportional to the
tree's height, not its node count. In a balanced tree `h ≈ log₂ n`, so
searching a million nodes takes about 20 comparisons.

This is the same "halve the search space" idea as binary search on a
sorted array (Phase 6). A BST is essentially binary search made into a
structure that can also be modified cheaply.

## In-order traversal emits sorted values

Run in-order on the tree above and you get:

```
20, 30, 40, 50, 60, 70, 80
```

Sorted — and not by coincidence. In-order visits *left subtree, node,
right subtree*, and the ordering rule says exactly that everything left
is smaller and everything right is larger. The traversal and the
invariant are the same statement.

Two consequences worth remembering:

- **In-order traversal is how you validate a BST.** If the output isn't
  strictly increasing, the ordering rule is broken somewhere.
- A BST gives you sorted iteration for free, which a hash table cannot.
  That's the main reason to choose one over a hash table — see below.

## Operations

**Search** — compare, go left or right, repeat. O(h).

**Insert** — search for where the value *would* be; when you fall off
the bottom, that empty spot is where it belongs. O(h). The tree grows
itself here, unlike the plain binary tree where you handed it a shape.

**Delete** — three cases, and the third is the one that takes thought.

### Delete case 1: a leaf

Nothing depends on it. Detach and done. Deleting `20`:

```
before:                    after:
      50                       50
    /   \                    /   \
  30      70               30      70
 / \     / \                \     / \
20  40  60  80               40  60  80
```

### Delete case 2: one child

The node has a single subtree hanging off it. That subtree is already
correctly ordered relative to everything above, so it just moves up to
take the node's place. Deleting `30` (which has only `40` left):

```
before:                    after:
    50                       50
  /   \                     /  \
30      70                40    70
 \     / \                     / \
  40  60  80                  60  80
```

### Delete case 3: two children

Here you can't just splice — the node has two subtrees and only one
slot to put them in. So instead of removing the node, you **replace its
value** with one that's allowed to sit there, then delete *that* value
from below.

Which value can legally take the spot? Exactly two candidates:

- the **in-order successor** — the smallest value in the right subtree
- the **in-order predecessor** — the largest value in the left subtree

Both work, because both are the immediate neighbours of the deleted
value in sorted order, so either one keeps everything left of it smaller
and everything right of it larger. Convention is the successor.

Finding the successor is simple: go **right once, then left as far as
you can**. Deleting the root `50`, whose successor is `60`:

```
before:                    after:
      50                       60
    /   \                    /  \
  30      70               30    70
 / \     / \              / \     \
20  40  60  80           20  40    80
```

`60` moved up into the root, and the original `60` was removed from the
right subtree. In-order is still sorted: `20, 30, 40, 60, 70, 80`.

**Why the successor is always easy to remove:** it's the *leftmost* node
of the right subtree, so by definition it has no left child. That means
the recursive delete of it always lands in case 1 or case 2 — never
case 3 again. The recursion can't spiral.

## Complexity

| operation | balanced | degenerate |
| --- | --- | --- |
| search | O(log n) | O(n) |
| insert | O(log n) | O(n) |
| delete | O(log n) | O(n) |

Every operation is **O(h)**, the tree's height. The table just shows
what `h` becomes for the two extreme shapes — so the honest statement is
"O(h), and h is O(log n) *if the tree is balanced*."

That caveat is not academic. Insert sorted data and every value goes to
the same side:

```
inserting 20, 30, 40, 50, 60, 70, 80 in order:

20
 \
  30
   \
    40
     \
      50
       \
        60
         \
          70
           \
            80
```

Height 6 instead of 2, for the same seven values — a
[degenerate](../../../../GLOSSARY.md#degenerate-case) tree, which is a
linked list with extra pointers. Every operation is now O(n), and the
BST has bought nothing.

Sorted input isn't an exotic edge case, either — it's what you get from
a database dump, a sequential ID column, or timestamps. **Self-balancing
trees** (AVL, red-black) exist precisely for this: they rotate on insert
to keep the height logarithmic no matter the input order. They're in
this repo's stretch tier, but knowing *why* they exist is the part that
matters.

## The invariant

**Every node's entire left subtree is smaller, and its entire right
subtree is larger.** The trap is checking only immediate children:

```
      50
    /   \
  30      70
 / \
20  60          <- 60 > 50, but it sits in 50's LEFT subtree
```

Every parent-child pair here looks fine — `60` is correctly greater than
its parent `30`. But the tree is invalid, because `60` is in the left
subtree of `50` while being larger than `50`. A search for `60` would go
right at the root and never find it.

So validation isn't "is each child on the correct side of its parent" —
it's "is each node within the **range** allowed by every ancestor above
it." Equivalently, and more simply: does in-order traversal come out
strictly increasing?

**Duplicates.** The rule as stated has no place for equal values —
they're neither smaller nor larger. Implementations pick a policy: reject
them, always send them one direction, or store a count per node. This
repo's task rejects them (insert becomes a no-op), which keeps the rule
crisp.

## Space cost

O(n) for `n` nodes, two references each — same as a plain binary tree.
Recursive operations add O(h) call-stack frames, which is the same
degenerate-tree hazard from the binary tree notes: a sorted-input BST of
a million nodes will overflow the stack before it finishes.

## When to reach for it

Against a plain binary tree, the BST wins whenever you're searching by
value — that's the whole point of the ordering.

The more interesting comparison is against a **hash table**, which beats
a BST on raw lookup: O(1) average versus O(log n). Reach for the BST
when you need something hashing throws away — **order**:

- **Sorted iteration** — in-order traversal, free. A hash table has no
  meaningful order at all.
- **Range queries** — "every value between 30 and 60." Straightforward
  in a BST, impossible in a hash table without scanning everything.
- **Nearest-match queries** — smallest value ≥ x, largest ≤ x.
- **Min and max** — leftmost and rightmost nodes.

Real-world: database indexes are tree-based (B-trees, a generalisation
of this idea) precisely because `WHERE created_at > ...` is a range
query. Kotlin's `sortedMapOf` and Java's `TreeMap` are red-black trees;
`hashMapOf` and `HashMap` are hash tables. The choice between them is
exactly this trade.

## Check Your Understanding

1. State the BST ordering rule precisely. Why is "left child smaller,
   right child larger" not a sufficient statement of it?
2. Why does the ordering rule make search O(h) instead of O(n)? What
   specifically happens to the half you don't descend into?
3. Why does in-order traversal produce sorted output on a BST? Connect
   it to the ordering rule rather than just asserting it.
4. Walk through deleting a node with two children. Why can't you just
   remove it the way you would a leaf?
5. What is the in-order successor of a node, and how do you find it
   given that node? Why is it guaranteed to have at most one child?
6. The predecessor would work equally well in delete case 3. Why is
   either one a valid replacement — what property do they share?
7. What input order turns a BST into a degenerate tree, and why is that
   input realistic rather than contrived?
8. Every BST operation is O(h). Under what condition is that O(log n),
   and what is it otherwise?
9. A hash table has O(1) average lookup versus a BST's O(log n). Give
   two concrete situations where you'd still choose the BST, and say
   what the hash table cannot do.
10. How would you check whether an arbitrary binary tree is a valid BST?
    Give two different approaches.
