package github.nwn.graph

/**
 * Builder for producing nodes in [Graph]
 */
class GraphNodeBuilder<State, Input> internal constructor(){
    private var enter: (GraphNode<State, Input>.(State) -> Unit)? = null
    private var step: (GraphNode<State, Input>.(State, Input) -> NodeReference)? = null
    private var exit: (GraphNode<State, Input>.(State) -> Unit)? = null
    private var shouldTerminate: Boolean = false
    private var initial: Boolean = false

    /**
     * Defines the behaviour for when the [Graph] first enters this node. This is only called if the previous node is different, and is called before [step]
     */
    fun enter(op: GraphNode<State, Input>.(state: State) -> Unit) {
        this.enter = op
    }

    /**
     * Defines the behaviour for when the [Graph] runs the input and state against this node. This is called every time this node is requested for processing.
     * @param op Should return the id of the next node. Use [GraphBuilder.nodeReference] to create node references ahead of time if necessary.
     *
     */
    fun step(op: GraphNode<State, Input>.(state: State, input: Input) -> NodeReference) {
        this.step = op
    }

    /**
     * Defines behaviour for when the [Graph] leaves this node. This is called after [step]
     */
    fun exit(op: GraphNode<State, Input>.(state: State) -> Unit) {
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

    internal fun build(id: NodeReference) = GraphNode(id, enter, step!!, exit, shouldTerminate, initial)
}