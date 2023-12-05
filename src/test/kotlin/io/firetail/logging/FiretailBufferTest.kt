package io.firetail.logging

import io.firetail.logging.core.FiretailBuffer
import io.firetail.logging.core.FiretailData
import io.firetail.logging.core.FiretailTemplate
import io.firetail.logging.core.FtRequest
import io.firetail.logging.core.FtResponse
import io.firetail.logging.servlet.FiretailMapper
import io.firetail.logging.spring.FiretailConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import kotlin.test.Test

class FiretailBufferTest {

    @Mock
    private lateinit var firetailConfig: FiretailConfig

    @Mock
    private lateinit var firetailTemplate: FiretailTemplate

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun buffer() {
        val firetailConfig = FiretailConfig(capacity = 10, url = "")
        // val firetailTemplate: FiretailTemplate = FiretailTemplate(firetailConfig)
        val firetailBuffer = FiretailBuffer(firetailConfig, firetailTemplate)
        firetailBuffer.add(FiretailData(request = FtRequest(), response = FtResponse()))
        assertThat(firetailBuffer.size() == 1)
        Mockito.`when`(firetailTemplate.send(any()))
            .thenReturn("{\n    \"message\": \"success\"\n}")
        firetailBuffer.flush()
        assertThat(firetailBuffer.size() == 0)
    }
}
