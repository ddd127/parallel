package org.example.quicksort.benchmark

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.example.bfs.parallel.ParallelBfs
import org.example.bfs.sequential.SequentialBfs
import org.example.graph.Graph
import org.example.graph.Node
import org.example.graph.cube.CubeGraph
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

open class BfsBenchmark {

    @State(Scope.Benchmark)
    open class SortState {
        private val size = System.getProperty("bfs.benchmark.graph.size").toInt()
        val graph: Graph<Node> = CubeGraph(size)
        val array = IntArray(size * size * size)

        private val executor = Executors.newFixedThreadPool(4)
        val dispatcher: CoroutineDispatcher = executor.asCoroutineDispatcher()

        @Setup(Level.Invocation)
        fun setUp() {
            System.gc()
            array.fill(-1)
        }

        @TearDown(Level.Trial)
        fun tearDown() {
            executor.shutdownNow()
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1, warmups = 0)
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    fun sequentialBfs(state: SortState, blackHole: Blackhole) {
        SequentialBfs.fillDistances(state.graph, state.array)
        blackHole.consume(state.array)
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1, warmups = 0)
    @Warmup(iterations = 3, time = 5, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
    fun parallelBfs(state: SortState, blackHole: Blackhole) {
        ParallelBfs(state.dispatcher).fillDistances(state.graph, state.array)
        blackHole.consume(state.array)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val options = OptionsBuilder()
                .include("parallelBfs")
                .forks(1)
                .build()
            Runner(options).run()
        }
    }
}
