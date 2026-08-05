package queue

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CircularBufferQueueTest : FunSpec({

    context("enqueue") {
        test("enqueueing into an empty queue makes it non-empty") {
            val queue = CircularBufferQueue(4)
            queue.enqueue(1)
            queue.isEmpty() shouldBe false
            queue.size() shouldBe 1
            queue.peek() shouldBe 1
        }

        test("the front stays the first value enqueued, not the most recent") {
            val queue = CircularBufferQueue(4)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.enqueue(3)
            queue.peek() shouldBe 1
            queue.size() shouldBe 3
        }

        test("throws once the buffer reaches capacity") {
            val queue = CircularBufferQueue(2)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.isFull() shouldBe true
            shouldThrow<IllegalStateException> { queue.enqueue(3) }
        }

        test("a rejected enqueue on a full buffer doesn't corrupt existing values") {
            val queue = CircularBufferQueue(2)
            queue.enqueue(1)
            queue.enqueue(2)
            shouldThrow<IllegalStateException> { queue.enqueue(3) }
            queue.dequeue() shouldBe 1
            queue.dequeue() shouldBe 2
        }
    }

    context("dequeue") {
        test("throws on an empty queue") {
            val queue = CircularBufferQueue(4)
            shouldThrow<NoSuchElementException> { queue.dequeue() }
        }

        test("removes and returns the front value") {
            val queue = CircularBufferQueue(4)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.dequeue() shouldBe 1
            queue.size() shouldBe 1
        }

        test("dequeues values in the same order they were enqueued") {
            val queue = CircularBufferQueue(4)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.enqueue(3)
            queue.dequeue() shouldBe 1
            queue.dequeue() shouldBe 2
            queue.dequeue() shouldBe 3
        }

        test("draining a queue empties it, and a further dequeue still throws") {
            val queue = CircularBufferQueue(2)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.dequeue()
            queue.dequeue()
            queue.isEmpty() shouldBe true
            shouldThrow<NoSuchElementException> { queue.dequeue() }
        }

        test("wraps correctly when back and front cycle past the end of the buffer") {
            val queue = CircularBufferQueue(3)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.enqueue(3)
            queue.dequeue() shouldBe 1
            queue.dequeue() shouldBe 2
            // back wraps around to index 0 here, reusing a freed slot
            queue.enqueue(4)
            queue.enqueue(5)
            queue.isFull() shouldBe true
            queue.dequeue() shouldBe 3
            queue.dequeue() shouldBe 4
            queue.dequeue() shouldBe 5
            queue.isEmpty() shouldBe true
        }
    }

    context("peek") {
        test("throws on an empty queue") {
            val queue = CircularBufferQueue(4)
            shouldThrow<NoSuchElementException> { queue.peek() }
        }

        test("returns the front value without removing it") {
            val queue = CircularBufferQueue(4)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.peek() shouldBe 1
            queue.size() shouldBe 2
            queue.peek() shouldBe 1
        }
    }

    context("isEmpty") {
        test("true for a freshly created queue") {
            val queue = CircularBufferQueue(4)
            queue.isEmpty() shouldBe true
        }

        test("false after an enqueue") {
            val queue = CircularBufferQueue(4)
            queue.enqueue(1)
            queue.isEmpty() shouldBe false
        }

        test("true again after dequeuing the only element") {
            val queue = CircularBufferQueue(4)
            queue.enqueue(1)
            queue.dequeue()
            queue.isEmpty() shouldBe true
        }
    }

    context("isFull") {
        test("false for a freshly created queue") {
            val queue = CircularBufferQueue(2)
            queue.isFull() shouldBe false
        }

        test("true once capacity is reached") {
            val queue = CircularBufferQueue(2)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.isFull() shouldBe true
        }

        test("false again after dequeuing from a full buffer") {
            val queue = CircularBufferQueue(2)
            queue.enqueue(1)
            queue.enqueue(2)
            queue.dequeue()
            queue.isFull() shouldBe false
        }
    }

    context("size") {
        test("0 for a freshly created queue") {
            val queue = CircularBufferQueue(4)
            queue.size() shouldBe 0
        }

        test("reflects a mix of enqueues and dequeues accurately, including across a wrap") {
            val queue = CircularBufferQueue(3)
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
