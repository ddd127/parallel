package org.example.bfs

import kotlinx.coroutines.asCoroutineDispatcher
import org.example.bfs.parallel.ParallelBfs
import org.example.bfs.sequential.SequentialBfs
import org.example.graph.Graph
import org.example.graph.Node
import java.util.concurrent.Executors

interface Bfs {

    fun <NODE : Node, GRAPH : Graph<NODE>> fillDistances(
        graph: GRAPH,
        distances: IntArray,
        from: NODE = graph.startNode,
    )

    enum class Type {
        SEQUENTIAL {
            override fun withBfs(block: (Bfs) -> Unit) {
                block(SequentialBfs)
            }
        },
        PARALLEL {
            override fun withBfs(block: (Bfs) -> Unit) {
                val pool = Executors.newFixedThreadPool(4)
                val bfs = ParallelBfs(pool.asCoroutineDispatcher())
                block(bfs)
                pool.shutdownNow()
            }
        }
        ;

        abstract fun withBfs(block: (Bfs) -> Unit)
    }
}
