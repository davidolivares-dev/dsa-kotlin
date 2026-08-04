package stack

class Stack {
    private val elements = mutableListOf<Int>()

    fun push(value: Int) = elements.add(value)

    fun pop(): Int = elements.removeLast()

    fun peek(): Int = elements.last()

    fun isEmpty(): Boolean = elements.isEmpty()

    fun size(): Int = elements.size
}
