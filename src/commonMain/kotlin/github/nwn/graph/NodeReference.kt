package github.nwn.graph

import kotlin.jvm.JvmInline

@JvmInline
value class NodeReference internal constructor(val index: Int = -1) : Comparable<NodeReference> {
    override fun compareTo(other: NodeReference): Int {
        return index.compareTo(other.index)
    }

}