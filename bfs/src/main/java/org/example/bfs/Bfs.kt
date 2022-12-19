package org.example.bfs

import org.example.bfs.sequential.SequentialBfs
import org.example.graph.Graph
import org.example.graph.Node

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
        ;

        abstract fun withBfs(block: (Bfs) -> Unit)
    }
}
