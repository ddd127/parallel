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
        var currentLayer = intArrayOf(from.number)
        val nextLayerSize = AtomicInteger(0)
        var iteration = 0
        while (true) {
            ++iteration
            val blockSize = chooseBlockSize(currentLayer.size)
            val nextLayerInfo = IntArray(currentLayer.size) { 0 }
            calcNextLayerInfo(graph, currentLayer, nextLayerInfo, nextLayerSize, blockSize)

            if (nextLayerSize.get() == 0) {
                break
            }

            val nextLayer = IntArray(nextLayerSize.get()) { -1 }
            doStep(graph, currentLayer, nextLayerInfo, nextLayer, distances, iteration, blockSize)
            currentLayer = nextLayer
        }
    }

    private suspend fun doStep(
        graph: Graph<*>,
        currentLayer: IntArray,
        nextInfo: IntArray,
        nextLayer: IntArray,
        distances: IntArray,
        iteration: Int,
        blockSize: Int,
    ): Unit = coroutineScope {
        (currentLayer.indices step blockSize).map { left ->
            launch {
                val right = minOf(currentLayer.size, left + blockSize)
                doBlockStep(graph, distances, currentLayer, nextInfo, nextLayer, left, right, iteration)
            }
        }.joinAll()
    }

    private fun doBlockStep(
        graph: Graph<*>,
        distances: IntArray,
        currentLayer: IntArray,
        nextInfo: IntArray,
        nextLayer: IntArray,
        left: Int,
        right: Int,
        iteration: Int,
    ) {
        for (i in left until right) {
            val current = currentLayer[i]
            if (current == -1) continue
            var startIndex = nextInfo[i]
            graph.forEachNeighbour(current) { next ->
                if (varHandle.compareAndSet(distances, next, -1, iteration)) {
                    nextLayer[startIndex++] = next
                }
            }
        }
    }

    private suspend fun calcNextLayerInfo(
        graph: Graph<*>,
        layer: IntArray,
        info: IntArray,
        size: AtomicInteger,
        blockSize: Int,
    ): Unit = coroutineScope {
        size.set(0)
        (layer.indices step blockSize).map { left ->
            launch {
                val right = minOf(layer.size, left + blockSize)
                calcNeighbours(graph, layer, info, size, left, right)
            }
        }.joinAll()
        sequentialScan(info)
    }

    private fun calcNeighbours(
        graph: Graph<*>,
        layer: IntArray,
        info: IntArray,
        size: AtomicInteger,
        left: Int,
        right: Int,
    ) {
        for (index in left until right) {
            val node = layer[index]
            if (node == -1) {
                continue
            }
            var count = 0
            graph.forEachNeighbour(node) {
                ++count
            }
            if (index != info.size - 1) {
                info[index + 1] = count
            }
            size.addAndGet(count)
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
        } while (result < averageBlockSize)
        return result shr 1
    }

    companion object {
        private const val CORE_COUNT = 4
        private const val MULTIPLIER = 16

        val varHandle: VarHandle = MethodHandles.arrayElementVarHandle(IntArray::class.java)
    }
}
