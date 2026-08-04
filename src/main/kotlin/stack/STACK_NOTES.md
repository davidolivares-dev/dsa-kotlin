# Stack

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md) ·
[Singly Linked List notes](../linkedlist/SINGLY_LINKED_LIST_NOTES.md) ·
[Arrays & Strings notes](../arrays/ARRAYS_AND_STRINGS_NOTES.md)

## What it is

A stack is a **LIFO** structure — Last In, First Out. You can only ever
interact with the **top**: `push` adds a new element on top, `pop`
removes and returns the top element, `peek` looks at the top without
removing it. There's no operation for reaching into the middle — that
restriction is the entire point of a stack, not a limitation of it.

```
push(1)      push(2)      push(3)      pop() -> 3
 ___          ___          ___          ___
| 1 |        | 2 |        | 3 | <-top  | 2 | <-top
|___|        |___|        |___|        |___|
             | 1 |        | 2 |        | 1 |
             |___|        |___|        |___|
                          | 1 |
                          |___|
```

A stack is an **abstract data type** — an interface, not a specific
storage layout. It's typically backed by one of two structures you
already know:

- A dynamic array (`MutableList`), where "top" is the *end* of the list.
- A singly linked list, where "top" is the `head`.

Both choices give O(1) push/pop, for the same reason: in both cases,
"top" was chosen specifically to line up with whichever end of the
underlying structure is cheap to insert/delete at. (Recall from the array
notes that appending to the *end* is the cheap O(1) case, and from the
linked list notes that inserting at the *head* is the cheap O(1) case —
picking the "wrong" end for either backing structure would make push/pop
O(n) instead.)

## Complexity

`push`, `pop`, and `peek` are all O(1) — but worth noticing this isn't
quite the *same* O(1) depending on the backing structure:

- Array-backed: push/pop is [amortized](../../../../GLOSSARY.md#amortized-complexity)
  O(1) — occasionally a resize happens, which is O(n) for that one call,
  but it's rare enough to average out.
- Linked-list-backed: push/pop is O(1) *every single call*, no
  amortization involved — a head insert/delete never triggers anything
  more expensive.

Both are correctly described as "O(1)," but that distinction is worth
being able to explain if asked.

What a stack does **not** support, by design: access to any element
other than the top. There's no O(1) or even O(n) "give me the 3rd
element from the top" operation — if you need that, you don't want a
stack, you want the underlying array or list directly.

## The invariant

Only the top is ever directly reachable, and every push/pop must
preserve LIFO order: whatever was pushed most recently is the next (and
only) thing that can come off. This is simpler than either linked list's
invariant — there's exactly one place mutation happens, so there's no
"wrong pointer order" failure mode to worry about, as long as the
backing structure's own top-end operation is used correctly.

A doubly linked list would also give O(1) push/pop if used as a stack's
backing structure — but it would be pure overhead. A stack never needs
to walk backward or access both ends, so the extra `prev` pointer per
node just costs memory for a capability the stack never uses. This is a
concrete case of matching a structure's *capabilities* to what's
*actually needed*, not just picking the most powerful option available.

## Space cost

O(n) [auxiliary space](../../../../GLOSSARY.md#auxiliary-space) for `n`
elements, plus whatever per-element overhead the backing structure adds
(none extra for an array backing, one reference per node for a linked
list backing).

## When to reach for it

Reach for a stack whenever "undo the most recent thing" or "process in
reverse arrival order" describes the problem, and you don't need access
to anything but the most recent item.

Concretely: the **call stack** itself is a stack — every recursive call
pushes a new frame, and every return pops one off (this is exactly what
"space complexity = call stack depth" meant in the Phase 0 recursion
notes). Other common cases:
balanced-parentheses/bracket-matching, undo history, converting or
evaluating expressions (postfix/infix), a "back" button's history, and —
looking ahead — an explicit stack is how you'd convert a recursive DFS
into an iterative one (Phase 3/5), and backtracking algorithms (Phase 7)
are conceptually stack-shaped even when implemented via recursion's
implicit call stack rather than an explicit one.

## Check Your Understanding

1. What does LIFO mean? How is it different from FIFO, which the next
   linear structure (Queue) uses instead?
2. Why can a stack only ever access its top element — what's the
   tradeoff being made compared to an array or a linked list, which both
   support more general access?
3. If a stack is backed by a dynamic array, why is "top" the *end* of
   the array rather than the front? What would happen to push/pop's
   complexity if you picked the front instead?
4. If a stack is backed by a singly linked list, why is "top" the `head`
   rather than the tail?
5. Would a doubly linked list make a *better* backing structure for a
   stack than a singly linked list? Why or why not?
6. Push/pop are both described as O(1) whether the stack is array-backed
   or linked-list-backed — but is it the exact same *kind* of O(1) in
   both cases? What's the distinction?
7. Give a concrete real-world example (something other than "the call
   stack") where a stack's LIFO ordering is exactly the right fit.
8. How does the call stack relate to recursion, and what happens when
   you exceed it? Connect this back to what "space complexity" meant for
   a recursive function in the foundations notes.
