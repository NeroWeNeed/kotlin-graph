package github.nwn.graph

/**
 * Represents a State Machine. This class should not be instantiated directly, and instead should be created with the [graph] function.
 * @see graph
 */
class Graph<State, Input> internal constructor(
    internal val nodes: List<GraphNode<State, Input>>,
    initialNodeIndex: Int = 0
) {
    /**
     * The initial node that any processing starts on. If specified in the builder, will be whatever node was specified, otherwise will be the first node.
     */
    val initialNode = NodeReference(initialNodeIndex)

    /**
     * Wraps a [State] into a [GraphState] with initial parameters.
     */
    fun createInitialGraphState(state: State) = GraphState(state, initialNode, initialNode)

    private fun invoke(input: Input, graphState: GraphState<State>): GraphState<State> {
        val node = nodes[graphState.current.index]
        val scope = GraphNodeScope(node.id, node.shouldTerminate, node.initial, graphState.state, input)
        if (graphState.last != graphState.current) {
            node.enter?.invoke(scope)
        }
        graphState.last = graphState.current
        graphState.current = node.step.invoke(scope)
        if (graphState.last != graphState.current) {
            node.exit?.invoke(scope)
        }
        return graphState
    }

    /**
     * Processes an [input] with a given [state] against the graph until it reaches a terminal node.
     * The terminal node will NOT be processed. It is advised if you have any behaviour that needs to be run before the graph is done processing to create an intermediate node that directs to the terminal node.
     * @param input The input to be processed. Generally speaking should be immutable.
     * @param state The initial state. Generally speaking should be mutable.
     * @return the [state] parameter after being processed.
     */
    fun process(input: Input, state: State): State {
        var graphState: GraphState<State> = createInitialGraphState(state)
        while (!nodes[graphState.current.index].shouldTerminate) {
            graphState = invoke(input, graphState)
        }
        return graphState.state
    }

    /**
     * Processes an [input] with a given [state] against the graph until it reaches a terminal node or an external [condition] is not satisfied.
     * The terminal node will NOT be processed and once the [condition] returns false, the current node will not be processed either.. It is advised if you have any behaviour that needs to be run before the graph is done processing to create an intermediate node that directs to the terminal node.
     * @param input The input to be processed. Generally speaking should be immutable.
     * @param state The initial state. Generally speaking should be mutable.
     * @param condition An external condition. The graph will run until it reaches a terminal node or this parameter returns false against the state.
     * @return the [state] parameter after being processed.
     */

    fun processUntil(input: Input, state: State, condition: (State) -> Boolean): State {
        var graphState: GraphState<State> = createInitialGraphState(state)
        while (!nodes[graphState.current.index].shouldTerminate && condition(graphState.state)) {
            graphState = invoke(input, graphState)
        }
        return graphState.state
    }

    /**
     * Processes a single step of the graph with the given input. This is useful for any manual processing that isn't already provided.
     * @param input The input to be processed. Generally speaking should be immutable.
     * @param state The initial state. Generally speaking should be mutable.
     * @return The updated [GraphState], which holds a copy of the updated [state].
     */

    fun processStep(input: Input, state: State): GraphState<State> {
        return processStep(input,createInitialGraphState(state))
    }
    /**
     * Processes a single step of the graph with the given input. This is useful for any manual processing that isn't already provided.
     * @param input The input to be processed. Generally speaking should be immutable.
     * @param state The state returned by [processStep]'s [State] overload.
     * @return The updated [GraphState], which holds a copy of the updated [state].
     */
    fun processStep(input: Input, state: GraphState<State>): GraphState<State> {
        return invoke(input, state)
    }
}

/**
 * Creates an instance of [Graph] which can be used for state machine like behaviour.
 */
fun <State, Input> graph(op: GraphBuilder<State, Input>.() -> Unit) =
    GraphBuilder<State, Input>().apply(op).build()