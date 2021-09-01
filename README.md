# Kotlin Graph
Simple Kotlin DSL for streamlining state machine-like functionality.

# Motivation
I personally have run into a lot of relatively small scale problems that required state machines as solutions. It was very cumbersome to write and required a lot of boilerplate. This is an attempt to solve that problem.

# Features
 - State Machine like functionality.
 - State mutations
 - Custom Behaviour when upon entering and leaving a node.
 - Easy to maintain
 - Multiplatform support (Should work on any platform, but targets might need to be tweaked.)

# Usage
 - Define a State object (preferably mutable)
 - Define a Graph with the `graph()` entry point.
 - Define nodes with `node()`
   - Define behaviour for each step with `step()`
   - Optionally define behaviour for entering and exiting a node with `enter()` and `exit()`
   - Mark terminal nodes
 - provide an input (preferably immutable) and process with `process`

# Use Cases
 - Text Processors
 - Complex Logic that you don't want to dedicate an entire project to.

# Example
A simple text templating graph which writes arguments anywhere there are a pair of unescaped curly braces in the format of `{#}`. Any string input can be run against this with `SampleGraph.process(input, state)` or `SampleGraph.processAndThrow(input, state)`

`SampleGraph.processAndThrow(input, state)` is only available for graphs that process states that implement `ExceptionableState`. Similar functionality can be produced by anyone and this only exists for convenience.

```kotlin
import github.nwn.graph.ExceptionableState
import github.nwn.graph.error
import github.nwn.graph.graph

private const val ARGUMENT_START = '{'
private const val ARGUMENT_END = '}'
private const val ESCAPE = '\\'

data class SampleGraphState(
    val output: ArrayDeque<StringBuilder> = ArrayDeque<StringBuilder>().apply {
        addFirst(StringBuilder())
    }, 
    var index: Int = 0,
    val args: List<Any?> = emptyList(),
    var escaped: Boolean = false,
    override var exception: SampleGraphException? = null
) : ExceptionableState<SampleGraphException> {
    override fun createException(message: String): SampleGraphException = SampleGraphException(message)
    val result: String
        get() = output.first().toString()
}

class SampleGraphException(override val message: String?) : Exception(message)

val SampleGraph = graph<SampleGraphState, String> {
    // Define Nodes in advance if necessary with nodeReference
    val rawNode = nodeReference()
    val argumentNode = nodeReference()
    val terminalNode = terminalNode()
    // Define node behaviour
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
        // Define behaviour when the graph processor enters a node
        enter {
            state.output.addFirst(StringBuilder())
        }
       // Define behaviour when the graph processor exits a node
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
```

# TODO
 - Better utility functions
 - API Improvements

# Future Plans
## Graph Visualizer
Realistically a tool for visualizing node connections is doable, and relatively simple to create. I'd like to create a tool leveraging static code analysis to achieve this.