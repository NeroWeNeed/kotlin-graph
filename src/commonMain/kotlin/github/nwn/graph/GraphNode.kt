package github.nwn.graph

data class GraphNode<State, Input> internal constructor(
    val id: NodeReference,
    private val enter: (GraphNode<State, Input>.(State,Input) -> Unit)?,
    private val step: GraphNode<State, Input>.(State, Input) -> NodeReference,
    private val exit: (GraphNode<State, Input>.(State,Input) -> Unit)?,
    val shouldTerminate: Boolean,
    val initial: Boolean
) : Comparable<GraphNode<State, Input>> {

    fun enter(state: State, input: Input) = enter?.invoke(this, state, input) ?: Unit
    fun exit(state: State, input: Input) = exit?.invoke(this, state, input) ?: Unit
    fun step(graph: Graph<State, Input>, state: State, input: Input): GraphNode<State, Input> =
        graph.nodes[step.invoke(this, state, input).index]

    override fun compareTo(other: GraphNode<State, Input>): Int {
        return id.index.compareTo(other.id.index)
    }
}