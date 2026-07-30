# Foundations: Complexity Analysis & Recursion

Everything else in this repo leans on these two tools: a way to talk about
cost, and a way to express "solve this by solving a smaller version of
itself."

References: [`GLOSSARY.md`](../../../../GLOSSARY.md) ·
[`ROADMAP.md`](../../../../ROADMAP.md)

## Complexity analysis

Big-O describes how an algorithm's cost (time or space) grows as input size
`n` grows — not the exact runtime, the *shape* of the growth curve. It
matters because it predicts behavior at scales you haven't tested: an O(n²)
solution that "feels fine" on 100 elements can be unusable at 100,000.

A few things worth internalizing beyond the definition:
- Drop constants and lower-order terms: O(2n + 100) is O(n). The constant
  matters in practice (a tight O(n) loop can beat a loose O(log n) one for
  small n) but not in the asymptotic classification.
- Multiple inputs get multiple variables. Iterating two separate arrays of
  different lengths is O(a + b), not O(n) — collapsing them to one variable
  hides information.
- Space complexity counts
  [auxiliary space](../../../../GLOSSARY.md#auxiliary-space) unless stated
  otherwise. Recursive calls consume space too — a recursive function with
  no extra data structures still isn't O(1) space if it recurses n levels
  deep, because each call frame sits on the call stack.
- [Amortized](../../../../GLOSSARY.md#amortized-complexity) cost matters
  when an operation's worst case is rare. Don't confuse "amortized O(1)"
  with "always O(1)" — a single call can still be slow; it's the average
  over many calls that's cheap.

## Recursion

A recursive function solves a problem by calling itself on a smaller
subproblem, until it hits a case small enough to answer directly.

Every recursive function needs:
1. **Base case(s)** — the stopping condition, answered directly with no
   further recursive call. Get this wrong (missing, or unreachable) and
   the function recurses until the call stack overflows.
2. **Recursive case** — does some work, then calls itself on an input that
   is strictly closer to a base case. "Closer" is what guarantees
   termination — if the recursive call doesn't shrink the problem, it
   never ends.

Each recursive call adds a frame to the call stack, which only unwinds
(and starts multiplying/returning) once a base case is hit. For
`factorial(4)`:

```
call stack grows down --v         unwinds back up, multiplying --^

factorial(4)                                             = 4 * 6  = 24
  factorial(3)                                            = 3 * 2 = 6
    factorial(2)                                          = 2 * 1 = 2
      factorial(1)  -> base case, returns 1 directly       = 1
```

Four frames are on the stack simultaneously at the deepest point, before
any multiplication happens — this is the O(n) space cost mentioned above,
even though the function allocates no explicit data structure.

Two failure modes to watch for as you implement:
- **Off-by-one in the base case** — e.g. stopping at `n == 0` when the
  correct base case is `n <= 1`, silently producing wrong answers instead
  of crashing (which would at least be obvious).
- **Redundant recomputation** — naive recursive Fibonacci recomputes the
  same subproblems exponentially many times (`fib(5)` calls `fib(3)` twice,
  `fib(2)` three times, etc). This is *why* memoization exists — you'll
  meet it properly in Phase 8, but it's worth noticing the shape of the
  problem now.

A recursive function with no way to terminate (or a base case that's never
reached) causes a `StackOverflowError` in Kotlin/JVM — this is the
recursion analog of an infinite loop, and usually means a bug in the base
case or in how the problem shrinks.

## Check Your Understanding

1. Why is O(2n + 100) simplified to O(n)? What information does that
   simplification throw away, and when would that information matter in
   practice?
2. If a function iterates over array `a` (length `m`) and then separately
   over array `b` (length `k`), why is the correct complexity O(m + k)
   rather than O(n)?
3. Why does a recursive function that makes no extra allocations still use
   more than O(1) space?
4. What are the two required components of any correct recursive function,
   and what specifically goes wrong if each one is missing?
5. What does "amortized O(1)" actually guarantee — and what does it
   explicitly *not* guarantee about any single call?
6. Naive recursive Fibonacci is exponential time. Where exactly does the
   wasted work come from — what gets recomputed, and why?
7. What causes a `StackOverflowError`, and what does that error tell you
   about a recursive function's base case or its shrinking step?
8. Give an example (not from this doc) of a problem where the "smaller
   subproblem" a recursive case calls itself on isn't just `n - 1`.
