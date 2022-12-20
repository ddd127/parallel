package org.example.bfs.sequential

import org.example.bfs.Bfs
import org.example.graph.Graph
import org.example.graph.Node

object SequentialBfs : Bfs {

    override fun <NODE : Node, GRAPH : Graph<NODE>> fillDistances(
        graph: GRAPH,
        distances: IntArray,
        from: NODE,
    ) {
        distances.fill(-1)
        val queue = ArrayDeque<Int>()
        queue.addLast(from.number)
        distances[from.number] = 0
        while (true) {
            val current = queue.removeFirstOrNull() ?: break
            graph.forEachNeighbour(current) { next ->
                if (distances[next] == -1) {
                    distances[next] = distances[current] + 1
                    queue.addLast(next)
                }
            }
        }
    }
}
