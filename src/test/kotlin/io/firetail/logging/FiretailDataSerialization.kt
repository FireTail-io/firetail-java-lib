package io.firetail.logging

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.firetail.logging.base.FireTailLog
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

class FiretailDataSerialization {
    @Test
    fun validateV1Alpha() {
        val objectMapper = jacksonObjectMapper()
        val jsonFile = ClassPathResource("/schemaV1Alpha.json").file
        val firetailLog = objectMapper.readValue(jsonFile, FireTailLog::class.java)
        assertThat(firetailLog)
            .isNotNull
            .hasFieldOrPropertyWithValue("version", "1.0.0-alpha")
            .hasFieldOrProperty("request")
            .hasFieldOrProperty("response")
    }
}
