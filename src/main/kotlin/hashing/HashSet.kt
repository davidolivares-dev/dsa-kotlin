package hashing

import kotlin.math.absoluteValue

private const val LOAD_FACTOR_THRESHOLD = 0.75

class HashSet {
    private var capacity = 8
    private var buckets = Array(capacity) { mutableListOf<Int>() }
    private var size = 0

    fun add(value: Int): Boolean {
        val bucket = bucketFor(value)
        if (bucket.contains(value)) return false

        bucket.add(value)
        size++
        if (thresholdExceeded()) {
            resize(capacity * 2)
        }
        return true
    }

    fun contains(value: Int): Boolean = bucketFor(value).contains(value)

    fun remove(value: Int): Boolean {
        if (!bucketFor(value).remove(value)) return false

        size--
        return true
    }

    fun isEmpty(): Boolean = size == 0

    fun size(): Int = size

    fun toList(): List<Int> = buckets.flatMap { it }

    fun union(other: HashSet): HashSet = setFrom(toList() + other.toList())

    fun intersection(other: HashSet): HashSet = setFrom(other.toList().filter { contains(it) })

    fun difference(other: HashSet): HashSet = setFrom(toList().filter { !other.contains(it) })

    // private helpers
    private fun bucketFor(value: Int): MutableList<Int> = buckets[bucketIndex(value)]

    private fun bucketIndex(value: Int): Int = value.hashCode().absoluteValue % capacity

    private fun thresholdExceeded(): Boolean = size / capacity.toDouble() > LOAD_FACTOR_THRESHOLD

    private fun resize(newCapacity: Int) {
        capacity = newCapacity
        val resizedArray = Array(capacity) { mutableListOf<Int>() }
        buckets.forEach { bucket ->
            bucket.forEach { value ->
                resizedArray[bucketIndex(value)].add(value)
            }
        }
        buckets = resizedArray
    }

    private fun setFrom(values: List<Int>): HashSet {
        val result = HashSet()
        values.forEach { result.add(it) }
        return result
    }
}
