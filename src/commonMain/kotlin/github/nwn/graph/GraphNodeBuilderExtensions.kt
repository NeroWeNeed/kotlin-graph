package github.nwn.graph

import github.nwn.graph.GraphNode
import github.nwn.graph.GraphNodeBuilder
import github.nwn.graph.NodeReference

interface TextProcessingState {
    var index: Int
}


interface ExceptionableState<T : Exception> {
    var exception: T?
    fun createException(message: String): T
}

fun <State : ExceptionableState<T>, T : Exception, Input> GraphNode<State, Input>.error(
    state: State,
    terminalNode: NodeReference,
    message: String
): NodeReference {
    state.exception = state.createException(message)
    return terminalNode
}

class CharStepBuilder<State : TextProcessingState, Input : CharSequence> {
    internal var onEnd: (GraphNode<State, Input>.(state: State, input: Input) -> NodeReference)? = null
    internal var onStep: (GraphNode<State, Input>.(state: State, input: Input, char: Char) -> NodeReference)? = null
    fun end(op: GraphNode<State, Input>.(state: State, input: Input) -> NodeReference) {
        onEnd = op
    }

    fun step(op: GraphNode<State, Input>.(state: State, input: Input, char: Char) -> NodeReference) {
        onStep = op
    }

}


fun <State : TextProcessingState, Input : CharSequence> GraphNodeBuilder<State, Input>.charStep(
    op: CharStepBuilder<State, Input>.() -> Unit
) {
    val (onEnd, onStep) = CharStepBuilder<State, Input>().apply(op).let { it.onEnd!! to it.onStep!! }
    step { state, input ->
        if (state.index >= input.length) {
            return@step onEnd(state, input)
        }
        val char = input[state.index]
        val result = onStep(state, input, char)
        state.index++
        return@step result
    }
}

fun <State : TextProcessingState, Input : CharSequence> GraphNodeBuilder<State, Input>.charStep(
    onEnd: GraphNode<State, Input>.(state: State, input: Input) -> NodeReference,
    onStep: GraphNode<State, Input>.(state: State, input: Input, char: Char) -> NodeReference
) {
    step { state, input ->
        if (state.index >= input.length) {
            return@step onEnd(state, input)
        }
        val char = input[state.index]
        val result = onStep(state, input, char)
        state.index++
        return@step result
    }
}