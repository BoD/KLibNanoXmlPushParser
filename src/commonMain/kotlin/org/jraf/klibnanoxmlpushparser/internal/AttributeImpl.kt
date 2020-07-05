package org.jraf.klibnanoxmlpushparser.internal

import org.jraf.klibnanoxmlpushparser.Attribute

internal data class AttributeImpl(
    override val name: String,
    override val value: String
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
