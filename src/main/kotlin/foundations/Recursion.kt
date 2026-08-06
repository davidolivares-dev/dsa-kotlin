package foundations

fun factorial(n: Int): Long {
    require(n >= 0) { "n must be non-negative, was $n" }
    if (n == 0 || n == 1) return 1
    return n * factorial(n - 1)
}

fun fibonacci(n: Int): Long =
    when (n) {
        0 -> 0
        1 -> 1
        else -> fibonacci(n - 1) + fibonacci(n - 2)
    }

fun sumDigits(n: Int): Int {
    if (n == 0) return 0
    return n % 10 + sumDigits(n / 10)
}

fun isPalindrome(s: String): Boolean {
    if (s.length <= 1) return true
    if (s.first() != s.last()) return false
    return isPalindrome(s.substring(1, s.lastIndex))
}
