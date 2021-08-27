package github.nwn.graph

data class GraphNode<State, Input> internal constructor(
    val id: NodeReference,
    private val enter: (GraphNode<State, Input>.(State) -> Unit)?,
    private val step: GraphNode<State, Input>.(State, Input) -> NodeReference,
    private val exit: (GraphNode<State, Input>.(State) -> Unit)?,
    val shouldTerminate: Boolean,
    val initial: Boolean
) : Comparable<GraphNode<State, Input>> {

    fun enter(state: State) = enter?.invoke(this, state) ?: Unit
    fun exit(state: State) = exit?.invoke(this, state) ?: Unit
    fun step(graph: Graph<State, Input>, state: State, input: Input): GraphNode<State, Input> =
        graph.nodes[step.invoke(this, state, input).index]

    override fun compareTo(other: GraphNode<State, Input>): Int {
        return id.index.compareTo(other.id.index)
    }
}