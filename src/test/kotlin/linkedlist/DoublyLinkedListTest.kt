package linkedlist

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DoublyLinkedListTest : FunSpec({

    fun doublyLinkedListOf(vararg values: Int): DoublyLinkedList {
        val list = DoublyLinkedList()
        for (value in values) list.addLast(value)
        return list
    }

    context("addFirst") {
        test("inserting into an empty list sets a single-element list") {
            val list = DoublyLinkedList()
            list.addFirst(1)
            list.toList() shouldBe listOf(1)
        }

        test("each insert becomes the new head") {
            val list = DoublyLinkedList()
            list.addFirst(1)
            list.addFirst(2)
            list.addFirst(3)
            list.toList() shouldBe listOf(3, 2, 1)
        }
    }

    context("addLast") {
        test("inserting into an empty list sets a single-element list") {
            val list = DoublyLinkedList()
            list.addLast(1)
            list.toList() shouldBe listOf(1)
        }

        test("each insert becomes the new tail") {
            val list = DoublyLinkedList()
            list.addLast(1)
            list.addLast(2)
            list.addLast(3)
            list.toList() shouldBe listOf(1, 2, 3)
        }
    }

    context("deleteFirst") {
        test("throws on an empty list") {
            val list = DoublyLinkedList()
            shouldThrow<NoSuchElementException> { list.deleteFirst() }
        }

        test("removes and returns the head's value") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.deleteFirst() shouldBe 1
            list.toList() shouldBe listOf(2, 3)
        }

        test("clears the new head's prev, so the old head isn't reachable walking backward") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.deleteFirst()
            list.contains(1) shouldBe false
        }

        test("draining a list one node at a time empties it in both directions") {
            val list = doublyLinkedListOf(1, 2)
            list.deleteFirst()
            list.deleteFirst()
            list.toList() shouldBe emptyList()
            shouldThrow<NoSuchElementException> { list.deleteFirst() }
            list.addFirst(9)
            list.toList() shouldBe listOf(9)
        }
    }

    context("deleteLast") {
        test("throws on an empty list") {
            val list = DoublyLinkedList()
            shouldThrow<NoSuchElementException> { list.deleteLast() }
        }

        test("removes and returns the tail's value") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.deleteLast() shouldBe 3
            list.toList() shouldBe listOf(1, 2)
        }

        test("clears the new tail's next, so the old tail isn't reachable walking forward") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.deleteLast()
            list.toList() shouldBe listOf(1, 2)
            list.contains(3) shouldBe false
        }

        test("draining a list one node at a time empties it in both directions") {
            val list = doublyLinkedListOf(1, 2)
            list.deleteLast()
            list.deleteLast()
            list.toList() shouldBe emptyList()
            shouldThrow<NoSuchElementException> { list.deleteLast() }
            list.addLast(9)
            list.toList() shouldBe listOf(9)
        }
    }

    context("deleteValue") {
        test("returns false on an empty list") {
            val list = DoublyLinkedList()
            list.deleteValue(1) shouldBe false
        }

        test("returns false when the value isn't present") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.deleteValue(9) shouldBe false
            list.toList() shouldBe listOf(1, 2, 3)
        }

        test("removes the head when it holds the target value") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.deleteValue(1) shouldBe true
            list.toList() shouldBe listOf(2, 3)
            list.contains(1) shouldBe false
        }

        test("removes a middle node, relinking both directions around it") {
            val list = doublyLinkedListOf(1, 2, 3, 4)
            list.deleteValue(2) shouldBe true
            list.toList() shouldBe listOf(1, 3, 4)
            list.contains(2) shouldBe false
        }

        test("removes the tail node") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.deleteValue(3) shouldBe true
            list.toList() shouldBe listOf(1, 2)
            list.contains(3) shouldBe false
        }

        test("removes only the first occurrence of a duplicated value") {
            val list = doublyLinkedListOf(1, 2, 2, 3)
            list.deleteValue(2) shouldBe true
            list.toList() shouldBe listOf(1, 2, 3)
        }

        test("removing a single-node list's only value empties it in both directions") {
            val list = doublyLinkedListOf(1)
            list.deleteValue(1) shouldBe true
            list.toList() shouldBe emptyList()
            list.addFirst(9)
            list.toList() shouldBe listOf(9)
        }
    }

    context("contains") {
        test("returns false on an empty list") {
            val list = DoublyLinkedList()
            list.contains(1) shouldBe false
        }

        test("finds a value present in the list") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.contains(2) shouldBe true
        }

        test("returns false for a value not in the list") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.contains(9) shouldBe false
        }
    }

    context("reverse") {
        test("reversing an empty list is a no-op") {
            val list = DoublyLinkedList()
            list.reverse()
            list.toList() shouldBe emptyList()
        }

        test("reversing a single-element list is a no-op") {
            val list = doublyLinkedListOf(1)
            list.reverse()
            list.toList() shouldBe listOf(1)
        }

        test("reverses the order of a multi-element list") {
            val list = doublyLinkedListOf(1, 2, 3, 4)
            list.reverse()
            list.toList() shouldBe listOf(4, 3, 2, 1)
        }

        test("head and tail are correctly wired after reversing") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.reverse()
            list.addFirst(0)
            list.addLast(9)
            list.toList() shouldBe listOf(0, 3, 2, 1, 9)
        }
    }

    context("toList") {
        test("an empty list produces an empty list") {
            val list = DoublyLinkedList()
            list.toList() shouldBe emptyList()
        }

        test("reflects insertion order") {
            val list = doublyLinkedListOf(1, 2, 3)
            list.toList() shouldBe listOf(1, 2, 3)
        }
    }
})
