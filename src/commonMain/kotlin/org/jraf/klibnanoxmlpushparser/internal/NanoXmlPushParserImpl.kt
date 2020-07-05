package org.jraf.klibnanoxmlpushparser.internal

import org.jraf.klibnanoxmlpushparser.*

internal class NanoXmlPushParserImpl : NanoXmlPushParser {
    override fun parse(input: CharSequence, callback: (Event) -> Unit) {
        var depth = 0
        for (matchResult in CONTENT_OR_TAG_REGEX.findAll(input)) {
            val value = matchResult.value
            val range = matchResult.range

            // Start tag
            val tagStartMatchResult = TAG_START_REGEX.find(value)
            if (tagStartMatchResult != null) {
                val name = tagStartMatchResult.groupValues[1]
                // Attributes
                val attributes = parseAttributes(tagStartMatchResult.groupValues[2])
                callback(TagStartEvent(range, depth++, name, attributes))
            } else {
                // End tag
                val tagEndMatchResult = TAG_END_REGEX.find(value)
                if (tagEndMatchResult != null) {
                    val name = tagEndMatchResult.groupValues[1]
                    callback(TagEndEvent(range, --depth, name))
                } else {
                    // Empty tag
                    val tagEmptyMatchResult = TAG_EMPTY_REGEX.find(value)
                    if (tagEmptyMatchResult != null) {
                        val name = tagEmptyMatchResult.groupValues[1]
                        // Emit both a start and end events
                        callback(TagStartEvent(range, depth, name, emptySet()))
                        callback(TagEndEvent(range, depth, name))
                    } else {
                        // Content
                        callback(ContentEvent(range, depth, value))
                    }
                }
            }
        }
    }

    private fun parseAttributes(input: CharSequence): Set<Attribute> {
        val attributes = mutableSetOf<Attribute>()
        for (matchResult in ATTRIBUTE_REGEX.findAll(input)) {
            val name = matchResult.groupValues[1].trim()
            val value = matchResult.groupValues[2]
            attributes += AttributeImpl(name, value)
        }
        return attributes
    }

    companion object {
        // Identifier allowed for a tag name or attribute name
        private const val IDENTIFIER = "[^\\s/]+?"

        // Optional space(s)
        private const val SP = "\\s*?"

        private val CONTENT_OR_TAG_REGEX = Regex("([^<]+)|(<.+?>)")

        private val TAG_START_REGEX = Regex("<$SP($IDENTIFIER)((\\s+$IDENTIFIER$SP=$SP\".*?\")*?)$SP>")
        private val TAG_END_REGEX = Regex("<$SP/$SP($IDENTIFIER)$SP>")
        private val TAG_EMPTY_REGEX = Regex("<$SP($IDENTIFIER)$SP/$SP>")

        private val ATTRIBUTE_REGEX = Regex("($IDENTIFIER)$SP=$SP\"(.*?)\"")
    }
}
