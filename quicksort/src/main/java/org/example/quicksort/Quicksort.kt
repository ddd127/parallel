package org.example.quicksort

import kotlinx.coroutines.asCoroutineDispatcher
import org.example.quicksort.linear.SequentialQuicksort
import org.example.quicksort.parallel.ParallelQuicksort
import java.util.concurrent.Executors

interface Quicksort {

    fun sort(array: IntArray)

    enum class QuicksortType {
        SEQUENTIAL {
            override fun withSort(block: (Quicksort) -> Unit) {
                block(SequentialQuicksort)
            }
        },
        PARALLEL {
            override fun withSort(block: (Quicksort) -> Unit) {
                val pool = Executors.newFixedThreadPool(4)
                val sort = ParallelQuicksort(pool.asCoroutineDispatcher())
                block(sort)
                pool.shutdownNow()
            }
        }
        ;

        abstract fun withSort(block: (Quicksort) -> Unit)
    }
}
