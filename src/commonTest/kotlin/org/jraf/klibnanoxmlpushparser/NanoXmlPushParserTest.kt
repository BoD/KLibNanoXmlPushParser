package org.jraf.klibnanoxmlpushparser

import kotlin.test.Test
import kotlin.test.assertEquals

class NanoXmlPushParserTest {
    @Test
    fun `Element at first index`() {
        val eventList = parse("<ROOT>Simple!</ROOT>")
        assertEquals(
            listOf(
                TagStartEvent(0..5, 0, "ROOT", emptySet()),
                ContentEvent(6..12, 1, "Simple!"),
                TagEndEvent(13..19, 0, "ROOT")
            ),
            eventList
        )
    }

    @Test
    fun `Empty tag`() {
        val eventList = parse("Here's an <EMPTY /> tag")
        assertEquals(
            listOf(
                ContentEvent(0..9, 0, "Here's an "),
                TagStartEvent(10..18, 0, "EMPTY", emptySet()),
                TagEndEvent(10..18, 0, "EMPTY"),
                ContentEvent(19..22, 0, " tag")
            ),
            eventList
        )

    }


    @Test
    fun `Various cases`() {
        val eventList = parse(
            """
            Hello, World! <BOLD>This text is bold</BOLD>. This is a <LINK ID="test_000">a link</LINK>.
            
            This is a <STUFF ATTR1="attribute 1" ATTR2="attribute 2" ATTR3="attribute 3">a link with <I>more</I> attributes</STUFF>.
            """.trimIndent()
        )
        assertEquals(
            listOf(
                ContentEvent(
                    range = 0..13,
                    depth = 0,
                    content = "Hello, World! "
                ),
                TagStartEvent(
                    range = 14..19,
                    depth = 0,
                    name = "BOLD",
                    attributes = emptySet()
                ),
                ContentEvent(
                    range = 20..36,
                    depth = 1,
                    content = "This text is bold"
                ),
                TagEndEvent(range = 37..43, depth = 0, name = "BOLD"),
                ContentEvent(
                    range = 44..55,
                    depth = 0,
                    content = ". This is a "
                ),
                TagStartEvent(
                    range = 56..75,
                    depth = 0,
                    name = "LINK",
                    attributes = setOf<Attribute>(Attr("ID", "test_000"))
                ),
                ContentEvent(
                    range = 76..81,
                    depth = 1,
                    content = "a link"
                ),
                TagEndEvent(range = 82..88, depth = 0, name = "LINK"),
                ContentEvent(range = 89..101, depth = 0, content = ".\n\nThis is a "),
                TagStartEvent(
                    range = 102..168,
                    depth = 0,
                    name = "STUFF",
                    attributes = setOf<Attribute>(
                        Attr(name = "ATTR1", value = "attribute 1"),
                        Attr(name = "ATTR2", value = "attribute 2"),
                        Attr(name = "ATTR3", value = "attribute 3")
                    )
                ),
                ContentEvent(range = 169..180, depth = 1, content = "a link with "),
                TagStartEvent(range = 181..183, depth = 1, name = "I", attributes = emptySet()),
                ContentEvent(range = 184..187, depth = 2, content = "more"),
                TagEndEvent(range = 188..191, depth = 1, name = "I"),
                ContentEvent(range = 192..202, depth = 1, content = " attributes"),
                TagEndEvent(range = 203..210, depth = 0, name = "STUFF"),
                ContentEvent(range = 211..211, depth = 0, content = ".")
            ),
            eventList
        )

    }

    private fun parse(input: String): MutableList<Event> {
        val parser = newKNanoXmlPushParser()
        val eventList = mutableListOf<Event>()
        parser.parse(input) { event ->
            eventList += event
        }
        return eventList
    }

    private fun assertEquals(expected: List<*>, actual: List<*>) {
        for ((i, elem) in expected.withIndex()) {
            assertEquals(elem, actual[i])
        }
    }
}

class Attr(
    override val name: CharSequence,
    override val value: CharSequence
) : Attribute {

    override fun equals(other: Any?): Boolean {
        other as Attribute
        return name == other.name && value == other.value
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}
