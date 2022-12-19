package org.example.quicksort.benchmark

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.example.bfs.sequential.SequentialBfs
import org.example.graph.Graph
import org.example.graph.Node
import org.example.graph.cube.CubeGraph
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
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

        @TearDown(Level.Trial)
        fun tearDown() {
            executor.shutdownNow()
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    fun sequentialBfs(state: SortState) {
        SequentialBfs.fillDistances(state.graph, state.array)
    }
}
