package github.nwn.graph

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
    internal var onEnd: (GraphNodeScope<State, Input>.() -> NodeReference)? = null
    internal var onStep: (GraphNodeScope<State, Input>.(char: Char) -> NodeReference)? = null
    fun end(op: GraphNodeScope<State, Input>.() -> NodeReference) {
        onEnd = op
    }

    fun step(op: GraphNodeScope<State, Input>.(char: Char) -> NodeReference) {
        onStep = op
    }

}


fun <State : TextProcessingState, Input : CharSequence> GraphNodeBuilder<State, Input>.charStep(
    op: CharStepBuilder<State, Input>.() -> Unit
) {
    val (onEnd, onStep) = CharStepBuilder<State, Input>().apply(op).let { it.onEnd!! to it.onStep!! }
    step {
        if (state.index >= input.length) {

            return@step onEnd()
        }
        val char = input[state.index]
        val result = onStep(char)
        state.index++
        return@step result
    }
}