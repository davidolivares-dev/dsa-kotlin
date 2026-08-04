# Task: Stack

Build a stack from scratch, backed by a `MutableList<Int>` — from
[`STACK_NOTES.md`](STACK_NOTES.md). This is the array-backed variant
from the notes (amortized O(1) push/pop), as opposed to a linked-list
backing.

Create `src/main/kotlin/stack/Stack.kt` yourself (copy the stub below in,
then implement each function). Values are plain `Int`s.

## Operations

**`push(value: Int): Unit`**
Adds `value` to the top of the stack, in amortized O(1). Remember which
end of the backing list "top" needs to be, per the notes and the
comprehension check — the other end would turn this into an O(n)
operation.

**`pop(): Int`**
Removes and returns the top value. Throws `NoSuchElementException` if
the stack is empty (same convention used throughout the linked list
tasks — an empty-collection removal is a caller error, not a silent
no-op).

**`peek(): Int`**
Returns the top value *without* removing it. Also throws
`NoSuchElementException` on an empty stack. The difference from `pop`
should be exactly one thing: does the stack still hold that value
afterward?

**`isEmpty(): Boolean`**
Returns whether the stack currently holds any values, in O(1).

**`size(): Int`**
Returns how many values are currently on the stack, in O(1).

## Edge cases to handle across the above
- `pop`/`peek` on an empty stack — both should throw, not crash some
  other way or silently return a bogus value.
- A single push immediately followed by a pop should return that exact
  value, and leave the stack empty again.
- A sequence of pushes followed by pops should come back out in
  **reverse** order of how they went in — that's the whole point of
  LIFO, and worth a test case that pushes 3+ values and pops them all to
  confirm the order.
- `isEmpty`/`size` should stay correct across a mix of pushes and pops,
  not just when the stack is freshly created.

## Stub

```kotlin
package stack

class Stack {
    private val elements = mutableListOf<Int>()

    fun push(value: Int) {
        TODO()
    }

    fun pop(): Int {
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
