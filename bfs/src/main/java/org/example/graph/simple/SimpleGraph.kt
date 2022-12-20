package org.example.graph.simple

import org.example.graph.Graph
import org.example.graph.Node

class SimpleGraph(
    private val graph: Map<Int, Set<Int>>,
) : Graph<SimpleGraph.SimpleNode> {

    init {
        val size = graph.size
        check((0 until size).toSet() == graph.keys) {
            "Node numbers must be sequential, but missing node detected"
        }
        graph.values.forEach { children ->
            check(children.all { it in 0 until size }) {
                "Edge to non-existing node detected"
            }
        }
    }

    override val size: Int = graph.size

    override val startNode: SimpleNode = SimpleNode(0)

    override fun nodeByNumber(nodeNumber: Int): SimpleNode = SimpleNode(0)

    override fun forEachNeighbour(nodeNumber: Int, action: (next: Int) -> Unit) =
        graph.getValue(nodeNumber).forEach(action)

    data class SimpleNode(
        override val number: Int,
    ) : Node
}
