package foundations

fun factorial(n: Int): Long {
    if (n < 0) throw IllegalArgumentException("n must be a positive integer")
    if (n == 0 || n == 1) return 1
    return n * factorial(n - 1)
}

fun fibonacci(n: Int): Long {
    if (n == 0) return 0
    if (n == 1) return 1
    return fibonacci(n - 1) + fibonacci(n - 2)
}

fun sumDigits(n: Int): Int {
    if (n == 0) return 0
    return n % 10 + sumDigits(n / 10)
}

fun isPalindrome(s: String): Boolean {
    if (s.isEmpty() || s.length == 1) return true
    if (s.first() != s.last()) return false
    return isPalindrome(s.substring(1, s.lastIndex))
}