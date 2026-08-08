package hashing

import kotlin.math.absoluteValue

private const val LOAD_FACTOR_THRESHOLD = 0.75

class HashTable {
    private var capacity = 8
    private var buckets: Array<MutableList<Pair<Int, Int>>> = Array(capacity) { mutableListOf() }
    private var size = 0

    fun put(key: Int, value: Int) {
        val bucket = bucketFor(key)
        val keyIndex = bucket.indexOfFirst { it.first == key }

        if (keyIndex == -1) { // new key-value pair
            bucket.add(key to value)
            size++
            if (thresholdExceeded()) {
                resize(capacity * 2)
            }
        } else { // update key-value pair, no size increase
            bucket[keyIndex] = key to value
        }
    }

    fun get(key: Int): Int =
        bucketFor(key).find { it.first == key }?.second
            ?: throw NoSuchElementException("No such key: $key")

    fun remove(key: Int): Int {
        val bucket = bucketFor(key)
        val keyIndex = bucket.indexOfFirst { it.first == key }
        if (keyIndex == -1) throw NoSuchElementException("No such key: $key")
        size--
        return bucket.removeAt(keyIndex).second
    }

    fun containsKey(key: Int): Boolean = bucketFor(key).any { it.first == key }

    fun isEmpty(): Boolean = size == 0

    fun size(): Int = size

    // private helpers
    private fun bucketFor(key: Int): MutableList<Pair<Int, Int>> = buckets[bucketIndex(key)]

    private fun bucketIndex(key: Int): Int = key.hashCode().absoluteValue % capacity

    private fun thresholdExceeded(): Boolean = size / capacity.toDouble() > LOAD_FACTOR_THRESHOLD

    private fun resize(newCapacity: Int) {
        capacity = newCapacity
        val resizedArray = Array(capacity) { mutableListOf<Pair<Int, Int>>() }
        buckets.forEach { bucket ->
            bucket.forEach { pair ->
                resizedArray[bucketIndex(pair.first)].add(pair)
            }
        }
        buckets = resizedArray
    }
}
