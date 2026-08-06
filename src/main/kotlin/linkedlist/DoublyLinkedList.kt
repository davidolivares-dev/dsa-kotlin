package linkedlist

class DoublyLinkedList {
    private class Node(val value: Int, var prev: Node? = null, var next: Node? = null)

    private var head: Node? = null
    private var tail: Node? = null

    fun addFirst(value: Int) {
        if (head == null) {
            head = Node(value)
            tail = head
            return
        }
        val node = Node(value)
        node.next = head
        head?.prev = node
        head = node
    }

    fun addLast(value: Int) {
        if (tail == null) {
            tail = Node(value)
            head = tail
            return
        }
        val node = Node(value)
        node.prev = tail
        tail?.next = node
        tail = node
    }

    fun deleteFirst(): Int {
        val first = head ?: throw NoSuchElementException("List is empty")
        head = first.next
        head?.prev = null
        if (head == null) {
            tail = null
        }
        return first.value
    }

    fun deleteLast(): Int {
        val last = tail ?: throw NoSuchElementException("List is empty")
        tail = last.prev
        tail?.next = null
        if (tail == null) {
            head = null
        }
        return last.value
    }

    fun deleteValue(value: Int): Boolean {
        var curr = head
        while (curr != null) {
            if (curr.value == value) {
                curr.next?.prev = curr.prev
                curr.prev?.next = curr.next
                if (curr === head) {
                    head = curr.next
                }
                if (curr === tail) {
                    tail = curr.prev
                }
                return true
            }
            curr = curr.next
        }
        return false
    }

    fun contains(value: Int): Boolean {
        var curr = tail
        while (curr != null) {
            if (curr.value == value) {
                return true
            }
            curr = curr.prev
        }
        return false
    }

    fun reverse() {
        var curr = head
        while (curr != null) {
            val next = curr.next
            curr.next = curr.prev
            curr.prev = next
            curr = next
        }
        val oldHead = head
        head = tail
        tail = oldHead
    }

    fun toList(): List<Int> {
        val list = mutableListOf<Int>()
        var curr = head
        while (curr != null) {
            list.add(curr.value)
            curr = curr.next
        }
        return list
    }
}
