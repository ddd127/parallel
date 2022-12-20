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
                Executors.newFixedThreadPool(4).use {
                    block(ParallelBfs(it.asCoroutineDispatcher()))
                }
            }
        }
        ;

        abstract fun withBfs(block: (Bfs) -> Unit)
    }
}
