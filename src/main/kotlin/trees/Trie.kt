package trees

class Trie {
    private class Node {
        val children = mutableMapOf<Char, Node>()
        var isWord = false
    }

    private val root = Node()
    private var size = 0

    fun insert(word: String) {
        var currNode = root
        for (char in word) {
            currNode = currNode.children.getOrPut(char) { Node() }
        }
        if (!currNode.isWord) {
            currNode.isWord = true
            size++
        }
    }

    fun contains(word: String): Boolean = nodeAt(word)?.isWord ?: false

    fun startsWith(prefix: String): Boolean = nodeAt(prefix) != null

    fun wordsWithPrefix(prefix: String): List<String> {
        val start = nodeAt(prefix) ?: return emptyList()
        val result = mutableListOf<String>()
        getWords(start, prefix, result)
        return result
    }

    fun words(): List<String> = wordsWithPrefix("")

    fun size(): Int = size

    fun isEmpty(): Boolean = size == 0

    // private helpers
    private fun nodeAt(path: String): Node? {
        var currNode = root
        for (char in path) {
            currNode = currNode.children[char] ?: return null
        }
        return currNode
    }

    private fun getWords(node: Node, currPrefix: String, result: MutableList<String>) {
        if (node.isWord) {
            result.add(currPrefix)
        }
        for ((key, child) in node.children.entries.sortedBy { it.key }) {
            getWords(child, currPrefix + key, result)
        }
    }
}
