package foundations

fun factorial(n: Int): Long {
    require(n in 0..20) { "n must be in 0..20 (20! is the largest Long), was $n" }
    if (n <= 1) return 1
    return n * factorial(n - 1)
}

fun fibonacci(n: Int): Long =
    when (n) {
        0 -> 0
        1 -> 1
        else -> fibonacci(n - 1) + fibonacci(n - 2)
    }

fun sumDigits(n: Int): Int {
    require(n >= 0) { "n must be non-negative, was $n" }
    if (n == 0) return 0
    return n % 10 + sumDigits(n / 10)
}

fun isPalindrome(s: String): Boolean = isPalindrome(s, 0, s.lastIndex)

private fun isPalindrome(s: String, left: Int, right: Int): Boolean {
    if (left >= right) return true
    if (s[left] != s[right]) return false
    return isPalindrome(s, left + 1, right - 1)
}
