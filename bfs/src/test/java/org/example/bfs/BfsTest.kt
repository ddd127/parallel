package org.example.bfs

import org.example.graph.Graph
import org.example.graph.Node
import org.example.graph.cube.CubeGraph
import org.example.graph.simple.SimpleGraph
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BfsTest {

    @ParameterizedTest
    @EnumSource(Bfs.Type::class)
    fun test(type: Bfs.Type) {
        type.withBfs { bfs ->
            testPreDefined0(bfs)
            testPreDefined1(bfs)
            testPreDefined2(bfs)
            testPreDefined3(bfs)
            testPreDefined4(bfs)
            testPreDefined5(bfs)
            testCube(bfs)
        }
    }

    private fun testCube(bfs: Bfs) {
        for (size in 1..101 step 5) {
            val graph = CubeGraph(size)
            val array = IntArray(size * size * size) { -1 }
            bfs.fillDistances(graph, array)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    for (z in 0 until size) {
                        val number = x * size * size + y * size + z
                        assertEquals(array[number], x + y + z)
                    }
                }
            }
        }
    }

    private fun testPreDefined0(bfs: Bfs) {
        val map = mapOf(
            0 to setOf<Int>(),
        )
        val expected = intArrayOf(0)
        val graph: Graph<Node> = SimpleGraph(map)
        val actual = IntArray(map.size) { -1 }
        bfs.fillDistances(graph, actual)
        assertContentEquals(expected, actual)
    }

    private fun testPreDefined1(bfs: Bfs) {
        val map = mapOf(
            0 to setOf(1, 2),
            1 to setOf(),
            2 to setOf(3),
            3 to setOf(4),
            4 to setOf(),
        )
        val expected = intArrayOf(0, 1, 1, 2, 3)
        val graph: Graph<Node> = SimpleGraph(map)
        val actual = IntArray(map.size) { -1 }
        bfs.fillDistances(graph, actual)
        assertContentEquals(expected, actual)
    }

    private fun testPreDefined2(bfs: Bfs) {
        val map = mapOf(
            0 to setOf(1, 2),
            1 to setOf(3),
            2 to setOf(4),
            3 to setOf(4),
            4 to setOf(3),
        )
        val expected = intArrayOf(0, 1, 1, 2, 2)
        val graph: Graph<Node> = SimpleGraph(map)
        val actual = IntArray(map.size) { -1 }
        bfs.fillDistances(graph, actual)
        assertContentEquals(expected, actual)
    }

    private fun testPreDefined3(bfs: Bfs) {
        val map = mapOf(
            0 to setOf(1, 2, 3, 4),
            1 to setOf(0, 2, 3, 4),
            2 to setOf(0, 1, 3, 4),
            3 to setOf(0, 1, 2, 4),
            4 to setOf(0, 1, 2, 3),
        )
        val expected = intArrayOf(0, 1, 1, 1, 1)
        val graph: Graph<Node> = SimpleGraph(map)
        val actual = IntArray(map.size) { -1 }
        bfs.fillDistances(graph, actual)
        assertContentEquals(expected, actual)
    }

    private fun testPreDefined4(bfs: Bfs) {
        val map = mapOf(
            0 to setOf(4, 1),
            1 to setOf(0, 2),
            2 to setOf(1, 3),
            3 to setOf(2, 4),
            4 to setOf(3, 0),
        )
        val expected = intArrayOf(0, 1, 2, 2, 1)
        val graph: Graph<Node> = SimpleGraph(map)
        val actual = IntArray(map.size) { -1 }
        bfs.fillDistances(graph, actual)
        assertContentEquals(expected, actual)
    }

    private fun testPreDefined5(bfs: Bfs) {
        val map = mapOf(
            0 to setOf(2, 3),
            1 to setOf(3, 4),
            2 to setOf(4, 0),
            3 to setOf(0, 1),
            4 to setOf(1, 2),
        )
        val expected = intArrayOf(0, 2, 1, 1, 2)
        val graph: Graph<Node> = SimpleGraph(map)
        val actual = IntArray(map.size) { -1 }
        bfs.fillDistances(graph, actual)
        assertContentEquals(expected, actual)
    }
}
