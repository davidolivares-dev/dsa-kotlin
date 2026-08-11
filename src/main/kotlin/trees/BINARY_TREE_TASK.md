# Task: Binary Tree

Build a binary tree and its traversals from scratch — from
[`BINARY_TREE_NOTES.md`](BINARY_TREE_NOTES.md). Values are plain `Int`s,
with no ordering rule (that arrives with the BST).

Create `src/main/kotlin/trees/BinaryTree.kt` yourself (copy the stub
below in, then implement each function).

## How the tree gets built

Unlike every structure so far, there's no `insert` here. A plain binary
tree has no rule saying *where* a new value belongs — that's precisely
what the BST adds. So the tree is built by handing a root node to the
constructor, and the tests assemble specific shapes directly.

That means `Node` is **public**, not private: the tests need to construct
one. It's the only structure in this repo where that's true, and it's
because the shape itself is the input rather than something the class
decides.

An empty tree is `BinaryTree(null)`.

## The tree used in the examples

Every expected output below refers to this tree:

```
      1
    /  \
  2     3
 / \     \
4   5     6
```

Note `3` has only a right child.

## Traversals

Each returns a `List<Int>` in the traversal's order. Empty tree returns
an empty list.

**`preOrder(): List<Int>`** → `[1, 2, 4, 5, 3, 6]`

**`inOrder(): List<Int>`** → `[4, 2, 5, 1, 3, 6]`

**`postOrder(): List<Int>`** → `[4, 5, 2, 6, 3, 1]`

*Framing for all three: the recursion is identical — only the position of
the "add this node's value" line moves. Write one, then get the other two
by moving that line. If you find yourself writing three genuinely
different algorithms, step back.*

*Second framing: the public function returns a `List<Int>`, but the
recursion needs to carry an accumulator down through the calls. What
shape does that take — a private helper taking the list as a parameter,
or something else?*

**`levelOrder(): List<List<Int>>`** → `[[1], [2, 3], [4, 5, 6]]`

Note the return type: a list **per level**, not one flat list. Empty tree
returns an empty list.

*Framing: this is the one traversal recursion won't give you for free. It
needs a queue — `ArrayDeque<Node>` from the standard library is fine here
(you already built one from scratch in Phase 1; the lesson now is the
traversal, not the container). The grouping-by-level part is the twist:
if you dequeue one node at a time you lose track of where levels break.
What do you know at the top of each outer iteration that tells you
exactly how many nodes belong to the current level?*

## Properties

**`height(): Int`**
Longest root-to-leaf path in **edges**. Empty tree is `-1`, a single node
is `0`, the example tree is `2`.
*Framing: a node's height depends on both children's heights, so this is
return-up rather than parameter-down. The `-1` for empty isn't arbitrary
— it's what makes `1 + max(left, right)` work for a leaf without a
special case. Check that: what does a leaf compute if its null children
report `-1`?*

**`size(): Int`**
Total node count. Empty tree is `0`, the example tree is `6`.

**`countLeaves(): Int`**
Nodes with no children. Empty tree is `0`, the example tree is `3`
(`4`, `5`, `6`).
*Framing: three cases, not two — null, leaf, and internal node. What
distinguishes the second from the third?*

**`contains(value: Int): Boolean`**
Whether any node holds `value`. `contains(5)` is `true`, `contains(9)` is
`false`.
*Framing: with no ordering rule there's nothing to prune, so this is O(n)
— you may have to check every node. But it should still stop early once
it finds a match rather than always walking the whole tree. Which
operator gives you that short-circuit for free?*

## Edge cases to handle

- **Empty tree** (`BinaryTree(null)`) — every traversal returns an empty
  list, `size` and `countLeaves` are `0`, `height` is `-1`, `contains` is
  `false` for anything. None of these should throw.
- **Single node** — all three DFS traversals return one element,
  `levelOrder` returns `[[value]]`, `height` is `0`, `countLeaves` is
  `1`.
- **A node with only one child** — the example tree's `3` covers this.
  Easy to break by assuming both children exist, or by basing the
  recursion on "am I a leaf" instead of "is this null."
- **Degenerate tree** (every node has one child) — traversals must still
  work, and `height` equals `size - 1`.
- **Duplicate values** — nothing forbids them. `size` counts nodes, not
  distinct values, and `contains` returns on the first match.

## Stub

```kotlin
package trees

class BinaryTree(private val root: Node?) {
    class Node(val value: Int, val left: Node? = null, val right: Node? = null)

    fun preOrder(): List<Int> {
        TODO()
    }

    fun inOrder(): List<Int> {
        TODO()
    }

    fun postOrder(): List<Int> {
        TODO()
    }

    fun levelOrder(): List<List<Int>> {
        TODO()
    }

    fun height(): Int {
        TODO()
    }

    fun size(): Int {
        TODO()
    }

    fun countLeaves(): Int {
        TODO()
    }

    fun contains(value: Int): Boolean {
        TODO()
    }
}
```

Note `Node`'s `left` and `right` are `val`, not `var` — this tree is
built once and then only read. The BST will need `var` so it can rewire
pointers on insert and delete; here nothing mutates after construction.
