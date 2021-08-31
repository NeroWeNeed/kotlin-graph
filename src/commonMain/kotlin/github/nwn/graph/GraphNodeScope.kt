package github.nwn.graph

class GraphNodeScope<State, Input>(
    val id: NodeReference,
    val terminal: Boolean,
    val initial: Boolean,
    val state: State,
    val input: Input
) {

}
