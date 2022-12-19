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
            val neighbours = graph.getNeighbours(current)
            for (next in neighbours) {
                if (distances[next] != -1) {
                    continue
                }
                distances[next] = distances[current] + 1
                queue.addLast(next)
            }
        }
    }
}
