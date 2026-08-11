# Binary Tree

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md) ·
[Foundations notes](../foundations/FOUNDATIONS_NOTES.md) ·
[Stack notes](../stack/STACK_NOTES.md) ·
[Queue notes](../queue/QUEUE_NOTES.md)

## What it is

A binary tree is the first **non-linear** structure in this roadmap.
Every structure so far had one obvious "next" — a linked list node points
to one successor, an array index has one neighbour. A tree node points to
**two** children, so there's no single path through it. That branching is
what makes traversal a topic in its own right rather than just "loop from
the start."

Each node holds a value and two references, `left` and `right`, either of
which may be null:

```
      1
    /  \
  2     3
 / \     \
4   5     6
```

That's the tree used for every example below. Note node `3` has only a
right child — nothing requires both to exist.

**Binary tree, not binary *search* tree.** There is no ordering rule here.
Values can sit anywhere. The ordering invariant that makes lookup O(log n)
arrives with the BST, later in this phase — everything in these notes
applies to any binary tree, sorted or not.

## Vocabulary

Worth pinning down, because these get used loosely and the distinctions
matter:

- **Root** — the single node with no parent (`1` above). A tree has
  exactly one; an empty tree has a null root.
- **Leaf** — a node with no children (`4`, `5`, `6`).
- **Subtree** — any node together with everything beneath it. `2`, `4`,
  `5` form a subtree. **This is the key idea**: a subtree is itself a
  perfectly good binary tree, which is why recursion fits so naturally.
- **Depth** of a node — edges from the root down to it. `1` has depth 0,
  `4` has depth 2.
- **Height** of a tree — the longest root-to-leaf path, measured in
  edges. The tree above has height 2.
- **Level** — all nodes at the same depth. Level 1 is `[2, 3]`.

Depth counts *down* to a node; height counts *up* from the deepest leaf.
An empty tree is conventionally height `-1`, and a single node is height
`0` — that convention makes `height = 1 + max(left, right)` work without
a special case.

## Shapes

Shape drives complexity, so the terms are worth knowing:

- **Perfect** — every level completely filled. `n` nodes, height
  `log₂(n+1) - 1`.
- **Complete** — every level filled except possibly the last, which fills
  left to right. This is what lets a heap live in a flat array (Phase 4).
- **Balanced** — every node's two subtrees differ in height by at most a
  small constant, keeping height O(log n).
- **Degenerate** — every node has one child, so the tree collapses into a
  straight line:

```
1
 \
  2
   \
    3
     \
      4
```

A degenerate tree with `n` nodes has height `n-1`. It's a linked list
wearing a tree's type — same
[degenerate case](../../../../GLOSSARY.md#degenerate-case) idea flagged in
the glossary, and the reason the "O(log n) tree" claim always carries an
implicit *if balanced*.

## Traversals

Because there's no single next node, you have to *choose* an order. There
are two families.

### Depth-first: go deep before going wide

All three DFS traversals are **the same recursion**. The only thing that
changes is *when you visit the node* relative to the two recursive calls:

```
pre-order:    visit node,  recurse left,  recurse right
in-order:     recurse left,  visit node,  recurse right
post-order:   recurse left,  recurse right,  visit node
```

That's the whole difference. One line moves.

For the tree above (verified, not eyeballed):

| Traversal | Order | Mnemonic |
| --- | --- | --- |
| pre-order | `1, 2, 4, 5, 3, 6` | node **first** |
| in-order | `4, 2, 5, 1, 3, 6` | node in the **middle** |
| post-order | `4, 5, 2, 6, 3, 1` | node **last** |

Each has a use where the ordering isn't arbitrary:

- **Pre-order** sees a parent before its children — right for *copying* a
  tree or serialising it, since you must create a node before attaching
  anything to it.
- **In-order** on a **BST** emits values in sorted order. That's not a
  coincidence, it's the defining property, and it's the standard way to
  check whether a tree is a valid BST.
- **Post-order** sees both children before the parent — right for
  *deleting* a tree, or for any computation where a node's answer depends
  on its subtrees' answers. Computing height is post-order: you can't
  know a node's height until both children report theirs.

**The recursion terminates on null, not on a leaf.** A leaf still makes
two recursive calls; both immediately hit the null base case and return.
Trying to base-case on "is this a leaf" means checking whether children
exist before every call, which is more code and easy to get wrong.

### Breadth-first: finish each level before descending

Level-order visits every node at depth 0, then depth 1, and so on:
`1, 2, 3, 4, 5, 6` — grouped by level, `[[1], [2, 3], [4, 5, 6]]`.

This one **cannot** be written with plain recursion the way DFS can,
because recursion naturally dives. It needs an explicit **queue**:

```
enqueue the root
while the queue isn't empty:
    dequeue a node, visit it
    enqueue its left child if present
    enqueue its right child if present
```

That's the Phase 1 queue doing exactly what its FIFO ordering promised —
nodes come out in discovery order, so a node's children are always
processed after every node already waiting. Swap the queue for a
**stack** and you get DFS instead, iteratively. The traversal family is
determined entirely by which structure holds the pending nodes.

Which is the tie-back worth holding onto: **DFS uses a stack either way.**
Recursive DFS uses the *call stack* implicitly — the frames stacking up in
the Phase 0 factorial diagram are the same mechanism. Writing DFS with an
explicit stack just makes visible what recursion was doing for you.

## Complexity

Every traversal is **O(n) time** — each node is visited exactly once, and
there's no way to see all `n` nodes in less.

Space is where they differ, and it's about how many nodes are pending at
once:

| | space | worst case |
| --- | --- | --- |
| DFS (recursive) | O(h), h = height | O(n) on a degenerate tree |
| BFS (queue) | O(w), w = widest level | O(n) on a perfect tree |

DFS holds one call frame per level, so a balanced tree costs O(log n) —
but a degenerate one costs O(n) and can overflow the stack. BFS holds a
whole level at once; the bottom level of a perfect tree is about `n/2`
nodes, so O(n).

Neither dominates. **DFS is cheaper on wide shallow trees, BFS on deep
narrow ones.**

Search is O(n) in a plain binary tree — with no ordering rule, there's
nothing to prune, so finding a value means potentially visiting
everything. Getting that down to O(log n) is exactly what the BST's
ordering invariant buys.

## The invariant

Structurally, only one thing must hold: **it must be a tree, not a
graph.** Exactly one root, every other node reachable by exactly one path
from it, and no cycles. Point a child back at an ancestor and every
traversal here loops forever — none of them track visited nodes, because
a tree's structure is supposed to make that unnecessary.

That assumption is why graph traversal (Phase 5) needs a visited set and
tree traversal doesn't. Same algorithms otherwise.

## Space cost

O(n) for `n` nodes, two references each. Traversal adds the O(h) or O(w)
above on top.

## When to reach for it

Whenever the data is genuinely **hierarchical** — a filesystem, a DOM, an
org chart, a parsed expression — the structure mirrors the problem
instead of flattening it.

The plain binary tree is mostly a foundation rather than a destination.
What it leads to:

- **BST** (next) — adds an ordering rule, making search O(log n).
- **Heaps** (Phase 4) — a complete binary tree with a parent/child
  ordering rule, packed into an array with no pointers at all.
- **Tries** (this phase) — same recursive shape, one child per character.
- **Graphs** (Phase 5) — drop the "exactly one path" restriction and DFS
  and BFS carry over almost unchanged, plus a visited set.

## Check Your Understanding

1. What makes a tree fundamentally different to traverse than a linked
   list or array? Why is "traversal order" a real choice here?
2. What's the difference between a node's depth and a tree's height? Why
   is an empty tree conventionally height `-1` rather than `0`?
3. Pre-, in-, and post-order are the same recursion with one line moved.
   Which line, and what are the three positions?
4. Why does in-order specifically produce sorted output on a BST, when
   the other two don't?
5. Why is computing a tree's height naturally a post-order operation?
   What would go wrong trying to do it pre-order?
6. Why can't level-order be written with plain recursion the way DFS can,
   and what structure does it need instead?
7. If you replaced BFS's queue with a stack, what traversal would you
   get? What does that tell you about the relationship between the two
   families?
8. Recursive DFS looks like it uses no extra structure. What is it
   actually using, and what's its space complexity in terms of the tree's
   shape?
9. DFS is O(h) space and BFS is O(w). For a degenerate tree, which is
   cheaper? For a perfect tree? Why does neither win outright?
10. Tree traversal doesn't need a visited set, but graph traversal does.
    What property of trees makes it unnecessary, and what breaks if that
    property is violated?
