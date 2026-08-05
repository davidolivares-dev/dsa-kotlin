# Queue

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md) ·
[Stack notes](../stack/STACK_NOTES.md) ·
[Singly Linked List notes](../linkedlist/SINGLY_LINKED_LIST_NOTES.md) ·
[Arrays & Strings notes](../arrays/ARRAYS_AND_STRINGS_NOTES.md)

## What it is

A queue is a **FIFO** structure — First In, First Out. `enqueue` adds a
new element at the **back**, `dequeue` removes and returns whichever
element is at the **front** — whoever's been waiting longest. This is
the direct opposite of a stack's LIFO ordering: a stack always serves
whoever arrived *most* recently, a queue always serves whoever arrived
*least* recently.

```
enqueue(1)   enqueue(2)   enqueue(3)   dequeue() -> 1
front  back  front  back  front  back  front  back
 [1]         [1][2]       [1][2][3]       [2][3]
```

Like a stack, a queue is an **abstract data type**, not one specific
storage layout. Three implementations are worth knowing, each with a
different complexity story:

1. A plain dynamic array (`MutableList`), enqueueing at the end and
   dequeueing from the front.
2. A singly linked list.
3. A **circular buffer** — a fixed-size array with wraparound indices.

## Complexity, and why the naive array approach falls short

**Naive array-backed** (enqueue at the end, dequeue from the front):
`enqueue` is amortized O(1), same as any dynamic array append. But
`dequeue` is O(n) — removing from the *front* of an array means every
remaining element has to shift left by one to stay contiguous, exactly
the array-notes lesson about front removal being the expensive case.
This makes the naive array approach a poor choice whenever dequeue
actually gets called often.

**Linked-list-backed**: enqueue at the `tail`, dequeue from the `head`.
Both are O(1) — and notice this only needs a **singly** linked list, not
a doubly one. A doubly linked list's whole advantage over singly was
O(1) *tail deletion*; a queue never deletes from the tail, only inserts
there, so that weakness of a singly linked list never actually gets
triggered. A tracked `tail` reference (for O(1) enqueue) plus `head` (for
O(1) dequeue) is all a queue needs — the extra `prev` pointer a doubly
linked list would add is memory spent on a capability this access
pattern never uses.

**Circular buffer**: think of the array not as a straight line, but as a
*loop* — once you reach the last slot, the next step wraps back around
to the first, the same way a clock's hand goes from 12 back to 1.
`back` marks the next empty slot to fill; `front` marks the next full
slot to empty. Both only ever move forward around the loop:

```
[ 0 ]-->[ 1 ]-->[ 2 ]-->[ 3 ]
  ^                       |
  +-----------------------+
       (wraps back to 0)
```

Each operation only ever touches the one slot its marker currently
points at, then advances that marker one step around the loop:

```
enqueue: buffer[back] = value; back = (back + 1) % capacity
dequeue: value = buffer[front]; front = (front + 1) % capacity
```

(The `%` — modulo, i.e. remainder after division — is just the math
that makes "one step past the last slot" land back on the first slot.
`3 + 1 = 4`, and `4 % 4 = 0`.)

Since no element ever has to move, both operations stay O(1) no matter
how many times the markers have already looped around. The one real
cost: the capacity is fixed, so once the loop is completely full, there's
nowhere left to write until a `dequeue` frees a slot back up.

Walking through `capacity = 4` step by step — watch `front`/`back` wrap
around at index `3 -> 0`:

```
capacity 4, empty:
  0    1    2    3
[ _ ][ _ ][ _ ][ _ ]
  *                    (front & back coincide)

after enqueue(1), enqueue(2), enqueue(3):
  0    1    2    3
[ 1 ][ 2 ][ 3 ][ _ ]
  f              b     f=front, b=back

after dequeue() -> 1, dequeue() -> 2:
  0    1    2    3
[ _ ][ _ ][ 3 ][ _ ]
            f    b     f=front, b=back

after enqueue(4)  (back wraps from 3 to 0):
  0    1    2    3
[ _ ][ _ ][ 3 ][ 4 ]
  b         f          f=front, b=back

after enqueue(5):
  0    1    2    3
[ 5 ][ _ ][ 3 ][ 4 ]
       b    f          f=front, b=back

after enqueue(6)  (now FULL):
  0    1    2    3
[ 5 ][ 6 ][ 3 ][ 4 ]
            *          (front & back coincide)
```

Notice the first diagram (truly empty) and the last one (completely
full) both end up with `front == back` — just at different index values.
That collision is exactly the invariant gotcha covered next: the indices
alone can't tell those two states apart.

## The invariant

FIFO order must hold: whichever element has been in the queue longest is
always the next one out. For a linked-list backing, this is the same
"don't reassign a pointer before you've captured what you still need"
caution from the linked list notes, just applied to `tail` instead of
`head`.

For a circular buffer specifically, there's a sharper invariant gotcha:
**`front == back` is ambiguous** — it could mean the buffer is
completely empty, or completely full, and the indices alone can't tell
you which. Implementations resolve this either by tracking a separate
`size`/count field, or by deliberately leaving one slot always unused so
"full" and "empty" produce genuinely different index states.

## Space cost

A circular buffer allocates its full fixed capacity upfront, regardless
of how many elements are actually in use — no per-element pointer
overhead, but potential waste if the queue usually sits far below
capacity. A linked-list-backed queue is O(n) for exactly however many
elements are present, plus one reference per node (cheaper than a doubly
linked list's two, per the reasoning above) — but it grows and shrinks
node-by-node with no fixed ceiling.

## When to reach for it

The naive array-backed approach is mostly a teaching baseline — once you
know dequeue is O(n) there, a linked-list or circular-buffer backing is
almost always the better real choice for anything performance-sensitive.

Linked-list-backed queues fit unbounded, unpredictable workloads: task
scheduling, request queues, print queues — and, notably, breadth-first
traversal (Phase 3/5), which processes nodes in exactly the "whoever got
discovered first gets visited first" order a queue naturally gives you.

Circular buffers fit fixed-capacity, high-throughput scenarios:
producer/consumer buffering, streaming/audio/video buffers, rate
limiting. Real-world data point: Kotlin and Java's own `ArrayDeque`
implementations use a circular buffer internally, precisely to get O(1)
operations at both ends without either the naive array's shifting cost
or a linked list's per-node pointer overhead.

## Check Your Understanding

1. What does FIFO mean, and how is it the opposite of the stack's LIFO
   ordering?
2. Why is `dequeue` O(n) for a naive array-backed queue that enqueues at
   the end and dequeues from the front? What specifically causes that
   cost?
3. Why does a *singly* linked list suffice for an O(1)-both-operations
   queue, when a doubly linked list's main advantage over singly was
   specifically about deleting from the tail?
4. What is a circular buffer, and how does it get O(1) enqueue/dequeue
   on a fixed-size array without the naive approach's shifting cost?
5. In a circular buffer, why is `front == back` ambiguous, and what's a
   common way to resolve that ambiguity?
6. Give a concrete real-world scenario where a queue is exactly the
   right tool and a stack would produce the wrong behavior. Be specific
   about what goes wrong if you used LIFO order instead.
7. What's the space trade-off between a circular buffer and a
   linked-list-backed queue?
8. Where does a queue show up as a supporting structure for an algorithm
   you haven't learned yet, based on what "FIFO" implies about visit
   order?
