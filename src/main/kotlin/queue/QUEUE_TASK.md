# Task: Queue

Build **two** queue implementations from scratch — from
[`QUEUE_NOTES.md`](QUEUE_NOTES.md). The naive array-shifting approach
from the notes isn't worth implementing (it's O(n) dequeue by
construction, a teaching baseline rather than something you'd actually
want to write); the two implementations below are the ones worth having
in your hands.

Create `src/main/kotlin/queue/LinkedQueue.kt` and
`src/main/kotlin/queue/CircularBufferQueue.kt` yourself (copy the stubs
below in, then implement each function). Values are plain `Int`s.

## `LinkedQueue` — singly linked list backing

**`enqueue(value: Int): Unit`**
Adds `value` at the tail, in O(1). Same shape as `addLast` from the
singly linked list task, using the tracked `tail` reference.

**`dequeue(): Int`**
Removes and returns the value at `head`, in O(1). Throws
`NoSuchElementException` if the queue is empty (same convention used
throughout the linked list and stack tasks).

**`peek(): Int`**
Returns the value at `head` without removing it. Also throws
`NoSuchElementException` on an empty queue.

**`isEmpty(): Boolean`**
Returns whether the queue currently holds any values, in O(1).

**`size(): Int`**
Returns how many values are currently in the queue, in O(1).
*Framing: same issue as the stack task's linked-list variant would have
had — a plain singly linked list doesn't give you a count for free.
What field do you need to add, and where do `enqueue` and `dequeue` each
need to touch it?*

## `CircularBufferQueue` — fixed-size array backing

**`enqueue(value: Int): Unit`**
Writes `value` at the `back` index, then advances `back`. Throws
`IllegalStateException` if the buffer is already full — unlike the
stack's `MutableList` backing, this array can't grow, so a full buffer
is a real caller error, not something to silently ignore or overwrite.
*Framing: advancing `back` needs the wraparound formula from the notes
(`(back + 1) % capacity`), not a plain increment. And before you write
anything, how do you know whether there's room — what does "full" mean
in terms of your tracked count vs. `capacity`?*

**`dequeue(): Int`**
Reads the value at `front`, then advances `front`, and returns the value
read. Throws `NoSuchElementException` if the buffer is empty.
*Framing: `front` needs the same wraparound treatment as `back` did.*

**`peek(): Int`**
Returns the value at `front` without advancing anything. Throws
`NoSuchElementException` if empty.

**`isEmpty(): Boolean`**
Returns whether the buffer currently holds any values, in O(1).

**`isFull(): Boolean`**
Returns whether the buffer is at capacity, in O(1).
*Framing: this is the `front == back` ambiguity from the notes made
concrete — `isEmpty` and `isFull` can't be distinguished by comparing
`front` and `back` alone. What field resolves it, and what does each
function actually need to check?*

**`size(): Int`**
Returns how many values are currently in the buffer, in O(1).

## Edge cases to handle across both
- `dequeue`/`peek` on an empty queue — both should throw.
- `enqueue` on a full `CircularBufferQueue` — should throw, not silently
  drop the value or overwrite an existing one.
- A sequence of enqueues followed by dequeues should come back out in
  the **same** order they went in (FIFO) — the opposite of the stack
  task's reversed-order expectation.
- For `CircularBufferQueue` specifically: enqueue enough values to reach
  capacity, dequeue some, then enqueue again — this is the case that
  actually exercises the wraparound. A test that never fills the buffer
  once won't catch a broken modulo.

## Stubs

```kotlin
package queue

class LinkedQueue {
    private class Node(val value: Int, var next: Node? = null)

    private var head: Node? = null
    private var tail: Node? = null
    private var count = 0

    fun enqueue(value: Int) {
        TODO()
    }

    fun dequeue(): Int {
        TODO()
    }

    fun peek(): Int {
        TODO()
    }

    fun isEmpty(): Boolean {
        TODO()
    }

    fun size(): Int {
        TODO()
    }
}
```

```kotlin
package queue

class CircularBufferQueue(private val capacity: Int) {
    private val buffer = IntArray(capacity)
    private var front = 0
    private var back = 0
    private var count = 0

    fun enqueue(value: Int) {
        TODO()
    }

    fun dequeue(): Int {
        TODO()
    }

    fun peek(): Int {
        TODO()
    }

    fun isEmpty(): Boolean {
        TODO()
    }

    fun isFull(): Boolean {
        TODO()
    }

    fun size(): Int {
        TODO()
    }
}
```
