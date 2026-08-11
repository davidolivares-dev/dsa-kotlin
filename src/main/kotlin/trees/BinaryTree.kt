package trees

class BinaryTree(private val root: Node?) {
    class Node(val value: Int, val left: Node? = null, val right: Node? = null)

    fun preOrder(): List<Int> {
        val nodes = mutableListOf<Int>()
        preOrderTraversal(root, nodes)
        return nodes
    }

    fun inOrder(): List<Int> {
        val nodes = mutableListOf<Int>()
        inOrderTraversal(root, nodes)
        return nodes
    }

    fun postOrder(): List<Int> {
        val nodes = mutableListOf<Int>()
        postOrderTraversal(root, nodes)
        return nodes
    }

    fun levelOrder(): List<List<Int>> {
        val rootNode = root ?: return emptyList()
        val queue = ArrayDeque<Node>()
        val nodes = mutableListOf<List<Int>>()
        queue.add(rootNode)
        while (queue.isNotEmpty()) {
            val currentLevel = mutableListOf<Int>()
            repeat(queue.size) {
                val currentNode = queue.removeFirst()
                currentLevel.add(currentNode.value)
                if (currentNode.left != null) {
                    queue.addLast(currentNode.left)
                }
                if (currentNode.right != null) {
                    queue.addLast(currentNode.right)
                }
            }
            nodes.add(currentLevel)
        }
        return nodes
    }

    fun height(): Int = getHeight(root)

    fun size(): Int = getSize(root)

    fun countLeaves(): Int = getLeafCount(root)

    fun contains(value: Int): Boolean = containsValue(root, value)

    // private helpers
    private fun preOrderTraversal(node: Node?, nodes: MutableList<Int>) {
        if (node == null) return
        nodes.add(node.value)
        preOrderTraversal(node.left, nodes)
        preOrderTraversal(node.right, nodes)
    }

    private fun inOrderTraversal(node: Node?, nodes: MutableList<Int>) {
        if (node == null) return
        inOrderTraversal(node.left, nodes)
        nodes.add(node.value)
        inOrderTraversal(node.right, nodes)
    }

    private fun postOrderTraversal(node: Node?, nodes: MutableList<Int>) {
        if (node == null) return
        postOrderTraversal(node.left, nodes)
        postOrderTraversal(node.right, nodes)
        nodes.add(node.value)
    }

    private fun getHeight(node: Node?): Int {
        if (node == null) return -1
        return 1 + maxOf(getHeight(node.left), getHeight(node.right))
    }

    private fun getSize(node: Node?): Int {
        if (node == null) return 0
        return 1 + getSize(node.left) + getSize(node.right)
    }

    private fun containsValue(node: Node?, target: Int): Boolean {
        if (node == null) return false
        if (target == node.value) return true
        return containsValue(node.left, target) || containsValue(node.right, target)
    }

    private fun getLeafCount(node: Node?): Int {
        if (node == null) return 0
        if (node.left == null && node.right == null) return 1
        return getLeafCount(node.left) + getLeafCount(node.right)
    }
}
