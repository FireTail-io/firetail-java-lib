package io.firetail.logging.util

import org.springframework.stereotype.Service
import java.util.*

@Service
class KeyGenerator {
    fun generate(): String {
        return UUID.randomUUID().toString()
    }
}
