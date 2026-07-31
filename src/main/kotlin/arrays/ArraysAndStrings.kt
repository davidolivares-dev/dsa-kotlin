package arrays

fun reverseArray(arr: IntArray) {
    var left = 0
    var right = arr.lastIndex
    while (left < right) {
        val temp = arr[left]
        arr[left] = arr[right]
        arr[right] = temp
        left++
        right--
    }
}

fun reverseString(chars: CharArray) {
    var left = 0
    var right = chars.lastIndex
    while (left < right) {
        val temp = chars[left]
        chars[left] = chars[right]
        chars[right] = temp
        left++
        right--
    }
}

fun isPalindrome(s: String): Boolean {
    var left = 0
    var right = s.lastIndex
    while (left < right) {
        if (s[left] != s[right]) return false
        left++
        right--
    }
    return true
}

fun removeDuplicates(arr: IntArray): Int {
    if (arr.isEmpty()) return 0
    var curr = 0
    var next = 1
    while (next < arr.size) {
        if (arr[curr] != arr[next]) {
            curr++
            arr[curr] = arr[next]
        }
        next++
    }
    return curr + 1
}