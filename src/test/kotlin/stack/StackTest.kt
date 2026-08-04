package stack

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StackTest : FunSpec({

    context("push") {
        test("pushing onto an empty stack makes it non-empty") {
            val stack = Stack()
            stack.push(1)
            stack.isEmpty() shouldBe false
            stack.size() shouldBe 1
            stack.peek() shouldBe 1
        }

        test("each push becomes the new top") {
            val stack = Stack()
            stack.push(1)
            stack.push(2)
            stack.push(3)
            stack.peek() shouldBe 3
            stack.size() shouldBe 3
        }
    }

    context("pop") {
        test("throws on an empty stack") {
            val stack = Stack()
            shouldThrow<NoSuchElementException> { stack.pop() }
        }

        test("removes and returns the top value") {
            val stack = Stack()
            stack.push(1)
            stack.push(2)
            stack.pop() shouldBe 2
            stack.size() shouldBe 1
        }

        test("pops values in reverse order of pushes") {
            val stack = Stack()
            stack.push(1)
            stack.push(2)
            stack.push(3)
            stack.pop() shouldBe 3
            stack.pop() shouldBe 2
            stack.pop() shouldBe 1
        }

        test("draining a stack empties it, and a further pop still throws") {
            val stack = Stack()
            stack.push(1)
            stack.push(2)
            stack.pop()
            stack.pop()
            stack.isEmpty() shouldBe true
            shouldThrow<NoSuchElementException> { stack.pop() }
        }

        test("a single push immediately followed by a pop returns that value") {
            val stack = Stack()
            stack.push(5)
            stack.pop() shouldBe 5
            stack.isEmpty() shouldBe true
        }
    }

    context("peek") {
        test("throws on an empty stack") {
            val stack = Stack()
            shouldThrow<NoSuchElementException> { stack.peek() }
        }

        test("returns the top value without removing it") {
            val stack = Stack()
            stack.push(1)
            stack.push(2)
            stack.peek() shouldBe 2
            stack.size() shouldBe 2
            stack.peek() shouldBe 2
        }
    }

    context("isEmpty") {
        test("true for a freshly created stack") {
            val stack = Stack()
            stack.isEmpty() shouldBe true
        }

        test("false after a push") {
            val stack = Stack()
            stack.push(1)
            stack.isEmpty() shouldBe false
        }

        test("true again after popping the only element") {
            val stack = Stack()
            stack.push(1)
            stack.pop()
            stack.isEmpty() shouldBe true
        }
    }

    context("size") {
        test("0 for a freshly created stack") {
            val stack = Stack()
            stack.size() shouldBe 0
        }

        test("reflects a mix of pushes and pops accurately") {
            val stack = Stack()
            stack.push(1)
            stack.push(2)
            stack.size() shouldBe 2
            stack.pop()
            stack.size() shouldBe 1
            stack.push(3)
            stack.push(4)
            stack.size() shouldBe 3
        }
    }
})
