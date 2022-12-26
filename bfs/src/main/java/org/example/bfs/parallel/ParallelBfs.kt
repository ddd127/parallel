package org.example.bfs.parallel

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.example.bfs.Bfs
import org.example.graph.Graph
import org.example.graph.Node
import java.lang.invoke.MethodHandles
import java.lang.invoke.VarHandle
import java.util.concurrent.atomic.AtomicInteger

class ParallelBfs(
    private val dispatcher: CoroutineDispatcher,
) : Bfs {

    override fun <NODE : Node, GRAPH : Graph<NODE>> fillDistances(
        graph: GRAPH,
        distances: IntArray,
        from: NODE,
    ) {
        runBlocking {
            withContext(dispatcher) {
                suspendFillDistances(graph, distances, from)
            }
        }
    }

    private suspend fun <NODE : Node, GRAPH : Graph<NODE>> suspendFillDistances(
        graph: GRAPH,
        distances: IntArray,
        from: NODE,
    ): Unit = coroutineScope {
        distances[from.number] = 0

        var current = StepInfo(
            intArrayOf(from.number),
            IntArray(1) { 0 },
            AtomicInteger(graph.neighboursCount(from.number)),
        )

        var sum = current.neighbourSum.get()
        var iteration = 0

        while (sum != 0) {
            ++iteration
            val blockSize = chooseBlockSize(current.layer.size)
            val next = StepInfo(
                IntArray(sum) { -1 },
                IntArray(sum) { 0 },
                AtomicInteger(0),
            )
            doStep(graph, current, iteration, next, distances, blockSize)
            current = next
            sum = current.neighbourSum.get()
        }
    }

    private suspend fun doStep(
        graph: Graph<*>,
        current: StepInfo,
        iteration: Int,
        next: StepInfo,
        distances: IntArray,
        blockSize: Int,
    ): Unit = coroutineScope {
        (current.layer.indices step blockSize).map { left ->
            launch {
                val right = minOf(current.layer.size, left + blockSize)
                doBlockStep(graph, distances, iteration, current, next, left, right)
            }
        }.joinAll()
        sequentialScan(next.neighbourPrefix)
    }

    private fun doBlockStep(
        graph: Graph<*>,
        distances: IntArray,
        iteration: Int,
        current: StepInfo,
        next: StepInfo,
        left: Int,
        right: Int,
    ) {
        for (i in left until right) {
            val currentNode = current.layer[i]
            if (currentNode == -1) continue

            val nextLeft = current.neighbourPrefix[i]
            val nextRight = graph.writeNeighbours(currentNode, next.layer, nextLeft)

            graph.writeNeighbours(currentNode, next.layer, nextLeft)
            for (nextNodeIndex in nextLeft until nextRight) {
                val nextNode = next.layer[nextNodeIndex]
                if (!varHandle.compareAndSet(distances, nextNode, -1, iteration)) {
                    next.layer[nextNodeIndex] = -1
                    continue
                }
                val count = graph.neighboursCount(nextNode)
                next.neighbourSum.addAndGet(count)
                if (nextNodeIndex != next.neighbourPrefix.size - 1) {
                    next.neighbourPrefix[nextNodeIndex + 1] = count
                }
            }
        }
    }

    private fun sequentialScan(array: IntArray) {
        for (i in 1 until array.size) {
            array[i] += array[i - 1]
        }
    }

    private fun chooseBlockSize(arraySize: Int): Int {
        val averageBlocksCount = CORE_COUNT * MULTIPLIER
        val averageBlockSize = (arraySize + averageBlocksCount - 1) / averageBlocksCount
        var result = 1
        do {
            result = result shl 1
        } while (result <= averageBlockSize)
        return result shr 1
    }

    class StepInfo(
        val layer: IntArray,
        val neighbourPrefix: IntArray,
        val neighbourSum: AtomicInteger,
    )

    companion object {
        private const val CORE_COUNT = 4
        private const val MULTIPLIER = 64

        val varHandle: VarHandle = MethodHandles.arrayElementVarHandle(IntArray::class.java)
    }
}
