package queue

class CircularBufferQueue(private val capacity: Int) {
    private val buffer = IntArray(capacity)
    private var front = 0
    private var back = 0
    private var count = 0

    fun enqueue(value: Int) {
        if (count == capacity) throw IllegalStateException("Queue is full")
        buffer[back] = value
        back = (back + 1) % buffer.size
        count++
    }

    fun dequeue(): Int {
        if (count == 0) throw NoSuchElementException("Queue is empty")
        val frontValue = buffer[front]
        front = (front + 1) % buffer.size
        count--
        return frontValue
    }

    fun peek(): Int {
        if (count == 0) throw NoSuchElementException("Queue is empty")
        return buffer[front]
    }

    fun isEmpty(): Boolean = count == 0

    fun isFull(): Boolean = count == capacity

    fun size(): Int = count
}
