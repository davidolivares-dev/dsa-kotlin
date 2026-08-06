package queue

class LinkedQueue {
    private class Node(val value: Int, var next: Node? = null)

    private var head: Node? = null
    private var tail: Node? = null
    private var count = 0

    fun enqueue(value: Int) {
        val node = Node(value)
        if (head == null) {
            head = node
            tail = head
        } else {
            tail?.next = node
            tail = node
        }
        count++
    }

    fun dequeue(): Int {
        val curr = head ?: throw NoSuchElementException("Queue is empty")
        head = curr.next
        if (curr === tail) {
            tail = head
        }
        count--
        return curr.value
    }

    fun peek(): Int = head?.value ?: throw NoSuchElementException("Queue is empty")

    fun isEmpty(): Boolean = count == 0

    fun size(): Int = count
}
