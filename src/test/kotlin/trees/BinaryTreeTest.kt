package trees

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import trees.BinaryTree.Node

// Shapes matter more than values for tree code, so the fixtures below
// are named by shape and reused across contexts. The awkward ones —
// spines, and a one-child node sitting above a fork — are where the
// interesting edge cases live.

//       1
//     /  \
//   2     3
//  / \     \
// 4   5     6
private fun exampleTree() = BinaryTree(Node(1, Node(2, Node(4), Node(5)), Node(3, null, Node(6))))

//       1
//     /  \
//   2     3
//  / \   / \
// 4   5 6   7
private fun perfectTree() = BinaryTree(Node(1, Node(2, Node(4), Node(5)), Node(3, Node(6), Node(7))))

// 1 -> 2 -> 3 -> 4, each as a right child
private fun rightSpine() = BinaryTree(Node(1, null, Node(2, null, Node(3, null, Node(4)))))

// 1 -> 2 -> 3, each as a left child
private fun leftSpine() = BinaryTree(Node(1, Node(2, Node(3))))

//    1
//   /
//  2
// / \
// 3  4      one-child node above a fork
private fun oneChildOverFork() = BinaryTree(Node(1, Node(2, Node(3), Node(4))))

class BinaryTreeTest : FunSpec({

    context("preOrder") {
        test("visits node before its subtrees") {
            exampleTree().preOrder() shouldBe listOf(1, 2, 4, 5, 3, 6)
        }

        test("empty tree yields an empty list") {
            BinaryTree(null).preOrder() shouldBe emptyList()
        }

        test("single node yields one value") {
            BinaryTree(Node(7)).preOrder() shouldBe listOf(7)
        }

        test("handles a node with only one child") {
            oneChildOverFork().preOrder() shouldBe listOf(1, 2, 3, 4)
        }
    }

    context("inOrder") {
        test("visits left subtree, node, then right subtree") {
            exampleTree().inOrder() shouldBe listOf(4, 2, 5, 1, 3, 6)
        }

        test("empty tree yields an empty list") {
            BinaryTree(null).inOrder() shouldBe emptyList()
        }

        test("single node yields one value") {
            BinaryTree(Node(7)).inOrder() shouldBe listOf(7)
        }

        test("on a right spine it matches pre-order, since there is never a left subtree") {
            rightSpine().inOrder() shouldBe listOf(1, 2, 3, 4)
            rightSpine().inOrder() shouldBe rightSpine().preOrder()
        }

        test("on a left spine it reverses, since the node always comes after its subtree") {
            leftSpine().inOrder() shouldBe listOf(3, 2, 1)
        }
    }

    context("postOrder") {
        test("visits both subtrees before the node") {
            exampleTree().postOrder() shouldBe listOf(4, 5, 2, 6, 3, 1)
        }

        test("empty tree yields an empty list") {
            BinaryTree(null).postOrder() shouldBe emptyList()
        }

        test("single node yields one value") {
            BinaryTree(Node(7)).postOrder() shouldBe listOf(7)
        }

        test("the root is always last") {
            perfectTree().postOrder().last() shouldBe 1
            rightSpine().postOrder().last() shouldBe 1
        }
    }

    context("the three DFS traversals together") {
        test("all visit exactly the same nodes, differing only in order") {
            val tree = exampleTree()
            tree.preOrder().sorted() shouldBe tree.inOrder().sorted()
            tree.inOrder().sorted() shouldBe tree.postOrder().sorted()
        }

        test("pre-order starts at the root and post-order ends at it") {
            val tree = exampleTree()
            tree.preOrder().first() shouldBe 1
            tree.postOrder().last() shouldBe 1
        }
    }

    context("levelOrder") {
        test("groups values by depth") {
            exampleTree().levelOrder() shouldBe listOf(listOf(1), listOf(2, 3), listOf(4, 5, 6))
        }

        test("empty tree yields an empty list, not a list containing an empty level") {
            BinaryTree(null).levelOrder() shouldBe emptyList()
        }

        test("single node yields one level holding one value") {
            BinaryTree(Node(7)).levelOrder() shouldBe listOf(listOf(7))
        }

        test("a perfect tree's levels double in width") {
            perfectTree().levelOrder() shouldBe
                listOf(listOf(1), listOf(2, 3), listOf(4, 5, 6, 7))
        }

        test("a spine puts exactly one node on every level") {
            rightSpine().levelOrder() shouldBe
                listOf(listOf(1), listOf(2), listOf(3), listOf(4))
        }

        test("a ragged level draws from different parents") {
            // 2's right child and 3's left child share a level
            val ragged = BinaryTree(Node(1, Node(2, null, Node(5)), Node(3, Node(6), null)))
            ragged.levelOrder() shouldBe listOf(listOf(1), listOf(2, 3), listOf(5, 6))
        }

        test("level count equals height plus one") {
            exampleTree().levelOrder().size shouldBe exampleTree().height() + 1
            rightSpine().levelOrder().size shouldBe rightSpine().height() + 1
        }

        test("flattening it visits every node exactly once") {
            val tree = exampleTree()
            tree.levelOrder().flatten().sorted() shouldBe tree.preOrder().sorted()
        }
    }

    context("height") {
        test("empty tree is -1") {
            BinaryTree(null).height() shouldBe -1
        }

        test("single node is 0, since height counts edges not nodes") {
            BinaryTree(Node(7)).height() shouldBe 0
        }

        test("example tree is 2") {
            exampleTree().height() shouldBe 2
        }

        test("a degenerate tree's height is one less than its size") {
            rightSpine().height() shouldBe 3
            rightSpine().height() shouldBe rightSpine().size() - 1
        }

        test("takes the deeper subtree, not simply the left one") {
            // deeper on the left
            BinaryTree(Node(1, Node(2, Node(3, Node(4))), Node(5))).height() shouldBe 3
            // mirrored, deeper on the right
            BinaryTree(Node(1, Node(5), Node(2, null, Node(3, null, Node(4))))).height() shouldBe 3
        }
    }

    context("size") {
        test("empty tree is 0") {
            BinaryTree(null).size() shouldBe 0
        }

        test("counts every node") {
            exampleTree().size() shouldBe 6
            perfectTree().size() shouldBe 7
            rightSpine().size() shouldBe 4
        }

        test("counts duplicate values as separate nodes") {
            BinaryTree(Node(5, Node(5), Node(5))).size() shouldBe 3
        }

        test("agrees with the length of every traversal") {
            val tree = exampleTree()
            tree.size() shouldBe tree.preOrder().size
            tree.size() shouldBe tree.inOrder().size
            tree.size() shouldBe tree.postOrder().size
            tree.size() shouldBe tree.levelOrder().flatten().size
        }
    }

    context("countLeaves") {
        test("empty tree is 0") {
            BinaryTree(null).countLeaves() shouldBe 0
        }

        test("a single node is itself a leaf") {
            BinaryTree(Node(7)).countLeaves() shouldBe 1
        }

        test("example tree has 3 leaves") {
            exampleTree().countLeaves() shouldBe 3
        }

        test("a node with one child is not a leaf, and its subtree still counts") {
            oneChildOverFork().countLeaves() shouldBe 2
        }

        test("a right-only node above a fork also counts its subtree") {
            BinaryTree(Node(3, null, Node(6, Node(7), Node(8)))).countLeaves() shouldBe 2
        }

        test("several one-child nodes above forks all descend correctly") {
            val deep =
                BinaryTree(
                    Node(
                        1,
                        Node(2, null, Node(3, Node(4), Node(5))),
                        Node(6, Node(7, Node(8), Node(9)), null),
                    ),
                )
            deep.countLeaves() shouldBe 4
        }

        test("a spine has exactly one leaf however long it is") {
            rightSpine().countLeaves() shouldBe 1
            leftSpine().countLeaves() shouldBe 1
        }

        test("a perfect tree's leaves are its whole bottom level") {
            perfectTree().countLeaves() shouldBe 4
            perfectTree().countLeaves() shouldBe perfectTree().levelOrder().last().size
        }
    }

    context("contains") {
        test("finds every value in the tree") {
            val tree = exampleTree()
            for (value in listOf(1, 2, 3, 4, 5, 6)) {
                tree.contains(value) shouldBe true
            }
        }

        test("rejects values that are absent") {
            val tree = exampleTree()
            tree.contains(0) shouldBe false
            tree.contains(7) shouldBe false
            tree.contains(-1) shouldBe false
        }

        test("empty tree contains nothing, and does not throw") {
            BinaryTree(null).contains(1) shouldBe false
        }

        test("finds the root without descending") {
            BinaryTree(Node(7)).contains(7) shouldBe true
        }

        test("reaches the deepest node on either spine") {
            rightSpine().contains(4) shouldBe true
            leftSpine().contains(3) shouldBe true
        }

        test("handles zero and negative values") {
            val tree = BinaryTree(Node(0, Node(-5), Node(-10)))
            tree.contains(0) shouldBe true
            tree.contains(-5) shouldBe true
            tree.contains(-10) shouldBe true
            tree.contains(-6) shouldBe false
        }

        test("agrees with traversal membership on every shape") {
            for (tree in listOf(exampleTree(), perfectTree(), rightSpine(), oneChildOverFork())) {
                for (value in tree.preOrder()) {
                    tree.contains(value) shouldBe true
                }
                tree.contains(1000) shouldBe false
            }
        }
    }
})
