package hashing

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// The set starts at capacity 8 with a 0.75 load factor, so the 7th
// distinct value is the first add that triggers a resize. Several tests
// deliberately cross that line — a rehash that stranded or duplicated
// values would leave every smaller test still passing.
private const val VALUES_BEFORE_FIRST_RESIZE = 6

// A set promises no ordering: bucket order is an implementation detail
// that shifts on every resize. Compare sorted, never raw.
private fun HashSet.sorted(): List<Int> = toList().sorted()

private fun setOf(vararg values: Int): HashSet {
    val set = HashSet()
    values.forEach { set.add(it) }
    return set
}

class HashSetTest : FunSpec({

    context("add") {
        test("adding a new value returns true and stores it") {
            val set = HashSet()
            set.add(1) shouldBe true
            set.contains(1) shouldBe true
            set.size() shouldBe 1
        }

        test("adding a duplicate returns false and changes nothing") {
            val set = HashSet()
            set.add(1)
            set.add(1) shouldBe false
            set.size() shouldBe 1
            set.sorted() shouldBe listOf(1)
        }

        test("repeated adds of one value never grow the set") {
            val set = HashSet()
            repeat(20) { set.add(7) }
            set.size() shouldBe 1
            set.sorted() shouldBe listOf(7)
        }

        test("values that collide are both stored and both found") {
            // 5 and 13 hash to the same bucket at capacity 8
            val set = setOf(5, 13)
            set.contains(5) shouldBe true
            set.contains(13) shouldBe true
            set.size() shouldBe 2
        }

        test("negative values are stored and retrieved unchanged") {
            val set = setOf(-1, -3, -1000)
            set.contains(-1) shouldBe true
            set.contains(-3) shouldBe true
            set.contains(-1000) shouldBe true
            set.sorted() shouldBe listOf(-1000, -3, -1)
        }

        test("adding a negative value does not make its positive appear") {
            // guards the resize bug where a value was normalised on store
            val set = setOf(-3)
            set.contains(3) shouldBe false
        }

        test("zero is an ordinary value") {
            val set = setOf(0)
            set.contains(0) shouldBe true
            set.isEmpty() shouldBe false
            set.size() shouldBe 1
        }
    }

    context("contains") {
        test("false on an empty set") {
            HashSet().contains(1) shouldBe false
        }

        test("false for a value never added") {
            setOf(1, 2, 3).contains(99) shouldBe false
        }

        test("does not match on bucket occupancy alone") {
            // 21 shares a bucket with 5 at capacity 8 but was never added
            val set = setOf(5)
            set.contains(21) shouldBe false
        }
    }

    context("remove") {
        test("removing a present value returns true and drops the size") {
            val set = setOf(1, 2)
            set.remove(1) shouldBe true
            set.size() shouldBe 1
            set.contains(1) shouldBe false
        }

        test("removing an absent value returns false and leaves size alone") {
            val set = setOf(1, 2)
            set.remove(99) shouldBe false
            set.size() shouldBe 2
            set.sorted() shouldBe listOf(1, 2)
        }

        test("removing from an empty set returns false without throwing") {
            HashSet().remove(1) shouldBe false
        }

        test("removing one of two colliding values leaves the other") {
            val set = setOf(5, 13)
            set.remove(5) shouldBe true
            set.contains(13) shouldBe true
            set.size() shouldBe 1
        }

        test("a removed value can be added again as new") {
            val set = setOf(7)
            set.remove(7)
            set.add(7) shouldBe true
            set.size() shouldBe 1
        }

        test("removing the same value twice returns false the second time") {
            val set = setOf(7)
            set.remove(7) shouldBe true
            set.remove(7) shouldBe false
        }
    }

    context("isEmpty and size") {
        test("a fresh set is empty") {
            val set = HashSet()
            set.isEmpty() shouldBe true
            set.size() shouldBe 0
        }

        test("empty again once every value is removed") {
            val set = setOf(1, 2, 3)
            set.remove(1)
            set.remove(2)
            set.remove(3)
            set.isEmpty() shouldBe true
            set.size() shouldBe 0
        }

        test("size tracks a mixed sequence of adds, duplicates and removes") {
            val set = HashSet()
            set.add(1)
            set.add(2)
            set.size() shouldBe 2
            set.add(1) // duplicate
            set.size() shouldBe 2
            set.remove(2)
            set.size() shouldBe 1
            set.add(3)
            set.size() shouldBe 2
        }
    }

    context("toList") {
        test("empty set yields an empty list") {
            HashSet().toList() shouldBe emptyList()
        }

        test("yields every value exactly once, including collided ones") {
            val set = setOf(5, 13, -3, 0, 99)
            set.sorted() shouldBe listOf(-3, 0, 5, 13, 99)
        }

        test("duplicates never appear, however many times they were added") {
            val set = HashSet()
            repeat(5) { set.add(7) }
            set.toList() shouldBe listOf(7)
        }

        test("length always agrees with size()") {
            val set = setOf(1, 2, 3, 4, 5)
            set.toList().size shouldBe set.size()
            set.remove(3)
            set.toList().size shouldBe set.size()
        }
    }

    context("resizing") {
        test("every value survives the first resize") {
            val set = HashSet()
            val count = VALUES_BEFORE_FIRST_RESIZE + 4 // comfortably past it
            for (value in 0 until count) {
                set.add(value)
            }
            set.size() shouldBe count
            set.sorted() shouldBe (0 until count).toList()
        }

        test("values survive repeated resizes") {
            // 200 values force capacity 8 -> 16 -> ... -> 512
            val set = HashSet()
            for (value in 0 until 200) {
                set.add(value)
            }
            set.size() shouldBe 200
            set.sorted() shouldBe (0 until 200).toList()
        }

        test("negative values survive rehashing unchanged") {
            // the resize bug: normalising on store turned -3 into 3
            val set = HashSet()
            set.add(-3)
            set.add(-1000)
            for (value in 100 until 110) {
                set.add(value)
            }
            set.contains(-3) shouldBe true
            set.contains(-1000) shouldBe true
            set.contains(3) shouldBe false
            set.contains(1000) shouldBe false
        }

        test("values that collided before a resize both survive being split") {
            // 5 and 13 share a bucket at capacity 8, separate at 16
            val set = setOf(5, 13)
            for (value in 200 until 210) {
                set.add(value)
            }
            set.contains(5) shouldBe true
            set.contains(13) shouldBe true
        }

        test("resizing introduces no duplicates") {
            val set = HashSet()
            for (value in 0 until 50) {
                set.add(value)
            }
            set.toList().size shouldBe 50
            // a duplicated entry would let the second removal succeed
            for (value in 0 until 50) {
                set.remove(value) shouldBe true
            }
            set.isEmpty() shouldBe true
            for (value in 0 until 50) {
                set.remove(value) shouldBe false
            }
        }

        test("duplicate rejection still works after a resize") {
            val set = HashSet()
            for (value in 0 until 60) {
                set.add(value)
            }
            set.add(30) shouldBe false
            set.size() shouldBe 60
            set.add(9999) shouldBe true
            set.size() shouldBe 61
        }
    }

    context("union") {
        test("contains everything from either set, overlap included once") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.union(b).sorted() shouldBe listOf(1, 2, 3, 4, 5)
        }

        test("is symmetric") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.union(b).sorted() shouldBe b.union(a).sorted()
        }

        test("with an empty set returns the original values") {
            val a = setOf(1, 2, 3)
            a.union(HashSet()).sorted() shouldBe listOf(1, 2, 3)
            HashSet().union(a).sorted() shouldBe listOf(1, 2, 3)
        }

        test("of disjoint sets contains both entirely") {
            setOf(1, 2).union(setOf(90, 91)).sorted() shouldBe listOf(1, 2, 90, 91)
        }

        test("with itself changes nothing") {
            val a = setOf(1, 2, 3)
            a.union(a).sorted() shouldBe listOf(1, 2, 3)
        }

        test("leaves both operands unmodified") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.union(b)
            a.sorted() shouldBe listOf(1, 2, 3, 4)
            b.sorted() shouldBe listOf(3, 4, 5)
        }
    }

    context("intersection") {
        test("contains only values present in both") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.intersection(b).sorted() shouldBe listOf(3, 4)
        }

        test("is symmetric") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.intersection(b).sorted() shouldBe b.intersection(a).sorted()
        }

        test("with an empty set is empty") {
            val a = setOf(1, 2, 3)
            a.intersection(HashSet()).toList() shouldBe emptyList()
            HashSet().intersection(a).toList() shouldBe emptyList()
        }

        test("of disjoint sets is empty") {
            setOf(1, 2).intersection(setOf(90, 91)).toList() shouldBe emptyList()
        }

        test("with itself returns everything") {
            val a = setOf(1, 2, 3)
            a.intersection(a).sorted() shouldBe listOf(1, 2, 3)
        }

        test("leaves both operands unmodified") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.intersection(b)
            a.sorted() shouldBe listOf(1, 2, 3, 4)
            b.sorted() shouldBe listOf(3, 4, 5)
        }
    }

    context("difference") {
        test("contains values in this set but not the other") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.difference(b).sorted() shouldBe listOf(1, 2)
        }

        test("is NOT symmetric") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.difference(b).sorted() shouldBe listOf(1, 2)
            b.difference(a).sorted() shouldBe listOf(5)
        }

        test("minus an empty set returns the original values") {
            setOf(1, 2, 3).difference(HashSet()).sorted() shouldBe listOf(1, 2, 3)
        }

        test("an empty set minus anything is empty") {
            HashSet().difference(setOf(1, 2, 3)).toList() shouldBe emptyList()
        }

        test("minus itself is empty") {
            val a = setOf(1, 2, 3)
            a.difference(a).toList() shouldBe emptyList()
        }

        test("minus a disjoint set returns the original values") {
            setOf(1, 2).difference(setOf(90, 91)).sorted() shouldBe listOf(1, 2)
        }

        test("leaves both operands unmodified") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.difference(b)
            a.sorted() shouldBe listOf(1, 2, 3, 4)
            b.sorted() shouldBe listOf(3, 4, 5)
        }
    }

    context("set operations together") {
        test("(a - b) union (a intersect b) reconstructs a") {
            // cross-checks all three against each other: a bug in any one
            // of them breaks this identity
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.difference(b).union(a.intersection(b)).sorted() shouldBe a.sorted()
        }

        test("union minus intersection leaves values unique to one side") {
            val a = setOf(1, 2, 3, 4)
            val b = setOf(3, 4, 5)
            a.union(b).difference(a.intersection(b)).sorted() shouldBe listOf(1, 2, 5)
        }

        test("hold on large sets with negatives, across several resizes") {
            val x = HashSet()
            (-60..20).forEach { x.add(it) }
            val y = HashSet()
            (0..60).forEach { y.add(it) }
            x.union(y).sorted() shouldBe (-60..60).toList()
            x.intersection(y).sorted() shouldBe (0..20).toList()
            x.difference(y).sorted() shouldBe (-60..-1).toList()
            y.difference(x).sorted() shouldBe (21..60).toList()
        }
    }
})
