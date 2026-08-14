# Task: Binary Search Tree

Build a binary search tree from scratch — from
[`BINARY_SEARCH_TREE_NOTES.md`](BINARY_SEARCH_TREE_NOTES.md). Values are
plain `Int`s. **Duplicates are rejected**: inserting a value already
present is a no-op, which keeps the ordering rule crisp (an equal value
is neither smaller nor larger, so it has no home).

Create `src/main/kotlin/trees/BinarySearchTree.kt` yourself (copy the
stub below in, then implement each function).

## What changes from the plain binary tree

Three structural differences worth noticing before you start, all
consequences of the tree now owning its own shape:

- **`Node` is private again.** The ordering rule decides where values
  go, so callers hand over values, not shapes. `BinaryTree` exposed
  `Node` only because the shape *was* the input.
- **`left` and `right` are `var`, and so is `root`.** Insert and delete
  rewire pointers; `BinaryTree` was built once and only read.
- **`size` is a tracked field, not a traversal.** `BinaryTree.size()`
  counted nodes on demand in O(n). Here you already touch the exact
  moment a node is created or removed, so maintaining a counter makes
  `size()` O(1).

## The tree used in the examples

Inserting `50, 30, 70, 20, 40, 60, 80` in that order gives:

```
      50
    /   \
  30      70
 / \     / \
20  40  60  80
```

## Operations

**`insert(value: Int)`**
Places `value` in its ordered position. A value already present is a
no-op — no duplicate node, no change to `size`. O(h).

*Framing: the natural recursion here returns the subtree it was given,
possibly with a new node grafted in — `private fun insertInto(node:
Node?, value: Int): Node`, called as `root = insertInto(root, value)`.
That "reassign as you unwind" shape is worth getting comfortable with,
because delete needs the same one. Ask yourself what the base case
returns when it falls off the bottom of the tree, and what each
non-base case returns after recursing.*

*Second framing: `size` should only grow when a node is genuinely
created. Where in the recursion does that happen — and is it a place you
can increment directly?*

**`contains(value: Int): Boolean`**
Whether `value` is in the tree, in O(h) — comparing at each node and
descending one side only. Do **not** search both subtrees; discarding
half the tree at every step is the entire point of the ordering rule.

**`delete(value: Int)`**
Removes `value` if present; a value that isn't there is a no-op. O(h).
Three cases, per the notes:

1. **Leaf** — detaches.
2. **One child** — that subtree moves up.
3. **Two children** — copy the in-order successor's value into this
   node, then delete the successor from the right subtree.

*Framing: same return-the-subtree shape as insert. Cases 1 and 2
collapse into each other more neatly than they first look — if you
return "whichever child exists, or null," what happens for a leaf?*

*Second framing: the successor is the leftmost node of the right
subtree. A small private helper that walks left until it can't is worth
extracting, since `min()` needs the same walk.*

**`min(): Int` / `max(): Int`**
Smallest and largest values. Both throw `NoSuchElementException` on an
empty tree, matching the convention used throughout this repo.

*Framing: no comparisons needed — the ordering rule already tells you
which direction to walk, and where to stop.*

**`inOrder(): List<Int>`**
Every value in ascending order. Empty tree returns an empty list.

*This one doubles as your correctness check: if the output isn't
strictly increasing, the ordering rule has been broken somewhere — most
likely by delete.*

**`height(): Int`**
Longest root-to-leaf path in edges. Empty tree is `-1`, single node is
`0`. Same as `BinaryTree`.

**`size(): Int`**
Node count, in **O(1)** — read the tracked field rather than walking.

**`isEmpty(): Boolean`**
Whether the tree holds any values, O(1).

## Expected values for the example tree

| call | result |
| --- | --- |
| `inOrder()` | `[20, 30, 40, 50, 60, 70, 80]` |
| `size()` | `7` |
| `height()` | `2` |
| `min()` / `max()` | `20` / `80` |
| `contains(40)` / `contains(45)` | `true` / `false` |

After each delete, starting fresh from the example tree:

| delete | which case | `inOrder()` afterwards |
| --- | --- | --- |
| `20` | leaf | `[30, 40, 50, 60, 70, 80]` |
| `30` | two children | `[20, 40, 50, 60, 70, 80]` |
| `50` | two children, at the root | `[20, 30, 40, 60, 70, 80]` |

## Edge cases to handle

- **Empty tree** — `contains` is `false`, `inOrder` is empty, `size` is
  `0`, `height` is `-1`, `isEmpty` is `true`, `delete` of anything is a
  no-op, and `min`/`max` throw.
- **Inserting a duplicate** — no new node, `size` unchanged.
- **Deleting a value that isn't present** — no-op, `size` unchanged.
- **Deleting the root**, in all three cases — including deleting the
  only node in a single-node tree, which should leave the tree empty
  rather than holding a stale root.
- **Deleting a node with only a left child, and one with only a right
  child** — both are case 2, and an implementation that handles one
  direction but not the other will pass half the tests.
- **Sorted insert order** — inserting `1, 2, 3, 4, 5` must still work
  correctly, just as a degenerate tree. `height()` should equal
  `size() - 1`, and `inOrder()` should still come out sorted.
- **Negative values and zero** — nothing special, but worth confirming
  the comparisons don't assume positives.

## Stub

```kotlin
package trees

class BinarySearchTree {
    private class Node(val value: Int, var left: Node? = null, var right: Node? = null)

    private var root: Node? = null
    private var size = 0

    fun insert(value: Int) {
        TODO()
    }

    fun contains(value: Int): Boolean {
        TODO()
    }

    fun delete(value: Int) {
        TODO()
    }

    fun min(): Int {
        TODO()
    }

    fun max(): Int {
        TODO()
    }

    fun inOrder(): List<Int> {
        TODO()
    }

    fun height(): Int {
        TODO()
    }

    fun size(): Int {
        TODO()
    }

    fun isEmpty(): Boolean {
        TODO()
    }
}
```

Note `Node.value` is a `val` while `left` and `right` are `var`. That's
deliberate for cases 1 and 2 of delete, which only rewire pointers — but
case 3 as described copies a *value* into an existing node, which a
`val` won't allow. Two ways out: make `value` a `var`, or restructure
case 3 to rewire nodes instead of copying values. Pick one and know why
you picked it; the value-copy version is shorter, the pointer version
keeps `Node` immutable in its value.
