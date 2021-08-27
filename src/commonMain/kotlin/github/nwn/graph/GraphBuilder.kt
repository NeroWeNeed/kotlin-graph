package github.nwn.graph

import kotlin.math.max

class GraphBuilder<State, Input> {
    private val nodes = HashMap<NodeReference, GraphNode<State, Input>>()

    private var index = 0
    fun node(id: NodeReference, op: GraphNodeBuilder<State, Input>.() -> Unit): Int {
        val node = GraphNodeBuilder<State, Input>().apply(op)

        nodes[id] = node.build(id)
        return id.index
    }

    fun nodeReference() = NodeReference(index++)
    fun node(op: GraphNodeBuilder<State, Input>.() -> Unit): NodeReference {
        val node = GraphNodeBuilder<State, Input>().apply(op)
        val id = nodeReference()
        nodes[id] = node.build(id)
        return id
    }

    fun build(): Graph<State, Input> {
        val nodes = nodes.entries.sortedBy { it.key }.map { it.value }
        return Graph(nodes, max(nodes.indexOfFirst { it.initial }, 0))
    }

}