package org.example.quicksort

internal object Helper {

    fun partition(arr: IntArray, lArg: Int, rArg: Int, splitter: Int): Pair<Int, Int> {
        var l = lArg
        var e = lArg
        var r = rArg
        var tmp: Int
        while (e < r) {
            when {
                arr[e] < splitter -> {
                    tmp = arr[l]
                    arr[l] = arr[e]
                    arr[e] = tmp
                    ++l
                    ++e
                }
                arr[e] > splitter -> {
                    tmp = arr[e]
                    arr[e] = arr[--r]
                    arr[r] = tmp
                }
                else -> ++e
            }
        }
        return l to e
    }

    fun splitter(arr: IntArray, lArg: Int, rArg: Int): Int {
        return arr[(lArg + rArg) shr 1]
    }
}
