package org.example.quicksort.parallel

import org.example.quicksort.Helper
import org.example.quicksort.Quicksort
import org.example.quicksort.linear.SequentialQuicksort
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction

object ParallelQuicksort : Quicksort {

    override fun sort(array: IntArray) {
        val task = QuickSortTask(
            arr = array,
            l = 0,
            r = array.size,
            block = array.size / (CORE_COUNT * MULTIPLIER) + 1,
            null,
        )
        pool.invoke(task)
    }

    private class QuickSortTask(
        private val arr: IntArray,
        private var l: Int,
        private var r: Int,
        private val block: Int,
        private val next: QuickSortTask?,
    ) : RecursiveAction() {
        override fun compute() {
            var task: QuickSortTask? = null
            while (r - l > block && getSurplusQueuedTaskCount() < 4) {
                val splitter = Helper.splitter(arr, l, r)
                val (less, equal) = Helper.partition(arr, l, r, splitter)
                if (less - l < r - equal) {
                    task = QuickSortTask(arr, l, less, block, task)
                    l = equal
                } else {
                    task = QuickSortTask(arr, equal, r, block, task)
                    r = less
                }
                task.fork()
            }
            SequentialQuicksort.quickSort(arr, l, r)
            while (task != null) {
                if (task.tryUnfork()) {
                    SequentialQuicksort.quickSort(arr, task.l, task.r)
                } else {
                    task.join()
                }
                task = task.next
            }

//            if (r - l <= block) {
//                SequentialQuicksort.quickSort(arr, l, r)
//                return
//            }
//            val splitter = Helper.splitter(arr, l, r)
//            val (less, equal) = Helper.partition(arr, l, r, splitter)
//            val leftTask = QuickSortTask(arr, l, less, block)
//            val rightTask = QuickSortTask(arr, equal, r, block)
//            rightTask.invoke()
//            leftTask.invoke()
//            leftTask.join()
//            rightTask.join()
        }
    }

    private const val CORE_COUNT = 4
    private const val MULTIPLIER = 8
    private val pool = ForkJoinPool(CORE_COUNT)
}
