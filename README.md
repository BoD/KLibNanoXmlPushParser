# KLibNanoXmlPushParser

*Work in progress!*

This is a tiny XML push parser Kotlin Multiplatform library.

## Usage

```kotlin
val parser = newKNanoXmlPushParser()
parser.parse(input) { event ->
    println("range=${event.range} depth=${event.depth}")
    when (event) {
        is TagStartEvent -> println("name=${event.name} attributes=${event.attributes}")
        is TagEndEvent -> println("name=${event.name}")
        is ContentEvent -> println("content=${event.content}")
    }
}
```
