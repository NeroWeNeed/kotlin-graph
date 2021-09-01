package github.nwn.graph.tests

import github.nwn.graph.processAndThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GraphTest {


    @Test
    fun `Raw Input`() {
        val input = "Simple Raw Input"
        val state = SampleGraph.process(input, SampleGraphState())
        assertEquals(state.result, input)
    }

    @Test
    fun `Single Argument`() {
        val input = "Simple {0} Input"
        val state = SampleGraph.process(input, SampleGraphState(args = listOf("Cooked")))
        assertEquals(state.result, "Simple Cooked Input")
    }

    @Test
    fun `Multiple Arguments`() {
        val input = "Simple {0} Input that has been {0}, {1}, and {2}."
        val state = SampleGraph.process(input, SampleGraphState(args = listOf("Cooked", "Cleaned", "Covered")))
        assertEquals(state.result, "Simple Cooked Input that has been Cooked, Cleaned, and Covered.")
    }

    @Test
    fun `Unexpected End`() {
        val input = "Simple {0"
        assertFailsWith<SampleGraphException> {
            SampleGraph.processAndThrow(
                input,
                SampleGraphState(args = listOf("Cooked", "Cleaned", "Covered"))
            )
        }
    }
    @Test
    fun `Invalid Character`() {
        val input = "Simple {0a}"
        assertFailsWith<SampleGraphException> {
            SampleGraph.processAndThrow(
                input,
                SampleGraphState(args = listOf("Cooked", "Cleaned", "Covered"))
            )
        }
    }
    @Test
    fun `Escaped Input`() {
        val input = "Simple Raw Input \\{0}"
        val state = SampleGraph.process(input, SampleGraphState())
        assertEquals(state.result, "Simple Raw Input {0}")
    }
}