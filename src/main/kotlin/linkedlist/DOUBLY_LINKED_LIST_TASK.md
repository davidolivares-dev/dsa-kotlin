# Task: Doubly Linked List

Build a doubly linked list from scratch — same core operations as the
singly linked list, plus the ones the `prev` pointer newly makes cheap —
from [`DOUBLY_LINKED_LIST_NOTES.md`](DOUBLY_LINKED_LIST_NOTES.md).

Create `src/main/kotlin/linkedlist/DoublyLinkedList.kt` yourself (copy the
stub below in, then implement each function). Values are plain `Int`s,
same as the singly linked list.

## Operations

**`addFirst(value: Int): Unit`**
Inserts a new node at the head, in O(1). Same as the singly linked list
version, but now there's a `prev` pointer to wire up too on both the new
node and the old head.

**`addLast(value: Int): Unit`**
Inserts a new node at the tail.
*Framing: compare this to the singly linked list's `addLast` — did that
one need to walk the list to find the last node? Do you need to here?*

**`deleteFirst(): Int`**
Removes the head node and returns the value it held. Throws
`NoSuchElementException` if the list is empty (same convention as the
singly linked list).

**`deleteLast(): Int`**
Removes the tail node and returns the value it held. Throws
`NoSuchElementException` if the list is empty. This operation didn't
exist on the singly linked list's task list at all — it would've been
O(n) there. Here it should be O(1).
*Framing: `tail.prev` gets you the node that needs to become the new
tail — but what does that new tail's `next` need to be set to? And
what's different about a list with only one node, where `head` and
`tail` are currently the same node?*

**`deleteValue(value: Int): Boolean`**
Removes the *first* node holding `value` (duplicates further in the list
are left alone), keeping every remaining node's `prev`/`next` pair
consistent. Returns `true` if a node was removed, `false` if `value`
never appears.
*Framing: this is the "doubled invariant" from the notes — every removal
touches two links, not one. And unlike the singly linked list, there are
now two positions that need special-case handling, not just one: what
changes about the update when the node being removed is the head?
The tail? Both at once (single-node list)?*

**`contains(value: Int): Boolean`**
Returns whether `value` appears anywhere in the list. Plain O(n) walk
from `head` (or `tail`, doesn't matter which for this one).

**`reverse(): Unit`**
Reverses the list in place. After reversing `[1, 2, 3]`, the list holds
`[3, 2, 1]`.
*Framing: for the singly linked list, reversing meant redirecting one
pointer per node (`next`). Here, every node has two pointers — what
needs to happen to both of them at each node? And once you're done
walking the whole list, what needs to happen to `head` and `tail`
themselves?*

**`toList(): List<Int>`**
Walks the chain from `head` and returns the values as a `List<Int>`, in
order. Same traversal helper as the singly linked list version.

## Edge cases to handle across the above
- Operating on an empty list (`head == null` and `tail == null`) —
  `addLast`, `contains`, `deleteValue`, `reverse`, and `toList` should
  all behave sensibly; `deleteFirst`/`deleteLast` are the two that are
  *supposed* to throw.
- A single-node list, where `head` and `tail` point at the same node —
  deleting it (via any of the delete operations) should leave the list
  fully empty (`head` and `tail` both `null`), not just half-updated.
- `deleteValue`/`deleteLast` when the target is specifically the head,
  specifically the tail, and specifically a middle node — each touches
  different combinations of `head`/`tail`/neighboring `prev`/`next`.
- `deleteValue` when `value` doesn't appear at all.

## Stub

```kotlin
package linkedlist

class DoublyLinkedList {
    private class Node(val value: Int, var prev: Node? = null, var next: Node? = null)

    private var head: Node? = null
    private var tail: Node? = null

    fun addFirst(value: Int) {
        TODO()
    }

    fun addLast(value: Int) {
        TODO()
    }

    fun deleteFirst(): Int {
        TODO()
    }

    fun deleteLast(): Int {
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
