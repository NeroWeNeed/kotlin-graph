package org.nwn.util.graph

import kotlin.jvm.JvmInline
import kotlin.math.max


class Graph<State, Input>(val nodes: List<Node<State, Input>>, val initialNodeIndex: Int = 0) {

    fun initGraphState(state: State) = GraphState(state, initialNodeIndex, initialNodeIndex)

    fun invoke(input: Input, state: State): GraphState<State> =
        invoke(input, initGraphState(state))

    fun invoke(input: Input, state: GraphState<State>): GraphState<State> {

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
    fun processUntil(input: Input, state: State,condition: (State) -> Boolean): State {
        var graphState: GraphState<State> = initGraphState(state)
        while (!nodes[graphState.current].shouldTerminate && condition(graphState.state)) {
            graphState = invoke(input, graphState)
        }
        return graphState.state
    }


    data class Node<State, Input>(
        val id: NodeReference,
        private val enter: (Node<State, Input>.(State) -> Unit)?,
        private val step: Node<State, Input>.(State, Input) -> NodeReference,
        private val exit: (Node<State, Input>.(State) -> Unit)?,
        val shouldTerminate: Boolean,
        val initial: Boolean
    ) : Comparable<Node<State, Input>> {

        fun enter(state: State) = enter?.invoke(this, state) ?: Unit
        fun exit(state: State) = exit?.invoke(this, state) ?: Unit
        fun step(graph: Graph<State, Input>, state: State, input: Input): Node<State, Input> =
            graph.nodes[step.invoke(this, state, input).index]

        class Builder<State, Input> {
            private var enter: (Node<State, Input>.(State) -> Unit)? = null
            private var step: (Node<State, Input>.(State, Input) -> NodeReference)? = null
            private var exit: (Node<State, Input>.(State) -> Unit)? = null
            private var shouldTerminate: Boolean = false
            private var initial: Boolean = false
            fun enter(op: Node<State, Input>.(state: State) -> Unit) {
                this.enter = op
            }

            fun step(op: Node<State, Input>.(state: State, input: Input) -> NodeReference) {
                this.step = op
            }

            fun exit(op: Node<State, Input>.(state: State) -> Unit) {
                this.exit = op
            }

            fun terminal() {
                shouldTerminate = true
            }

            fun initial() {
                initial = true
            }

            fun build(id: NodeReference) = Node(id, enter, step!!, exit, shouldTerminate, initial)
        }

        override fun compareTo(other: Node<State, Input>): Int {
            return id.index.compareTo(other.id.index)
        }
    }

    class Builder<State, Input> {
        private val nodes = HashMap<NodeReference, Node<State, Input>>()

        private var index = 0
        fun node(id: NodeReference, op: Node.Builder<State, Input>.() -> Unit): Int {
            val node = Node.Builder<State, Input>().apply(op)

            nodes[id] = node.build(id)
            return id.index
        }

        fun nodeReference() = NodeReference(index++)
        fun node(op: Node.Builder<State, Input>.() -> Unit): NodeReference {
            val node = Node.Builder<State, Input>().apply(op)
            val id = nodeReference()
            nodes[id] = node.build(id)
            return id
        }

        fun build(): Graph<State, Input> {
            val nodes = nodes.entries.sortedBy { it.key }.map { it.value }
            return Graph(nodes, max(nodes.indexOfFirst { it.initial }, 0))
        }

    }
}

@JvmInline
value class NodeReference internal constructor(val index: Int = -1) : Comparable<NodeReference> {
    override fun compareTo(other: NodeReference): Int {
        return index.compareTo(other.index)
    }

}

data class GraphState<State>(
    val state: State,
    var current: Int = 0,
    var last: Int = 0
)


fun <State, Input> graph(op: Graph.Builder<State, Input>.() -> Unit) =
    Graph.Builder<State, Input>().apply(op).build()


