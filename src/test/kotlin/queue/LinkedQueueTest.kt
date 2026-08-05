package queue

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LinkedQueueTest : FunSpec({

    context("enqueue") {
        test("enqueueing into an empty queue makes it non-empty") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.isEmpty() shouldBe false
            queue.size() shouldBe 1
            queue.peek() shouldBe 1
        }

        test("the front stays the first value enqueued, not the most recent") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.enqueue(2)
            queue.enqueue(3)
            queue.peek() shouldBe 1
            queue.size() shouldBe 3
        }
    }

    context("dequeue") {
        test("throws on an empty queue") {
            val queue = LinkedQueue()
            shouldThrow<NoSuchElementException> { queue.dequeue() }
        }

        test("removes and returns the front value") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.enqueue(2)
            queue.dequeue() shouldBe 1
            queue.size() shouldBe 1
        }

        test("dequeues values in the same order they were enqueued") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.enqueue(2)
            queue.enqueue(3)
            queue.dequeue() shouldBe 1
            queue.dequeue() shouldBe 2
            queue.dequeue() shouldBe 3
        }

        test("draining a queue empties it, and a further dequeue still throws") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.enqueue(2)
            queue.dequeue()
            queue.dequeue()
            queue.isEmpty() shouldBe true
            shouldThrow<NoSuchElementException> { queue.dequeue() }
            queue.enqueue(9)
            queue.peek() shouldBe 9
        }
    }

    context("peek") {
        test("throws on an empty queue") {
            val queue = LinkedQueue()
            shouldThrow<NoSuchElementException> { queue.peek() }
        }

        test("returns the front value without removing it") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.enqueue(2)
            queue.peek() shouldBe 1
            queue.size() shouldBe 2
            queue.peek() shouldBe 1
        }
    }

    context("isEmpty") {
        test("true for a freshly created queue") {
            val queue = LinkedQueue()
            queue.isEmpty() shouldBe true
        }

        test("false after an enqueue") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.isEmpty() shouldBe false
        }

        test("true again after dequeuing the only element") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.dequeue()
            queue.isEmpty() shouldBe true
        }
    }

    context("size") {
        test("0 for a freshly created queue") {
            val queue = LinkedQueue()
            queue.size() shouldBe 0
        }

        test("reflects a mix of enqueues and dequeues accurately") {
            val queue = LinkedQueue()
            queue.enqueue(1)
            queue.enqueue(2)
            queue.size() shouldBe 2
            queue.dequeue()
            queue.size() shouldBe 1
            queue.enqueue(3)
            queue.enqueue(4)
            queue.size() shouldBe 3
        }
    }
})
