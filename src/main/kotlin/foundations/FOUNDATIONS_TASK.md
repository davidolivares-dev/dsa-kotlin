# Task: Recursion Warm-ups

Implement four small recursive functions. Each is deliberately simple —
the point isn't the problem, it's building the reflex of identifying the
base case and recursive case correctly before writing anything else. See
[`FOUNDATIONS_NOTES.md`](FOUNDATIONS_NOTES.md) for the concepts these draw on.

Create `src/main/kotlin/foundations/Recursion.kt` yourself (copy the stub
below in, then implement each function — don't just fill in `TODO()`
without first writing out, as a comment or on paper, what the base case
and recursive case are).

## Operations

**`factorial(n: Int): Long`**
Returns `n!` (n × (n-1) × ... × 1). `factorial(0) == 1`. Throw
`IllegalArgumentException` for `n < 0` — factorial isn't defined there,
and silently returning something plausible-looking would hide a caller
bug instead of surfacing it.

**`fibonacci(n: Int): Long`**
0-indexed: `fibonacci(0) == 0`, `fibonacci(1) == 1`,
`fibonacci(n) == fibonacci(n-1) + fibonacci(n-2)` for `n >= 2`. Naive
recursive implementation is fine and expected here — this is the exercise
that motivates memoization later, not a place to prematurely optimize.

**`sumDigits(n: Int): Int`**
Recursively sums the digits of a non-negative integer, e.g.
`sumDigits(1234) == 10`, `sumDigits(0) == 0`, `sumDigits(7) == 7`. Assume
`n >= 0`.
*Framing: what arithmetic operation isolates just the last digit of a
number, and what operation gives you "the number with that digit
removed"? Once you have both, the base case is whatever value one of
those operations eventually bottoms out at.*

**`isPalindrome(s: String): Boolean`**
Recursively checks whether `s` reads the same forwards and backwards.
`isPalindrome("racecar") == true`, `isPalindrome("hello") == false`.
Empty string and single-character strings are palindromes by definition.
Case-sensitive, no need to strip spaces/punctuation — keep this one
simple.
*Framing: what are the two specific characters you need to compare first
— and if they match, what smaller string (not just "one character
shorter") represents "everything left to check"?*

## Stub

```kotlin
package foundations

fun factorial(n: Int): Long {
    TODO()
}

fun fibonacci(n: Int): Long {
    TODO()
}

fun sumDigits(n: Int): Int {
    TODO()
}

fun isPalindrome(s: String): Boolean {
    TODO()
}
```
