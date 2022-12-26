package org.example.graph.cube

import org.example.graph.Graph
import org.example.graph.Node

class CubeGraph(
    override val size: Int,
) : Graph<CubeGraph.CubeNode> {

    init {
        check(size > 0) {
            "Expected positive size, but got $size"
        }
    }

    override val startNode: CubeNode = CubeNode(0, 0, 0)

    override fun neighboursCount(nodeNumber: Int): Int {
        var result = 0
        val x = nodeNumber / (size * size)
        val y = (nodeNumber - x * size * size) / size
        val z = nodeNumber - x * size * size - y * size
        if (isValid(x - 1, y, z)) {
            ++result
        }
        if (isValid(x + 1, y, z)) {
            ++result
        }
        if (isValid(x, y - 1, z)) {
            ++result
        }
        if (isValid(x, y + 1, z)) {
            ++result
        }
        if (isValid(x, y, z - 1)) {
            ++result
        }
        if (isValid(x, y, z + 1)) {
            ++result
        }
        return result
    }

    override fun writeNeighbours(nodeNumber: Int, targetArray: IntArray, startIndex: Int): Int {
        var index = startIndex
        val x = nodeNumber / (size * size)
        val y = (nodeNumber - x * size * size) / size
        val z = nodeNumber - x * size * size - y * size
        if (isValid(x - 1, y, z)) {
            targetArray[index++] = number(x - 1, y, z)
        }
        if (isValid(x + 1, y, z)) {
            targetArray[index++] = number(x + 1, y, z)
        }
        if (isValid(x, y - 1, z)) {
            targetArray[index++] = number(x, y - 1, z)
        }
        if (isValid(x, y + 1, z)) {
            targetArray[index++] = number(x, y + 1, z)
        }
        if (isValid(x, y, z - 1)) {
            targetArray[index++] = number(x, y, z - 1)
        }
        if (isValid(x, y, z + 1)) {
            targetArray[index++] = number(x, y, z + 1)
        }
        return index
    }

    private fun isValid(x: Int, y: Int, z: Int) =
        x in 0 until size &&
                y in 0 until size &&
                z in 0 until size

    inner class CubeNode(
        val x: Int,
        val y: Int,
        val z: Int,
    ) : Node {

        override val number: Int = number(x, y, z)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CubeNode

            if (x != other.x) return false
            if (y != other.y) return false
            if (z != other.z) return false

            return true
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            result = 31 * result + z
            return result
        }
    }

    private fun number(x: Int, y: Int, z: Int) =
        x * size * size + y * size + z
}
