package org.example.graph

interface Graph<out T : Node> {

    val size: Int
    val startNode: T

    fun nodeByNumber(nodeNumber: Int) : T

    fun forEachNeighbour(nodeNumber: Int, action: (next: Int) -> Unit)
}
