package org.example.graph

interface Graph<out T : Node> {

    val size: Int
    val startNode: T

    fun neighboursCount(nodeNumber: Int): Int

    fun writeNeighbours(nodeNumber: Int, targetArray: IntArray, startIndex: Int = 0): Int
}
