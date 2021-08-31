package github.nwn.graph

/**
 * Represents a State Machine. This class should not be instantiated directly, and instead should be created with the [graph] function.
 * @see graph
 */
class Graph<State, Input> internal constructor(
    internal val nodes: List<GraphNode<State, Input>>,
    initialNodeIndex: Int = 0
) {
    private val initialNode = NodeReference(initialNodeIndex)
    private fun initGraphState(state: State) = GraphState(state, initialNode, initialNode)

    private fun invoke(input: Input, state: GraphState<State>): GraphState<State> {

        if (state.last != state.current) {
            nodes[state.current.index].enter(state.state,input)

        }
        state.last = state.current
        state.current = nodes[state.current.index].step(this, state.state, input).id
        if (state.last != state.current) {
            nodes[state.last.index].exit(state.state,input)
        }
        return state
    }

    /**
     * Processes an [input] with a given [state] against the graph until it reaches a terminal node.
     * @param input The input to be processed. Generally speaking should be immutable.
     * @param state The initial state. Generally speaking should be mutable.
     * @return the [state] parameter after being processed.
     */
    fun process(input: Input, state: State): State {
        var graphState: GraphState<State> = initGraphState(state)
        while (!nodes[graphState.current.index].shouldTerminate) {
            graphState = invoke(input, graphState)
        }
        return graphState.state
    }

    /**
     * Processes an [input] with a given [state] against the graph until it reaches a terminal node or an external [condition] is not satisfied.
     * @param input The input to be processed. Generally speaking should be immutable.
     * @param state The initial state. Generally speaking should be mutable.
     * @param condition An external condition. The graph will run until it reaches a terminal node or this parameter returns false against the state.
     * @return the [state] parameter after being processed.
     */

    fun processUntil(input: Input, state: State, condition: (State) -> Boolean): State {
        var graphState: GraphState<State> = initGraphState(state)
        while (!nodes[graphState.current.index].shouldTerminate && condition(graphState.state)) {
            graphState = invoke(input, graphState)
        }
        return graphState.state
    }

    fun processStep(input: Input, state: State): GraphState<State> {
        var graphState: GraphState<State> = initGraphState(state)
        graphState = invoke(input, graphState)
        return graphState
    }
}

/**
 * Creates an instance of [Graph] which can be used for state machine like behaviour.
 */
fun <State, Input> graph(op: GraphBuilder<State, Input>.() -> Unit) =
    GraphBuilder<State, Input>().apply(op).build()