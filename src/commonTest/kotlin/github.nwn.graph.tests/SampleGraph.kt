package github.nwn.graph.tests

import github.nwn.graph.ExceptionableState
import github.nwn.graph.error
import github.nwn.graph.graph

private const val ARGUMENT_START = '{'
private const val ARGUMENT_END = '}'
private const val ESCAPE = '\\'

data class SampleGraphState(
    val output: ArrayDeque<StringBuilder> = ArrayDeque<StringBuilder>().apply {
        addFirst(StringBuilder())
    }, var index: Int = 0,
    val args: List<Any?> = emptyList(),
    var escaped: Boolean = false,
    override var exception: SampleGraphException? = null
) : ExceptionableState<SampleGraphException> {
    override fun createException(message: String): SampleGraphException = SampleGraphException(message)
    val result: String
        get() = output.first().toString()
}


val SampleGraph = graph<SampleGraphState, String> {
    val rawNode = nodeReference()
    val argumentNode = nodeReference()
    val terminalNode = terminalNode()
    node(rawNode) {
        step {
            if (state.index >= input.length) {
                return@step terminalNode
            }
            val char = input[state.index]
            val r = when {
                state.escaped -> {
                    state.escaped = false
                    state.output.first().append(char)
                    id
                }
                char == ESCAPE -> {
                    state.escaped = true
                    id
                }
                char == ARGUMENT_START -> argumentNode
                else -> {
                    state.output.first().append(char)
                    id
                }
            }
            state.index++
            r
        }
    }
    node(argumentNode) {
        enter {
            state.output.addFirst(StringBuilder())
        }
        exit {
            state.output.removeFirst()
        }
        step {
            if (state.index >= input.length) {
                return@step error(terminalNode, "Unexpected end")
            }
            val char = input[state.index]
            val r = when {
                char == ARGUMENT_END -> {
                    val indexStr = state.output.first().toString()
                    val index = indexStr.toIntOrNull()
                    if (index == null) {
                        error(terminalNode, "Unable to parse '$indexStr' to argument index")
                    } else {
                        if (index >= 0 && index < state.args.size) {
                            state.output.last().append(state.args[index])
                            rawNode
                        } else
                            error(terminalNode, "Index out of bounds")

                    }

                }
                char.isDigit() -> {
                    state.output.first().append(char)
                    id
                }
                else -> {
                    error(terminalNode, "Unexpected char '$char' at index ${state.index}")
                }
            }
            state.index++
            return@step r
        }
    }
}

class SampleGraphException(override val message: String?) : Exception(message)