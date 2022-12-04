package org.example.quicksort

import org.example.quicksort.linear.SequentialQuicksort
import org.example.quicksort.parallel.ParallelQuicksort

interface Quicksort {

    fun sort(array: IntArray)

    enum class QuicksortType(private val instance: Quicksort) : Quicksort {
        SEQUENTIAL(SequentialQuicksort),
        PARALLEL(ParallelQuicksort)
        ;

        override fun sort(array: IntArray) = instance.sort(array)
    }
}
