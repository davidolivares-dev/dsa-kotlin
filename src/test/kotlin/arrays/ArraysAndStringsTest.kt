package arrays

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ArraysAndStringsTest : FunSpec({

    context("reverseArray") {
        test("empty array remains empty") {
            val arr = intArrayOf()
            reverseArray(arr)
            arr.toList() shouldBe emptyList()
        }

        test("single-element array is unchanged") {
            val arr = intArrayOf(5)
            reverseArray(arr)
            arr.toList() shouldBe listOf(5)
        }

        test("reverses an even-length array") {
            val arr = intArrayOf(1, 2, 3, 4)
            reverseArray(arr)
            arr.toList() shouldBe listOf(4, 3, 2, 1)
        }

        test("reverses an odd-length array") {
            val arr = intArrayOf(1, 2, 3, 4, 5)
            reverseArray(arr)
            arr.toList() shouldBe listOf(5, 4, 3, 2, 1)
        }
    }

    context("reverseString") {
        test("empty char array remains empty") {
            val chars = charArrayOf()
            reverseString(chars)
            String(chars) shouldBe ""
        }

        test("single character is unchanged") {
            val chars = charArrayOf('a')
            reverseString(chars)
            String(chars) shouldBe "a"
        }

        test("reverses a char array") {
            val chars = "hello".toCharArray()
            reverseString(chars)
            String(chars) shouldBe "olleh"
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

    context("removeDuplicates") {
        test("empty array returns 0") {
            val arr = intArrayOf()
            removeDuplicates(arr) shouldBe 0
        }

        test("single-element array returns 1") {
            val arr = intArrayOf(7)
            removeDuplicates(arr) shouldBe 1
        }

        test("array of all duplicates collapses to one") {
            val arr = intArrayOf(2, 2, 2, 2)
            removeDuplicates(arr) shouldBe 1
        }

        test("array of all-unique values keeps every element") {
            val arr = intArrayOf(1, 2, 3, 4)
            val count = removeDuplicates(arr)
            count shouldBe 4
            arr.toList() shouldBe listOf(1, 2, 3, 4)
        }

        test("removes duplicates from a mixed sorted array") {
            val arr = intArrayOf(1, 1, 2, 2, 3)
            val count = removeDuplicates(arr)
            count shouldBe 3
            arr.copyOfRange(0, count).toList() shouldBe listOf(1, 2, 3)
        }
    }
})
