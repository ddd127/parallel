package org.example.quicksort.parallel

import org.example.quicksort.Helper
import org.example.quicksort.Quicksort
import org.example.quicksort.linear.SequentialQuicksort
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveAction

object ParallelQuicksort : Quicksort {

    override fun sort(array: IntArray) {
        val task = QuickSortTask(array, array.size / (CORE_COUNT * 16), 0, array.size)
        pool.invoke(task)
    }

    private class QuickSortTask(
        private val arr: IntArray,
        private val l: Int,
        private val r: Int,
        private val block: Int,
    ) : RecursiveAction() {
        override fun compute() {
            if (arr.size <= block) {
                SequentialQuicksort.sort(arr)
                return
            }
            val splitter = Helper.splitter(arr, l, r)
            val (less, equal) = Helper.partition(arr, l, r, splitter)
            val leftTask = QuickSortTask(arr, l, less, block)
            val rightTask = QuickSortTask(arr, equal, r, block)
            rightTask.invoke()
            leftTask.invoke()
        }
    }

    private const val CORE_COUNT = 4
    private val pool = ForkJoinPool(CORE_COUNT)
//    private val VH: VarHandle = MethodHandles.arrayElementVarHandle(IntArray::class.java)
}
