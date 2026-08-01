# Task: Singly Linked List

Build a singly linked list from scratch — the node type, and the core
operations covering head insertion, tail insertion, deletion (including
the invariant-preserving kind), search, and reversal — from
[`NOTES.md`](NOTES.md).

Create `src/main/kotlin/linkedlist/SinglyLinkedList.kt` yourself (copy the
stub below in, then implement each function). Values are plain `Int`s —
no generics here, so the focus stays on the pointer mechanics rather than
type parameter syntax.

## Operations

**`addFirst(value: Int): Unit`**
Inserts a new node at the head, in O(1). After `addFirst(1)` then
`addFirst(0)`, the list holds `[0, 1]`. Works the same whether the list is
currently empty or not.

**`addLast(value: Int): Unit`**
Inserts a new node at the tail. Since this is a *singly* linked list with
no tracked tail reference, this means walking from `head` until you find
the last node.
*Framing: what's different about the empty-list case here — is there even
an existing node to walk to and link from?*

**`deleteFirst(): Int`**
Removes the head node and returns the value it held. Throws
`NoSuchElementException` if the list is empty (matching the convention
Kotlin's own `ArrayDeque.removeFirst()` uses — an empty-collection removal
is a caller error, not a silent no-op).

**`deleteValue(value: Int): Boolean`**
Removes the *first* node holding `value` (there may be duplicates further
in — leave those alone), preserving the connectivity of every node around
it. Returns `true` if a node was removed, `false` if `value` never
appears.
*Framing: this is exactly the "wrong pointer order" failure mode from
`NOTES.md` waiting to happen. Before you unlink the target node, what
reference do you need to already be holding? And what's different about
removing the head node specifically — is there a node before it to
update at all?*

**`contains(value: Int): Boolean`**
Returns whether `value` appears anywhere in the list. Plain O(n) walk from
`head`.

**`reverse(): Unit`**
Reverses the list in place — no new nodes allocated, `next` pointers get
redirected. After reversing `[1, 2, 3]`, the list holds `[3, 2, 1]`, and
what used to be the head is now the tail.
*Framing: at each node, you need to point `next` backward — at the node
you just came from — but the moment you overwrite `next`, you lose the
only way to reach the rest of the original chain. What do you need to
save **before** you overwrite it? You'll be tracking three references at
once as you walk forward — what does each one point to, right before you
advance to the next node?*

**`toList(): List<Int>`**
Walks the chain from `head` and returns the values as a `List<Int>`, in
order. Not a "real" linked-list operation — just a traversal helper so
tests (and you, debugging) can inspect the whole list at once instead of
one node at a time. Empty list returns an empty list.

## Edge cases to handle across the above
- Operating on an empty list (`head == null`) — `addLast`, `contains`,
  `deleteValue`, `reverse`, and `toList` should all behave sensibly rather
  than crashing; `deleteFirst` is the one exception that's *supposed* to
  throw.
- A single-node list — deleting it should leave the list empty, not
  broken; reversing it is a no-op.
- `deleteValue` when the target is the head node vs. a node in the
  middle/tail — these need different handling, per the framing question
  above.
- `deleteValue` when `value` doesn't appear at all.

## Stub

```kotlin
package linkedlist

class SinglyLinkedList {
    private class Node(val value: Int, var next: Node? = null)

    private var head: Node? = null

    fun addFirst(value: Int) {
        TODO()
    }

    fun addLast(value: Int) {
        TODO()
    }

    fun deleteFirst(): Int {
        TODO()
    }

    fun deleteValue(value: Int): Boolean {
        TODO()
    }

    fun contains(value: Int): Boolean {
        TODO()
    }

    fun reverse() {
        TODO()
    }

    fun toList(): List<Int> {
        TODO()
    }
}
```
