package github.nwn.graph

class GraphNodeBuilder<State, Input> {
    private var enter: (GraphNode<State, Input>.(State) -> Unit)? = null
    private var step: (GraphNode<State, Input>.(State, Input) -> NodeReference)? = null
    private var exit: (GraphNode<State, Input>.(State) -> Unit)? = null
    private var shouldTerminate: Boolean = false
    private var initial: Boolean = false
    fun enter(op: GraphNode<State, Input>.(state: State) -> Unit) {
        this.enter = op
    }

    fun step(op: GraphNode<State, Input>.(state: State, input: Input) -> NodeReference) {
        this.step = op
    }

    fun exit(op: GraphNode<State, Input>.(state: State) -> Unit) {
        this.exit = op
    }

    fun terminal() {
        shouldTerminate = true
    }

    fun initial() {
        initial = true
    }

    fun build(id: NodeReference) = GraphNode(id, enter, step!!, exit, shouldTerminate, initial)
}