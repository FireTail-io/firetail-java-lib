package io.firetail.logging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.firetail.logging.base.FiretailLog
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

class FiretailDataSerialization {
    @Test
    fun validateV1Alpha() {
        val firetailLog = firetailLog()
        assertThat(firetailLog)
            .isNotNull
            .hasFieldOrPropertyWithValue("version", "1.0.0-alpha")
            .hasFieldOrProperty("request")
            .hasFieldOrProperty("response")
    }

    companion object {
        @JvmStatic
        fun firetailLog(): FiretailLog? {
            val objectMapper = jacksonObjectMapper()
            val jsonFile = ClassPathResource("/schemaV1Alpha.json").file
            return objectMapper.readValue(jsonFile, FiretailLog::class.java)
        }
    }
}
