package org.example.quicksort.parallel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.example.quicksort.Helper
import org.example.quicksort.Quicksort
import org.example.quicksort.linear.SequentialQuicksort

class ParallelQuicksort(
    private val dispatcher: CoroutineDispatcher,
) : Quicksort {

    override fun sort(array: IntArray) {
        runBlocking {
            withContext(dispatcher) {
                quicksort(
                    arr = array,
                    l = 0,
                    r = array.size,
                    block = array.size / (CORE_COUNT * MULTIPLIER) + 1,
                )
            }
        }
    }

    private suspend fun quicksort(arr: IntArray, l: Int, r: Int, block: Int) {
        coroutineScope {
            if (r - l <= block) {
                SequentialQuicksort.quickSort(arr, l, r)
                return@coroutineScope
            }
            val splitter = Helper.splitter(arr, l, r)
            val (less, equal) = Helper.partition(arr, l, r, splitter)
            val left = launch {
                quicksort(arr, l, less, block)
            }
            val right = launch {
                quicksort(arr, equal, r, block)
            }
            joinAll(left, right)
        }
    }

    companion object {
        private const val CORE_COUNT = 4
        private const val MULTIPLIER = 8
    }
}
