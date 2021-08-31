package github.nwn.graph


internal val UNINITIALIZED_NODEREFERENCE = NodeReference(-1)
data class GraphState<State>(
    val state: State,
    internal var current: NodeReference = UNINITIALIZED_NODEREFERENCE,
    internal var last: NodeReference = UNINITIALIZED_NODEREFERENCE
)