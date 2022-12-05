package org.example.quicksort.parallel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
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
                    lArg = 0,
                    rArg = array.size,
                    block = array.size / (CORE_COUNT * MULTIPLIER) + 1,
                )
            }
        }
    }

    private suspend fun quicksort(arr: IntArray, lArg: Int, rArg: Int, block: Int) {
        var l = lArg
        var r = rArg
        coroutineScope {
            val createdJobs = mutableListOf<Job>()
            while (r - l > block) {
                val splitter = Helper.splitter(arr, l, r)
                val (less, equal) = Helper.partition(arr, l, r, splitter)
                if (less - l < r - equal) {
                    createdJobs.add(
                        launch { quicksort(arr, l, less, block) }
                    )
                    l = equal
                } else {
                    createdJobs.add(
                        launch { quicksort(arr, equal, r, block) }
                    )
                    r = less
                }
            }
            SequentialQuicksort.quickSort(arr, l, r)
            createdJobs.joinAll()
        }
    }

    companion object {
        private const val CORE_COUNT = 4
        private const val MULTIPLIER = 8
    }
}
