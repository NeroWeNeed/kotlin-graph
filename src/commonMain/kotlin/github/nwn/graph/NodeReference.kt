package github.nwn.graph

import kotlin.jvm.JvmInline

/**
 * Represents a reference to a node.
 */
@JvmInline
value class NodeReference internal constructor(val index: Int = -1) : Comparable<NodeReference> {
    override fun compareTo(other: NodeReference): Int {
        return index.compareTo(other.index)
    }

}