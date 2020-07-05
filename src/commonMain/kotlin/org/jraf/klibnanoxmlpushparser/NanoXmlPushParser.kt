package org.jraf.klibnanoxmlpushparser

import org.jraf.klibnanoxmlpushparser.internal.NanoXmlPushParserImpl

fun newKNanoXmlPushParser(): NanoXmlPushParser = NanoXmlPushParserImpl()

interface NanoXmlPushParser {
    fun parse(input: CharSequence, callback: (Event) -> Unit)
}

sealed class Event {
    abstract val range: IntRange
    abstract val depth: Int
}

data class ContentEvent(
    override val range: IntRange,
    override val depth: Int,
    val content: String
) : Event()

data class TagStartEvent(
    override val range: IntRange,
    override val depth: Int,
    val name: CharSequence?,
    val attributes: Set<Attribute>
) : Event()

data class TagEndEvent(
    override val range: IntRange,
    override val depth: Int,
    val name: CharSequence?
) : Event()

interface Attribute {
    val name: CharSequence
    val value: CharSequence
}
