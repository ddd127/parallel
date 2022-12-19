package org.example.graph.cube

import org.example.graph.Graph
import org.example.graph.Node

class CubeGraph(
    val size: Int,
) : Graph<CubeGraph.CubeNode> {

    init {
        check(size > 0) {
            "Expected positive size, but got $size"
        }
    }

    override val startNode: CubeNode = CubeNode(0, 0, 0)

    override fun nodeByNumber(nodeNumber: Int): CubeNode {
        val x = nodeNumber / (size * size)
        val y = (nodeNumber - x * size * size) / size
        val z = nodeNumber - x * size * size - y * size
        return CubeNode(x, y, z)
    }

    override fun getNeighbours(nodeNumber: Int): Set<Int> {
        val node = nodeByNumber(nodeNumber)
        return listOf(
            CubeNode(node.x - 1, node.y, node.z),
            CubeNode(node.x + 1, node.y, node.z),
            CubeNode(node.x, node.y - 1, node.z),
            CubeNode(node.x, node.y + 1, node.z),
            CubeNode(node.x, node.y, node.z - 1),
            CubeNode(node.x, node.y, node.z + 1),
        ).filter(::isValid).map { it.number }.toSet()
    }

    private fun isValid(node: CubeNode) =
        node.x in 0 until size &&
                node.y in 0 until size &&
                node.z in 0 until size

    inner class CubeNode(
        val x: Int,
        val y: Int,
        val z: Int,
    ) : Node {

        override val number: Int =
            x * this@CubeGraph.size * this@CubeGraph.size + y * this@CubeGraph.size + z

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
//
//    companion object {
//
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val size = 10
//            val graph = CubeGraph(size)
//            for (x in 0 until size) {
//                for (y in 0 until size) {
//                    for (z in 0 until size) {
//                        val expected = graph.CubeNode(x, y, z)
//                        val actual = graph.nodeByNumber(expected.number)
//                        check(expected == actual)
//                    }
//                }
//            }
//        }
//    }
}
