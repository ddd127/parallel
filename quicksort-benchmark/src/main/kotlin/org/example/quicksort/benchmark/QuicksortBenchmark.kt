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
        private val random = Random(127)

        private val executor = Executors.newFixedThreadPool(4)
        val dispatcher: CoroutineDispatcher = executor.asCoroutineDispatcher()
        lateinit var array: IntArray

        @Setup(Level.Invocation)
        fun setup() {
            array = IntArray(System.getProperty("quicksort.benchmark.array.size").toInt()) {
                random.nextInt()
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
        SequentialQuicksort.sort(state.array)
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    fun parallelQuicksort(state: SortState) {
        ParallelQuicksort(state.dispatcher).sort(state.array)
    }
}
