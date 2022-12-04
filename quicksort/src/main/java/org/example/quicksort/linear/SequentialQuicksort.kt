package org.example.quicksort.linear

import org.example.quicksort.Helper
import org.example.quicksort.Quicksort

object SequentialQuicksort : Quicksort {

    override fun sort(array: IntArray) = quickSort(array, 0, array.size)

    private fun quickSort(arr: IntArray, l: Int, r: Int) {
        if (l >= r) return

        val splitter = Helper.splitter(arr, l, r)
        val (less, equal) = Helper.partition(arr, l, r, splitter)

        quickSort(arr, l, less)
        quickSort(arr, equal, r)
    }
}
