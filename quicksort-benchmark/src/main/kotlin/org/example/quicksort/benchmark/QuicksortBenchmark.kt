package org.example.quicksort.benchmark

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.example.quicksort.linear.SequentialQuicksort
import org.example.quicksort.parallel.ParallelQuicksort
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

open class QuicksortBenchmark {

    @State(Scope.Benchmark)
    open class SortState {

        private val executor = Executors.newFixedThreadPool(4)
        private val size = System.getProperty("quicksort.benchmark.array.size").toInt()
        val dispatcher: CoroutineDispatcher = executor.asCoroutineDispatcher()

        private val array: IntArray = Random(127).let { random ->
            IntArray(size) { random.nextInt() }
        }
        var current: IntArray = IntArray(size)

        @Setup(Level.Invocation)
        fun setup() {
            array.forEachIndexed { index, item ->
                current[index] = item
            }
        }

        @TearDown(Level.Trial)
        fun tearDown() {
            executor.shutdownNow()
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    fun sequentialQuicksort(state: SortState) {
        val array = state.current
        SequentialQuicksort.sort(array)
        for (i in 1 until array.size) {
            assert(array[i - 1] <= array[i])
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    fun parallelQuicksort(state: SortState) {
        val parallelSort = ParallelQuicksort(state.dispatcher)
        val array = state.current
        parallelSort.sort(array)
        for (i in 1 until array.size) {
            assert(array[i - 1] <= array[i])
        }
    }
}
