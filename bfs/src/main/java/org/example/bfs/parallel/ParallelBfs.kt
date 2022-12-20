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
        (0 until (currentLayer.size + blockSize - 1) / blockSize).map { block ->
            launch {
                doBlockStep(graph, distances, currentLayer, nextInfo, nextLayer, block, blockSize, iteration)
            }
        }.joinAll()
    }

    private fun doBlockStep(
        graph: Graph<*>,
        distances: IntArray,
        currentLayer: IntArray,
        nextInfo: IntArray,
        nextLayer: IntArray,
        block: Int,
        blockSize: Int,
        iteration: Int,
    ) {
        val left = block * blockSize
        val right = minOf(currentLayer.size, (block + 1) * blockSize)
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
        (0 until (layer.size + blockSize - 1) / blockSize).map { block ->
            launch {
                calcSums(graph, layer, info, size, block, blockSize)
            }
        }.joinAll()
        exclusiveScan(info, blockSize)
    }

    private fun calcSums(
        graph: Graph<*>,
        layer: IntArray,
        info: IntArray,
        size: AtomicInteger,
        block: Int,
        blockSize: Int,
    ) {
        val left = block * blockSize
        val right = minOf(layer.size, (block + 1) * blockSize)
        for (index in left until right) {
            val node = layer[index]
            if (node == -1) {
                continue
            }
            var count = 0
            graph.forEachNeighbour(node) {
                ++count
            }
            info[index] = count
            size.addAndGet(count)
        }
    }

    private suspend fun exclusiveScan(array: IntArray, blockSize: Int) {
        val blockedSize = (array.size + blockSize - 1) / blockSize
        val tree = createTree(blockedSize)
        buildTree(array, blockSize, tree, 0, 0, array.size)
        propagateLeft(array, blockSize, tree, 0, 0, array.size, 0)
    }

    private suspend fun propagateLeft(
        array: IntArray,
        blockSize: Int,
        tree: IntArray,
        treeIndex: Int,
        left: Int,
        right: Int,
        leftValue: Int,
    ): Unit = coroutineScope {
        if (right - left <= blockSize) {
            var prev = array[left]
            array[left] = leftValue
            for (i in left + 1 until right) {
                val cur = array[i]
                array[i] = array[i - 1] + prev
                prev = cur
            }
            return@coroutineScope
        }
        val med = (right + left) / 2
        val l = launch {
            propagateLeft(array, blockSize, tree, treeIndex * 2 + 1, left, med, leftValue)
        }
        val r = launch {
            propagateLeft(
                array,
                blockSize,
                tree,
                treeIndex * 2 + 2,
                med,
                right,
                leftValue + tree[treeIndex * 2 + 1]
            )
        }
        joinAll(l, r)
    }

    private suspend fun buildTree(
        array: IntArray,
        blockSize: Int,
        tree: IntArray,
        treeIndex: Int,
        left: Int,
        right: Int,
    ): Unit = coroutineScope {
        if (right - left <= blockSize) {
            var sum = 0
            for (i in left until right) {
                sum += array[i]
            }
            tree[treeIndex] = sum
            return@coroutineScope
        }
        val med = (right + left) / 2
        val l = launch {
            buildTree(array, blockSize, tree, treeIndex * 2 + 1, left, med)
        }
        val r = launch {
            buildTree(array, blockSize, tree, treeIndex * 2 + 2, med, right)
        }
        joinAll(l, r)
        tree[treeIndex] = tree[treeIndex * 2 + 1] + tree[treeIndex * 2 + 2]
    }

    private fun createTree(originalSize: Int): IntArray {
        var count = 2
        var result = originalSize
        while (result != 1) {
            result = result shr 1
            ++count
        }
        while (count != 0) {
            result = result shl 1
            --count
        }
        return IntArray(result)
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
        private const val MULTIPLIER = 4

        val varHandle: VarHandle = MethodHandles.arrayElementVarHandle(IntArray::class.java)
    }
}
