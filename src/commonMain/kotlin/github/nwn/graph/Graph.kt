package github.nwn.graph


class Graph<State, Input> internal constructor(
    internal val nodes: List<GraphNode<State, Input>>,
    private val initialNodeIndex: Int = 0
) {

    private fun initGraphState(state: State) = GraphState(state, initialNodeIndex, initialNodeIndex)

    private fun invoke(input: Input, state: State): GraphState<State> =
        invoke(input, initGraphState(state))

    private fun invoke(input: Input, state: GraphState<State>): GraphState<State> {

        if (state.last != state.current) {
            nodes[state.current].enter(state.state)

        }
        state.last = state.current
        state.current = nodes[state.current].step(this, state.state, input).id.index
        if (state.last != state.current) {
            nodes[state.last].exit(state.state)
        }
        return state
    }

    fun process(input: Input, state: State): State {
        var graphState: GraphState<State> = initGraphState(state)
        while (!nodes[graphState.current].shouldTerminate) {
            graphState = invoke(input, graphState)
        }
        return graphState.state
    }

    fun processUntil(input: Input, state: State, condition: (State) -> Boolean): State {
        var graphState: GraphState<State> = initGraphState(state)
        while (!nodes[graphState.current].shouldTerminate && condition(graphState.state)) {
            graphState = invoke(input, graphState)
        }
        return graphState.state
    }


}


fun <State, Input> graph(op: GraphBuilder<State, Input>.() -> Unit) =
    GraphBuilder<State, Input>().apply(op).build()


