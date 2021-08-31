package github.nwn.graph

data class GraphNode<State, Input> internal constructor(
    val id: NodeReference,
    internal val enter: (GraphNodeScope<State, Input>.() -> Unit)?,
    internal val step: GraphNodeScope<State, Input>.() -> NodeReference,
    internal val exit: (GraphNodeScope<State, Input>.() -> Unit)?,
    val shouldTerminate: Boolean,
    val initial: Boolean
) : Comparable<GraphNode<State, Input>> {
    override fun compareTo(other: GraphNode<State, Input>): Int {
        return id.index.compareTo(other.id.index)
    }
}

