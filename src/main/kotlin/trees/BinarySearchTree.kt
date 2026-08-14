package trees

class BinarySearchTree {
    private class Node(var value: Int, var left: Node? = null, var right: Node? = null)

    private var root: Node? = null
    private var size = 0

    fun insert(value: Int) {
        root = insertInto(root, value)
    }

    fun contains(value: Int): Boolean = containsValue(root, value)

    fun delete(value: Int) {
        root = deleteFrom(root, value)
    }

    fun min(): Int {
        val start = root ?: throw NoSuchElementException("Empty root node")
        return leftMost(start).value
    }

    fun max(): Int {
        val start = root ?: throw NoSuchElementException("Empty root node")
        return rightMost(start).value
    }

    fun inOrder(): List<Int> {
        val nodes = mutableListOf<Int>()
        inOrderTraversal(root, nodes)
        return nodes
    }

    fun height(): Int = getHeight(root)

    fun size(): Int = size

    fun isEmpty(): Boolean = size == 0

    // private helpers
    private fun insertInto(node: Node?, value: Int): Node {
        if (node == null) {
            size++
            return Node(value)
        }
        when {
            value < node.value -> node.left = insertInto(node.left, value)
            value > node.value -> node.right = insertInto(node.right, value)
        }
        return node
    }

    private fun containsValue(node: Node?, value: Int): Boolean {
        if (node == null) return false
        return when {
            value < node.value -> containsValue(node.left, value)
            value > node.value -> containsValue(node.right, value)
            else -> true
        }
    }

    private fun deleteFrom(node: Node?, value: Int): Node? {
        if (node == null) return node
        when {
            value < node.value -> node.left = deleteFrom(node.left, value)
            value > node.value -> node.right = deleteFrom(node.right, value)
            else -> {
                // case 1: no left child — the right subtree (possibly null) takes this
                // node's place. A leaf lands here too, returning null.
                if (node.left == null) {
                    size--
                    return node.right
                }
                // case 2: no right child — the left subtree moves up.
                val rightNode = node.right
                if (rightNode == null) {
                    size--
                    return node.left
                }
                // case 3: two children. Overwrite this node's value with its in-order
                // successor, then delete the successor from the right subtree. No
                // size-- here: that recursive call removes a node and decrements.
                val successor = leftMost(rightNode)
                node.value = successor.value
                node.right = deleteFrom(rightNode, successor.value)
            }
        }
        return node
    }

    private fun leftMost(from: Node): Node {
        var node = from
        while (true) {
            val left = node.left ?: break
            node = left
        }
        return node
    }

    private fun rightMost(from: Node): Node {
        var node = from
        while (true) {
            val right = node.right ?: break
            node = right
        }
        return node
    }

    private fun getHeight(node: Node?): Int {
        if (node == null) return -1
        return 1 + maxOf(getHeight(node.left), getHeight(node.right))
    }

    private fun inOrderTraversal(node: Node?, nodes: MutableList<Int>) {
        if (node == null) return
        inOrderTraversal(node.left, nodes)
        nodes.add(node.value)
        inOrderTraversal(node.right, nodes)
    }
}
