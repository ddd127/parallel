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
        var neighbours = IntArray(10)
        while (true) {
            val current = queue.removeFirstOrNull() ?: break
            val neighboursCount = graph.neighboursCount(current)
            if (neighboursCount > neighbours.size) {
                neighbours = IntArray(neighboursCount)
            }
            graph.writeNeighbours(current, neighbours)
            for (i in 0 until neighboursCount) {
                val next = neighbours[i]
                if (distances[next] == -1) {
                    distances[next] = distances[current] + 1
                    queue.addLast(next)
                }
            }
        }
    }
}
