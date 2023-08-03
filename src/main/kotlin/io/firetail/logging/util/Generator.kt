package io.firetail.logging.util

import java.util.UUID

class Generator {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}
