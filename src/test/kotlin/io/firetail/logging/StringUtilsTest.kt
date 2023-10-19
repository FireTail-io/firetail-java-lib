package io.firetail.logging

import io.firetail.logging.util.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.text.Charsets.US_ASCII

class StringUtilsTest {
    // Assertions around \r\n and unicode.
    private val input = "Now is the time \r\n for all good \n people to. 你好世界."

    @Test
    fun bytesToStringUsingDefaultEncoder() {
        assertThat(StringUtils().toString(input.encodeToByteArray()))
            .isEqualTo(input)
    }

    @Test
    fun bytesToStringUsingAsciiEncoderIsNotEqual() {
        assertThat(StringUtils(US_ASCII).toString(input.encodeToByteArray()))
            .isNotEqualTo(input)
            .startsWith(input.subSequence(0, 18))
    }

    @Test
    fun bytesToStringUsingSpecifiedEncoderIsNotEqual() {
        assertThat(StringUtils().toString(input.encodeToByteArray(), US_ASCII.toString()))
            .isNotEqualTo(input)
            .startsWith(input.subSequence(0, 18))
    }
}
