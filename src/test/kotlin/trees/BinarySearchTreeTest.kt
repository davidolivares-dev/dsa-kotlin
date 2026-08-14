package trees

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// Most assertions here go through inOrder(). On a BST it is the natural
// oracle: if the ordering rule is intact the output is sorted, and if a
// delete rewired something wrongly it shows up immediately as either a
// wrong order or a missing/duplicated value.

private fun bstOf(vararg values: Int): BinarySearchTree {
    val tree = BinarySearchTree()
    values.forEach { tree.insert(it) }
    return tree
}

//       50
//     /    \
//   30      70
//  /  \    /  \
// 20   40 60   80
private fun exampleTree() = bstOf(50, 30, 70, 20, 40, 60, 80)

class BinarySearchTreeTest : FunSpec({

    context("insert") {
        test("stores a single value") {
            val tree = bstOf(42)
            tree.inOrder() shouldBe listOf(42)
            tree.size() shouldBe 1
        }

        test("orders values regardless of the order they arrived in") {
            val ascending = bstOf(20, 30, 40, 50, 60, 70, 80)
            val descending = bstOf(80, 70, 60, 50, 40, 30, 20)
            val scattered = bstOf(50, 30, 70, 20, 40, 60, 80)
            val expected = listOf(20, 30, 40, 50, 60, 70, 80)
            ascending.inOrder() shouldBe expected
            descending.inOrder() shouldBe expected
            scattered.inOrder() shouldBe expected
        }

        test("rejects duplicates without growing the tree") {
            val tree = bstOf(50, 30, 70, 50, 30, 70, 50)
            tree.inOrder() shouldBe listOf(30, 50, 70)
            tree.size() shouldBe 3
        }

        test("a rejected duplicate adds no depth") {
            val tree = bstOf(50, 30, 70)
            val before = tree.height()
            tree.insert(50)
            tree.height() shouldBe before
        }

        test("handles zero and negative values") {
            bstOf(0, -5, 5, -100, 100).inOrder() shouldBe listOf(-100, -5, 0, 5, 100)
        }
    }

    context("contains") {
        test("finds every value in the tree") {
            val tree = exampleTree()
            for (value in listOf(20, 30, 40, 50, 60, 70, 80)) {
                tree.contains(value) shouldBe true
            }
        }

        test("rejects values that fall between existing ones") {
            // these descend a full path before failing, unlike an
            // out-of-range value that fails near the root
            val tree = exampleTree()
            for (value in listOf(25, 35, 45, 55, 65, 75)) {
                tree.contains(value) shouldBe false
            }
        }

        test("rejects values outside the tree's range") {
            val tree = exampleTree()
            tree.contains(10) shouldBe false
            tree.contains(90) shouldBe false
        }

        test("is false on an empty tree without throwing") {
            BinarySearchTree().contains(1) shouldBe false
        }

        test("reaches the deepest node of a degenerate tree") {
            bstOf(1, 2, 3, 4, 5).contains(5) shouldBe true
            bstOf(5, 4, 3, 2, 1).contains(1) shouldBe true
        }
    }

    context("delete — case 1, no left child") {
        test("removes a leaf") {
            val tree = exampleTree()
            tree.delete(20)
            tree.inOrder() shouldBe listOf(30, 40, 50, 60, 70, 80)
            tree.size() shouldBe 6
            tree.contains(20) shouldBe false
        }

        test("promotes the right child when there is no left child") {
            val tree = bstOf(50, 30, 70, 40)
            tree.delete(30)
            tree.inOrder() shouldBe listOf(40, 50, 70)
            tree.size() shouldBe 3
        }
    }

    context("delete — case 2, no right child") {
        test("promotes the left child when there is no right child") {
            val tree = bstOf(50, 30, 70, 20)
            tree.delete(30)
            tree.inOrder() shouldBe listOf(20, 50, 70)
            tree.size() shouldBe 3
        }

        test("handles a left-only chain") {
            val tree = bstOf(50, 40, 30, 20)
            tree.delete(40)
            tree.inOrder() shouldBe listOf(20, 30, 50)
            tree.size() shouldBe 3
        }
    }

    context("delete — case 3, two children") {
        test("replaces the value with its in-order successor") {
            val tree = exampleTree()
            tree.delete(30)
            tree.inOrder() shouldBe listOf(20, 40, 50, 60, 70, 80)
            tree.size() shouldBe 6
        }

        test("works when the successor is deeper than one step") {
            // right subtree of 50 is rooted at 70; the successor is 60,
            // which itself has a right child that must be promoted
            val tree = bstOf(50, 30, 70, 60, 80, 65)
            tree.delete(50)
            tree.inOrder() shouldBe listOf(30, 60, 65, 70, 80)
            tree.size() shouldBe 5
        }

        test("does not leave the successor behind as a duplicate") {
            val tree = exampleTree()
            tree.delete(50)
            tree.inOrder() shouldBe listOf(20, 30, 40, 60, 70, 80)
            tree.inOrder().count { it == 60 } shouldBe 1
        }
    }

    context("delete — the root") {
        test("removes a root that is also the only node") {
            val tree = bstOf(7)
            tree.delete(7)
            tree.isEmpty() shouldBe true
            tree.size() shouldBe 0
            tree.height() shouldBe -1
            tree.inOrder() shouldBe emptyList()
            tree.contains(7) shouldBe false
        }

        test("removes a root with two children") {
            val tree = exampleTree()
            tree.delete(50)
            tree.inOrder() shouldBe listOf(20, 30, 40, 60, 70, 80)
            tree.size() shouldBe 6
        }

        test("removes a root with only one child") {
            val tree = bstOf(50, 30)
            tree.delete(50)
            tree.inOrder() shouldBe listOf(30)
            tree.size() shouldBe 1
        }
    }

    context("delete — no-ops and repeats") {
        test("deleting an absent value changes nothing") {
            val tree = exampleTree()
            tree.delete(999)
            tree.inOrder() shouldBe listOf(20, 30, 40, 50, 60, 70, 80)
            tree.size() shouldBe 7
        }

        test("deleting from an empty tree does not throw") {
            val tree = BinarySearchTree()
            tree.delete(1)
            tree.size() shouldBe 0
            tree.isEmpty() shouldBe true
        }

        test("deleting the same value twice only removes it once") {
            val tree = exampleTree()
            tree.delete(30)
            tree.delete(30)
            tree.size() shouldBe 6
        }

        test("a value can be deleted and reinserted") {
            val tree = exampleTree()
            tree.delete(50)
            tree.insert(50)
            tree.inOrder() shouldBe listOf(20, 30, 40, 50, 60, 70, 80)
            tree.size() shouldBe 7
        }
    }

    context("delete — draining the tree") {
        test("removing every value in ascending order empties it") {
            val tree = exampleTree()
            for ((removed, value) in listOf(20, 30, 40, 50, 60, 70, 80).withIndex()) {
                tree.delete(value)
                tree.size() shouldBe 6 - removed
                tree.inOrder() shouldBe tree.inOrder().sorted()
            }
            tree.isEmpty() shouldBe true
        }

        test("removing every value in descending order empties it") {
            val tree = exampleTree()
            for ((removed, value) in listOf(80, 70, 60, 50, 40, 30, 20).withIndex()) {
                tree.delete(value)
                tree.size() shouldBe 6 - removed
                tree.inOrder() shouldBe tree.inOrder().sorted()
            }
            tree.isEmpty() shouldBe true
        }

        test("removing roots repeatedly empties it") {
            // each delete targets whatever is currently at the top,
            // so case 3 fires over and over
            val tree = exampleTree()
            repeat(7) {
                tree.delete(tree.inOrder()[tree.size() / 2])
                tree.inOrder() shouldBe tree.inOrder().sorted()
            }
            tree.isEmpty() shouldBe true
        }
    }

    context("min and max") {
        test("report the extremes of the example tree") {
            exampleTree().min() shouldBe 20
            exampleTree().max() shouldBe 80
        }

        test("a single node is both the min and the max") {
            bstOf(7).min() shouldBe 7
            bstOf(7).max() shouldBe 7
        }

        test("handle degenerate trees in both directions") {
            bstOf(1, 2, 3, 4, 5).min() shouldBe 1
            bstOf(1, 2, 3, 4, 5).max() shouldBe 5
            bstOf(5, 4, 3, 2, 1).min() shouldBe 1
            bstOf(5, 4, 3, 2, 1).max() shouldBe 5
        }

        test("both throw on an empty tree") {
            shouldThrow<NoSuchElementException> { BinarySearchTree().min() }
            shouldThrow<NoSuchElementException> { BinarySearchTree().max() }
        }

        test("track the extremes as values are deleted") {
            val tree = exampleTree()
            tree.delete(20)
            tree.min() shouldBe 30
            tree.delete(80)
            tree.max() shouldBe 70
        }
    }

    context("inOrder") {
        test("is empty for an empty tree") {
            BinarySearchTree().inOrder() shouldBe emptyList()
        }

        test("is sorted even for a degenerate tree") {
            bstOf(1, 2, 3, 4, 5).inOrder() shouldBe listOf(1, 2, 3, 4, 5)
            bstOf(5, 4, 3, 2, 1).inOrder() shouldBe listOf(1, 2, 3, 4, 5)
        }
    }

    context("height") {
        test("is -1 for an empty tree and 0 for a single node") {
            BinarySearchTree().height() shouldBe -1
            bstOf(7).height() shouldBe 0
        }

        test("is 2 for the balanced example tree") {
            exampleTree().height() shouldBe 2
        }

        test("equals size minus one for a degenerate tree") {
            val ascending = bstOf(1, 2, 3, 4, 5)
            ascending.height() shouldBe ascending.size() - 1
            val descending = bstOf(5, 4, 3, 2, 1)
            descending.height() shouldBe descending.size() - 1
        }

        test("takes the deeper subtree, not a fixed side") {
            bstOf(50, 30, 70, 20, 10, 5).height() shouldBe 4
            bstOf(50, 30, 70, 80, 90, 95).height() shouldBe 4
        }
    }

    context("size and isEmpty") {
        test("a fresh tree is empty") {
            val tree = BinarySearchTree()
            tree.isEmpty() shouldBe true
            tree.size() shouldBe 0
        }

        test("track a mixed sequence of inserts, duplicates and deletes") {
            val tree = BinarySearchTree()
            tree.insert(50)
            tree.insert(30)
            tree.size() shouldBe 2
            tree.insert(50) // duplicate
            tree.size() shouldBe 2
            tree.delete(30)
            tree.size() shouldBe 1
            tree.delete(999) // absent
            tree.size() shouldBe 1
            tree.delete(50)
            tree.isEmpty() shouldBe true
        }

        test("size always matches the traversal length") {
            val tree = exampleTree()
            tree.size() shouldBe tree.inOrder().size
            tree.delete(50)
            tree.size() shouldBe tree.inOrder().size
            tree.delete(20)
            tree.size() shouldBe tree.inOrder().size
        }
    }

    context("the ordering invariant survives heavy mutation") {
        test("interleaved inserts and deletes keep the tree sorted and counted") {
            val tree = BinarySearchTree()
            val present = sortedSetOf<Int>()
            // deterministic pseudo-random sequence, no test-run variance
            var seed = 12345

            fun next(bound: Int): Int {
                seed = (seed * 1103515245 + 12345) and 0x7FFFFFFF
                return seed % bound
            }
            repeat(600) {
                val value = next(80) - 40
                if (next(2) == 0) {
                    tree.insert(value)
                    present.add(value)
                } else {
                    tree.delete(value)
                    present.remove(value)
                }
                tree.size() shouldBe present.size
            }
            tree.inOrder() shouldBe present.toList()
            for (value in -40 until 40) {
                tree.contains(value) shouldBe present.contains(value)
            }
        }
    }
})
