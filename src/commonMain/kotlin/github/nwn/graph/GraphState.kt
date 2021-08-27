package github.nwn.graph

internal data class GraphState<State>(
    val state: State,
    var current: Int = 0,
    var last: Int = 0
)