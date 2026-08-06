package hashing

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// The table starts at capacity 8 with a 0.75 load factor, so the 7th
// distinct key is the first insert that triggers a resize. Several tests
// below deliberately cross that line — a resize that silently stranded
// entries would still leave every earlier assertion passing.
private const val KEYS_BEFORE_FIRST_RESIZE = 6

class HashTableTest : FunSpec({

    context("put and get") {
        test("stores and retrieves a single pair") {
            val table = HashTable()
            table.put(1, 100)
            table.get(1) shouldBe 100
            table.size() shouldBe 1
        }

        test("stores several distinct keys independently") {
            val table = HashTable()
            table.put(1, 10)
            table.put(2, 20)
            table.put(3, 30)
            table.get(1) shouldBe 10
            table.get(2) shouldBe 20
            table.get(3) shouldBe 30
            table.size() shouldBe 3
        }

        test("putting an existing key updates it instead of adding a duplicate") {
            val table = HashTable()
            table.put(5, 1)
            table.put(5, 2)
            table.get(5) shouldBe 2
            table.size() shouldBe 1
        }

        test("repeated updates to one key never grow size") {
            val table = HashTable()
            repeat(20) { table.put(7, it) }
            table.size() shouldBe 1
            table.get(7) shouldBe 19
        }

        test("keys that collide in the same bucket stay independently retrievable") {
            // 5 and 13 both hash to bucket 5 at capacity 8 (5 % 8 == 13 % 8)
            val table = HashTable()
            table.put(5, 50)
            table.put(13, 130)
            table.get(5) shouldBe 50
            table.get(13) shouldBe 130
            table.size() shouldBe 2
        }

        test("negative keys map to a valid bucket and round-trip") {
            val table = HashTable()
            table.put(-1, -10)
            table.put(-3, -30)
            table.put(-1000, -10000)
            table.get(-1) shouldBe -10
            table.get(-3) shouldBe -30
            table.get(-1000) shouldBe -10000
        }

        test("zero works as both key and value") {
            val table = HashTable()
            table.put(0, 0)
            table.get(0) shouldBe 0
            table.containsKey(0) shouldBe true
            table.size() shouldBe 1
        }

        test("get throws for a key that was never inserted") {
            val table = HashTable()
            table.put(1, 10)
            shouldThrow<NoSuchElementException> { table.get(2) }
        }

        test("get throws on an empty table") {
            val table = HashTable()
            shouldThrow<NoSuchElementException> { table.get(1) }
        }
    }

    context("remove") {
        test("removes an existing key and returns its value") {
            val table = HashTable()
            table.put(1, 100)
            table.remove(1) shouldBe 100
            table.size() shouldBe 0
            table.containsKey(1) shouldBe false
        }

        test("a removed key is no longer retrievable") {
            val table = HashTable()
            table.put(1, 100)
            table.remove(1)
            shouldThrow<NoSuchElementException> { table.get(1) }
        }

        test("throws for a missing key") {
            val table = HashTable()
            shouldThrow<NoSuchElementException> { table.remove(99) }
        }

        test("a failed remove leaves size untouched") {
            val table = HashTable()
            table.put(1, 10)
            table.put(2, 20)
            shouldThrow<NoSuchElementException> { table.remove(99) }
            table.size() shouldBe 2
            table.get(1) shouldBe 10
            table.get(2) shouldBe 20
        }

        test("removing one of two colliding keys leaves the other intact") {
            // the chain-scan case: clearing the whole bucket would break this
            val table = HashTable()
            table.put(5, 50)
            table.put(13, 130)
            table.remove(5) shouldBe 50
            table.get(13) shouldBe 130
            table.containsKey(5) shouldBe false
            table.size() shouldBe 1
        }

        test("a key can be removed and re-added") {
            val table = HashTable()
            table.put(1, 10)
            table.remove(1)
            table.put(1, 999)
            table.get(1) shouldBe 999
            table.size() shouldBe 1
        }
    }

    context("containsKey") {
        test("true for a present key, false for an absent one") {
            val table = HashTable()
            table.put(1, 10)
            table.containsKey(1) shouldBe true
            table.containsKey(2) shouldBe false
        }

        test("false on an empty table") {
            HashTable().containsKey(1) shouldBe false
        }

        test("distinguishes colliding keys rather than matching on bucket alone") {
            val table = HashTable()
            table.put(5, 50)
            table.containsKey(13) shouldBe false // same bucket, different key
        }
    }

    context("isEmpty and size") {
        test("a fresh table is empty with size 0") {
            val table = HashTable()
            table.isEmpty() shouldBe true
            table.size() shouldBe 0
        }

        test("not empty after a put") {
            val table = HashTable()
            table.put(1, 10)
            table.isEmpty() shouldBe false
        }

        test("empty again once every entry is removed") {
            val table = HashTable()
            table.put(1, 10)
            table.put(2, 20)
            table.remove(1)
            table.remove(2)
            table.isEmpty() shouldBe true
            table.size() shouldBe 0
        }

        test("size tracks a mixed sequence of puts, updates and removes") {
            val table = HashTable()
            table.put(1, 10)
            table.put(2, 20)
            table.size() shouldBe 2
            table.put(1, 11) // update, not a new entry
            table.size() shouldBe 2
            table.remove(2)
            table.size() shouldBe 1
            table.put(3, 30)
            table.size() shouldBe 2
        }
    }

    context("resizing") {
        test("every entry survives the first resize") {
            val table = HashTable()
            val count = KEYS_BEFORE_FIRST_RESIZE + 4 // comfortably past the threshold
            for (key in 0 until count) {
                table.put(key, key * 10)
            }
            table.size() shouldBe count
            for (key in 0 until count) {
                table.get(key) shouldBe key * 10
            }
        }

        test("entries survive repeated resizes") {
            // 200 entries forces capacity 8 -> 16 -> ... -> 512, several rehashes
            val table = HashTable()
            for (key in 0 until 200) {
                table.put(key, key * 3)
            }
            table.size() shouldBe 200
            for (key in 0 until 200) {
                table.get(key) shouldBe key * 3
            }
        }

        test("keys that collided before a resize are still both retrievable after") {
            // 5 and 13 share a bucket at capacity 8; at capacity 16 they split
            // apart (5 % 16 == 5, 13 % 16 == 13). Both must survive the move.
            val table = HashTable()
            table.put(5, 50)
            table.put(13, 130)
            for (key in 100 until 110) {
                table.put(key, key)
            }
            table.get(5) shouldBe 50
            table.get(13) shouldBe 130
        }

        test("negative keys still resolve after rehashing") {
            val table = HashTable()
            table.put(-7, -70)
            table.put(-42, -420)
            for (key in 0 until 30) {
                table.put(key, key)
            }
            table.get(-7) shouldBe -70
            table.get(-42) shouldBe -420
        }

        test("resizing does not duplicate entries or inflate size") {
            val table = HashTable()
            for (key in 0 until 50) {
                table.put(key, key)
            }
            table.size() shouldBe 50
            // if a rehash had duplicated entries, some of these would fail
            for (key in 0 until 50) {
                table.remove(key) shouldBe key
            }
            table.isEmpty() shouldBe true
            table.size() shouldBe 0
        }

        test("updates after a resize still overwrite rather than duplicate") {
            val table = HashTable()
            for (key in 0 until 40) {
                table.put(key, key)
            }
            table.put(7, 777)
            table.get(7) shouldBe 777
            table.size() shouldBe 40
        }

        test("remove and re-add work correctly across a resize boundary") {
            val table = HashTable()
            for (key in 0 until 40) {
                table.put(key, key)
            }
            for (key in 0 until 20) {
                table.remove(key)
            }
            table.size() shouldBe 20
            for (key in 0 until 20) {
                table.containsKey(key) shouldBe false
            }
            for (key in 20 until 40) {
                table.get(key) shouldBe key
            }
            for (key in 0 until 20) {
                table.put(key, key * 100)
            }
            table.size() shouldBe 40
            for (key in 0 until 20) {
                table.get(key) shouldBe key * 100
            }
        }
    }
})
