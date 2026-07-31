package linkedlist

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SinglyLinkedListTest : FunSpec({

    fun linkedListOf(vararg values: Int): SinglyLinkedList {
        val list = SinglyLinkedList()
        for (value in values) list.addLast(value)
        return list
    }

    context("addFirst") {
        test("inserting into an empty list sets a single-element list") {
            val list = SinglyLinkedList()
            list.addFirst(1)
            list.toList() shouldBe listOf(1)
        }

        test("each insert becomes the new head") {
            val list = SinglyLinkedList()
            list.addFirst(1)
            list.addFirst(2)
            list.addFirst(3)
            list.toList() shouldBe listOf(3, 2, 1)
        }
    }

    context("addLast") {
        test("inserting into an empty list sets a single-element list") {
            val list = SinglyLinkedList()
            list.addLast(1)
            list.toList() shouldBe listOf(1)
        }

        test("each insert becomes the new tail") {
            val list = SinglyLinkedList()
            list.addLast(1)
            list.addLast(2)
            list.addLast(3)
            list.toList() shouldBe listOf(1, 2, 3)
        }
    }

    context("deleteFirst") {
        test("throws on an empty list") {
            val list = SinglyLinkedList()
            shouldThrow<NoSuchElementException> { list.deleteFirst() }
        }

        test("removes and returns the head's value") {
            val list = linkedListOf(1, 2, 3)
            list.deleteFirst() shouldBe 1
            list.toList() shouldBe listOf(2, 3)
        }

        test("draining a list one node at a time empties it") {
            val list = linkedListOf(1, 2)
            list.deleteFirst()
            list.deleteFirst()
            list.toList() shouldBe emptyList()
            shouldThrow<NoSuchElementException> { list.deleteFirst() }
        }
    }

    context("deleteValue") {
        test("returns false on an empty list") {
            val list = SinglyLinkedList()
            list.deleteValue(1) shouldBe false
        }

        test("returns false when the value isn't present") {
            val list = linkedListOf(1, 2, 3)
            list.deleteValue(9) shouldBe false
            list.toList() shouldBe listOf(1, 2, 3)
        }

        test("removes the head when it holds the target value") {
            val list = linkedListOf(1, 2, 3)
            list.deleteValue(1) shouldBe true
            list.toList() shouldBe listOf(2, 3)
        }

        test("removes a middle node, relinking around it") {
            val list = linkedListOf(1, 2, 3)
            list.deleteValue(2) shouldBe true
            list.toList() shouldBe listOf(1, 3)
        }

        test("removes the tail node") {
            val list = linkedListOf(1, 2, 3)
            list.deleteValue(3) shouldBe true
            list.toList() shouldBe listOf(1, 2)
        }

        test("removes only the first occurrence of a duplicated value") {
            val list = linkedListOf(1, 2, 2, 3)
            list.deleteValue(2) shouldBe true
            list.toList() shouldBe listOf(1, 2, 3)
        }

        test("removing a single-node list's only value empties it") {
            val list = linkedListOf(1)
            list.deleteValue(1) shouldBe true
            list.toList() shouldBe emptyList()
        }
    }

    context("contains") {
        test("returns false on an empty list") {
            val list = SinglyLinkedList()
            list.contains(1) shouldBe false
        }

        test("finds a value present in the list") {
            val list = linkedListOf(1, 2, 3)
            list.contains(2) shouldBe true
        }

        test("returns false for a value not in the list") {
            val list = linkedListOf(1, 2, 3)
            list.contains(9) shouldBe false
        }
    }

    context("reverse") {
        test("reversing an empty list is a no-op") {
            val list = SinglyLinkedList()
            list.reverse()
            list.toList() shouldBe emptyList()
        }

        test("reversing a single-element list is a no-op") {
            val list = linkedListOf(1)
            list.reverse()
            list.toList() shouldBe listOf(1)
        }

        test("reverses the order of a multi-element list") {
            val list = linkedListOf(1, 2, 3, 4)
            list.reverse()
            list.toList() shouldBe listOf(4, 3, 2, 1)
        }

        test("the list is still correctly linked after reversing") {
            val list = linkedListOf(1, 2, 3)
            list.reverse()
            list.addFirst(0)
            list.addLast(9)
            list.toList() shouldBe listOf(0, 3, 2, 1, 9)
        }
    }

    context("toList") {
        test("an empty list produces an empty list") {
            val list = SinglyLinkedList()
            list.toList() shouldBe emptyList()
        }

        test("reflects insertion order") {
            val list = linkedListOf(1, 2, 3)
            list.toList() shouldBe listOf(1, 2, 3)
        }
    }
})
