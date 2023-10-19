package io.firetail.logging.util

import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

@Service
class StringUtils(private val defaultCharset: Charset = UTF_8) {
    fun toString(inputStream: ByteArray, characterEncoding: String = charSet()): String {
        return IOUtils.toString(
            inputStream,
            characterEncoding,
        )
    }

    fun charSet() = defaultCharset.toString()
}
