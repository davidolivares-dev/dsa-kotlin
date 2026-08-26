package trees

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// The pair worth watching throughout: contains asks "does a word end
// here", startsWith asks "does this path exist". Every test that checks
// one against the other on the same input is probing the end-of-word
// flag, which is the piece a trie is wrong without.

private fun trieOf(vararg words: String): Trie {
    val trie = Trie()
    words.forEach { trie.insert(it) }
    return trie
}

//  root
//   ├── c ─ a ─┬─ r* ─ t*
//   │          └─ t*
//   └── d ─ o* ─ g*
private fun exampleTrie() = trieOf("car", "cart", "cat", "do", "dog")

class TrieTest : FunSpec({

    context("insert") {
        test("stores a single word") {
            val trie = trieOf("cat")
            trie.contains("cat") shouldBe true
            trie.size() shouldBe 1
        }

        test("stores words that share a prefix") {
            exampleTrie().words() shouldBe listOf("car", "cart", "cat", "do", "dog")
        }

        test("inserting a duplicate leaves size unchanged") {
            val trie = trieOf("cat")
            repeat(5) { trie.insert("cat") }
            trie.size() shouldBe 1
            trie.words() shouldBe listOf("cat")
        }

        test("a word inserted after its own extension still counts as new") {
            // every node for "do" already exists once "dog" is stored, so
            // an implementation that counted created nodes would miss this
            val trie = trieOf("dog", "do")
            trie.size() shouldBe 2
            trie.contains("do") shouldBe true
            trie.contains("dog") shouldBe true
        }

        test("a word inserted before its own extension is unaffected") {
            val trie = trieOf("do", "dog")
            trie.size() shouldBe 2
            trie.words() shouldBe listOf("do", "dog")
        }

        test("is case sensitive and accepts non-ascii characters") {
            val trie = trieOf("Cafe", "cafe", "café")
            trie.size() shouldBe 3
            trie.contains("café") shouldBe true
            trie.contains("CAFE") shouldBe false
        }
    }

    context("contains") {
        test("finds every stored word") {
            val trie = exampleTrie()
            for (word in listOf("car", "cart", "cat", "do", "dog")) {
                trie.contains(word) shouldBe true
            }
        }

        test("rejects a path that exists but ends no word") {
            // this is the whole job of the end-of-word flag
            val trie = exampleTrie()
            trie.contains("c") shouldBe false
            trie.contains("ca") shouldBe false
            trie.contains("d") shouldBe false
        }

        test("rejects a word extending past anything stored") {
            exampleTrie().contains("carts") shouldBe false
            exampleTrie().contains("doge") shouldBe false
        }

        test("rejects a word sharing no prefix at all") {
            exampleTrie().contains("zebra") shouldBe false
        }

        test("is false on an empty trie without throwing") {
            Trie().contains("anything") shouldBe false
        }
    }

    context("startsWith") {
        test("is true for every proper prefix of every stored word") {
            val trie = exampleTrie()
            for (word in trie.words()) {
                for (length in 0..word.length) {
                    trie.startsWith(word.take(length)) shouldBe true
                }
            }
        }

        test("is true where contains is false, for a path with no word end") {
            val trie = exampleTrie()
            for (prefix in listOf("c", "ca", "d")) {
                trie.startsWith(prefix) shouldBe true
                trie.contains(prefix) shouldBe false
            }
        }

        test("is false for a path that runs out partway") {
            exampleTrie().startsWith("doge") shouldBe false
            exampleTrie().startsWith("carts") shouldBe false
        }

        test("is false for a path sharing no prefix") {
            exampleTrie().startsWith("z") shouldBe false
        }

        test("is false on an empty trie for any non-empty prefix") {
            Trie().startsWith("a") shouldBe false
        }
    }

    context("the empty string") {
        test("is a valid path in any trie, including an empty one") {
            // a zero-length walk trivially succeeds and lands on the root
            Trie().startsWith("") shouldBe true
            exampleTrie().startsWith("") shouldBe true
        }

        test("is not a stored word until it is inserted") {
            Trie().contains("") shouldBe false
            exampleTrie().contains("") shouldBe false
        }

        test("can be inserted, and creates no nodes") {
            val trie = Trie()
            trie.insert("")
            trie.contains("") shouldBe true
            trie.size() shouldBe 1
            trie.isEmpty() shouldBe false
            trie.words() shouldBe listOf("")
        }

        test("inserted twice still counts once") {
            val trie = Trie()
            trie.insert("")
            trie.insert("")
            trie.size() shouldBe 1
        }

        test("coexists with ordinary words") {
            val trie = Trie()
            trie.insert("")
            trie.insert("a")
            trie.words() shouldBe listOf("", "a")
            trie.size() shouldBe 2
        }
    }

    context("wordsWithPrefix") {
        test("returns every word beneath the prefix") {
            exampleTrie().wordsWithPrefix("ca") shouldBe listOf("car", "cart", "cat")
        }

        test("includes the prefix itself when the prefix is a word") {
            exampleTrie().wordsWithPrefix("do") shouldBe listOf("do", "dog")
            exampleTrie().wordsWithPrefix("car") shouldBe listOf("car", "cart")
        }

        test("returns everything for the empty prefix") {
            exampleTrie().wordsWithPrefix("") shouldBe
                listOf("car", "cart", "cat", "do", "dog")
        }

        test("returns nothing for an absent prefix") {
            exampleTrie().wordsWithPrefix("z") shouldBe emptyList()
            exampleTrie().wordsWithPrefix("doge") shouldBe emptyList()
        }

        test("returns nothing on an empty trie") {
            Trie().wordsWithPrefix("a") shouldBe emptyList()
            Trie().wordsWithPrefix("") shouldBe emptyList()
        }

        test("collects every word in a chain of nested words") {
            // a node that both ends a word and has children must do both:
            // record itself and keep descending
            val trie = trieOf("a", "ab", "abc", "abcd", "abcde")
            trie.wordsWithPrefix("a") shouldBe listOf("a", "ab", "abc", "abcd", "abcde")
        }

        test("results are ascending regardless of insertion order") {
            val trie = trieOf("zebra", "apple", "mango", "banana", "apricot")
            trie.wordsWithPrefix("") shouldBe
                listOf("apple", "apricot", "banana", "mango", "zebra")
        }
    }

    context("words") {
        test("is empty for an empty trie") {
            Trie().words() shouldBe emptyList()
        }

        test("returns every stored word in ascending order") {
            trieOf("dog", "car", "cat", "do", "cart").words() shouldBe
                listOf("car", "cart", "cat", "do", "dog")
        }

        test("agrees with wordsWithPrefix on the empty prefix") {
            val trie = exampleTrie()
            trie.words() shouldBe trie.wordsWithPrefix("")
        }

        test("length always matches size") {
            val trie = exampleTrie()
            trie.words().size shouldBe trie.size()
            trie.insert("dot")
            trie.words().size shouldBe trie.size()
        }
    }

    context("size and isEmpty") {
        test("a fresh trie is empty") {
            val trie = Trie()
            trie.size() shouldBe 0
            trie.isEmpty() shouldBe true
        }

        test("counts distinct words, not nodes") {
            // "cart" adds four nodes but only one word
            val trie = trieOf("cart")
            trie.size() shouldBe 1
        }

        test("tracks a mixed sequence of inserts and duplicates") {
            val trie = Trie()
            trie.insert("cat")
            trie.size() shouldBe 1
            trie.insert("car")
            trie.size() shouldBe 2
            trie.insert("cat") // duplicate
            trie.size() shouldBe 2
            trie.insert("ca") // a prefix of both, but new as a word
            trie.size() shouldBe 3
        }
    }

    context("every stored word round-trips") {
        test("across many shapes, contains and startsWith both accept it") {
            // deterministic pseudo-random words, no test-run variance
            var seed = 987654

            fun next(bound: Int): Int {
                seed = (seed * 1103515245 + 12345) and 0x7FFFFFFF
                return seed % bound
            }
            val inserted = mutableListOf<String>()
            val trie = Trie()
            repeat(500) {
                val word = (0..next(6)).map { 'a' + next(4) }.joinToString("")
                trie.insert(word)
                inserted.add(word)
            }
            val distinct = inserted.distinct().sorted()

            trie.size() shouldBe distinct.size
            trie.words() shouldBe distinct
            for (word in distinct) {
                trie.contains(word) shouldBe true
                trie.startsWith(word) shouldBe true
            }
            // and prefix queries agree with filtering the same list
            for (prefix in listOf("", "a", "b", "ab", "abc", "zz")) {
                trie.wordsWithPrefix(prefix) shouldBe distinct.filter { it.startsWith(prefix) }
            }
        }
    }
})
