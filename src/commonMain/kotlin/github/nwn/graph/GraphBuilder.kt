package github.nwn.graph

import kotlin.math.max

/**
 * Builder for producing [Graph].
 */
@GraphDSL
class GraphBuilder<State, Input> internal constructor() {
    private val nodes = HashMap<NodeReference, GraphNode<State, Input>>()

    private var index = 0

    /**
     * Creates a new node for [Graph].
     * @param id The [NodeReference] to use to identify the node. If not provided creates a new one and appends it. These should be unique.
     * @return The [id] of the node. If an id was provided, simply returns the provided id.
     */
    fun node(id: NodeReference = nodeReference(), op: GraphNodeBuilder<State, Input>.() -> Unit): NodeReference {
        val node = GraphNodeBuilder<State, Input>(id).apply(op)
        nodes[id] = node.build()
        return id
    }

    /**
     * Utility method for creating a terminal node with no functionality
     */
    fun terminalNode(id: NodeReference = nodeReference(), op: GraphNodeBuilder<State, Input>.() -> Unit): NodeReference {
        val node = GraphNodeBuilder<State, Input>(id).apply {
            step { id }
            op()
            terminal()
        }
        nodes[id] = node.build()
        return id
    }

    /**
     * Creates a new [NodeReference].
     */
    fun nodeReference() = NodeReference(index++)

    internal fun build(): Graph<State, Input> {
        val nodes = nodes.entries.sortedBy { it.key }.map { it.value }
        return Graph(nodes, max(nodes.indexOfFirst { it.initial }, 0))
    }

}