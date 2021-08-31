package github.nwn.graph

/**
 * Builder for producing nodes in [Graph]
 */
@GraphDSL
class GraphNodeBuilder<State, Input> internal constructor(val id: NodeReference){
    private var enter: (GraphNodeScope<State, Input>.() -> Unit)? = null
    private var step: (GraphNodeScope<State, Input>.() -> NodeReference)? = null
    private var exit: (GraphNodeScope<State, Input>.() -> Unit)? = null
    private var shouldTerminate: Boolean = false
    private var initial: Boolean = false

    /**
     * Defines the behaviour for when the [Graph] first enters this node. This is only called if the previous node is different, and is called before [step]
     */
    fun enter(op: GraphNodeScope<State, Input>.() -> Unit) {
        this.enter = op
    }

    /**
     * Defines the behaviour for when the [Graph] runs the input and state against this node. This is called every time this node is requested for processing.
     * @param op Should return the id of the next node. Use [GraphBuilder.nodeReference] to create node references ahead of time if necessary.
     *
     */
    fun step(op: GraphNodeScope<State, Input>.() -> NodeReference) {
        this.step = op
    }

    /**
     * Defines behaviour for when the [Graph] leaves this node. This is called after [step]
     */
    fun exit(op: GraphNodeScope<State, Input>.() -> Unit) {
        this.exit = op
    }

    /**
     * Indicates this node is a terminal node.
     */
    fun terminal() {
        shouldTerminate = true
    }

    /**
     * Indices this node is the initial node. This should only be called for one node.
     */
    fun initial() {
        initial = true
    }

    internal fun build() = GraphNode(id, enter, step!!, exit, shouldTerminate, initial)
}