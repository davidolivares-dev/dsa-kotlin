package linkedlist

class SinglyLinkedList {
    private class Node(val value: Int, var next: Node? = null)

    private var head: Node? = null

    fun addFirst(value: Int) {
        val node = Node(value)
        node.next = head
        head = node
    }

    fun addLast(value: Int) {
        if (head == null) {
            head = Node(value)
            return
        }

        var curr = head
        val node = Node(value)
        while (curr?.next != null) {
            curr = curr.next
        }
        curr?.next = node
    }

    fun deleteFirst(): Int {
        val curr = head ?: throw NoSuchElementException("List is empty")
        head = curr.next
        return curr.value
    }

    fun deleteValue(value: Int): Boolean {
        var curr = head
        if (curr?.value == value) {
            head = curr.next
            return true
        }

        var prev = curr
        curr = curr?.next
        while (curr != null) {
            if (curr.value == value) {
                prev?.next = curr.next
                return true
            }
            prev = curr
            curr = curr.next
        }
        return false
    }

    fun contains(value: Int): Boolean {
        var curr = head
        while (curr != null) {
            if (curr.value == value) {
                return true
            }
            curr = curr.next
        }
        return false
    }

    fun reverse() {
        var prev: Node? = null
        var curr = head
        while (curr != null) {
            val next = curr.next
            curr.next = prev
            prev = curr
            curr = next
        }
        head = prev
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
