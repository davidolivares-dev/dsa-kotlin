package foundations

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RecursionTest : FunSpec({

    context("factorial") {
        test("0! and 1! are both 1") {
            factorial(0) shouldBe 1L
            factorial(1) shouldBe 1L
        }

        test("computes factorial for positive n") {
            factorial(5) shouldBe 120L
            factorial(10) shouldBe 3628800L
        }

        test("throws IllegalArgumentException for negative n") {
            shouldThrow<IllegalArgumentException> { factorial(-1) }
        }

        test("computes 20!, the largest factorial that fits in a Long") {
            factorial(20) shouldBe 2432902008176640000L
        }

        test("throws for n = 21 rather than silently overflowing") {
            shouldThrow<IllegalArgumentException> { factorial(21) }
        }
    }

    context("fibonacci") {
        test("base cases") {
            fibonacci(0) shouldBe 0L
            fibonacci(1) shouldBe 1L
        }

        test("computes fibonacci for n >= 2") {
            fibonacci(2) shouldBe 1L
            fibonacci(5) shouldBe 5L
            fibonacci(10) shouldBe 55L
        }
    }

    context("sumDigits") {
        test("zero sums to zero") {
            sumDigits(0) shouldBe 0
        }

        test("single digit sums to itself") {
            sumDigits(7) shouldBe 7
        }

        test("sums digits of a multi-digit number") {
            sumDigits(1234) shouldBe 10
        }

        test("throws IllegalArgumentException for negative n") {
            shouldThrow<IllegalArgumentException> { sumDigits(-12) }
        }
    }

    context("isPalindrome") {
        test("empty and single-character strings are palindromes") {
            isPalindrome("") shouldBe true
            isPalindrome("a") shouldBe true
        }

        test("recognizes odd-length palindromes") {
            isPalindrome("aba") shouldBe true
            isPalindrome("racecar") shouldBe true
        }

        test("recognizes even-length palindromes") {
            isPalindrome("abba") shouldBe true
        }

        test("rejects non-palindromes") {
            isPalindrome("hello") shouldBe false
            isPalindrome("ab") shouldBe false
        }

        test("is case-sensitive") {
            isPalindrome("Aba") shouldBe false
        }
    }
})
