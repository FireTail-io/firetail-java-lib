package io.firetail.logging

import io.firetail.logging.base.Constants.Companion.CORRELATION_ID
import io.firetail.logging.base.Constants.Companion.REQUEST_ID
import io.firetail.logging.util.FiretailMDC
import io.firetail.logging.util.KeyGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.mock.web.MockHttpServletRequest

class MDCGeneratorTests {
    @Test
    fun mdcIsSetFromHeaderValues() {
        val firetailLogContext = FiretailMDC() // test with default generator
        val httpRequest = MockHttpServletRequest()
        val requestId = "requestId"
        val correlationId = "correlationId"
        httpRequest.addHeader(REQUEST_ID, requestId)
        httpRequest.addHeader(CORRELATION_ID, correlationId)
        firetailLogContext.generateAndSetMDC(httpRequest)

        assertThat(firetailLogContext.get(REQUEST_ID)).isEqualTo(requestId)
        assertThat(firetailLogContext.get(CORRELATION_ID)).isEqualTo(correlationId)
        assertThat(httpRequest.getHeader(REQUEST_ID)).isEqualTo(requestId)
        assertThat(httpRequest.getHeader(CORRELATION_ID)).isEqualTo(correlationId)
    }

    @Test
    fun mdcIsSetWhenNoHeaderValues() {
        val keyGenerator = Mockito.mock(KeyGenerator::class.java)
        val firetailMdc = FiretailMDC(keyGenerator = keyGenerator) // test with default generator
        firetailMdc.clear()
        assertThat(firetailMdc.get(CORRELATION_ID)).isNull()
        assertThat(firetailMdc.get(REQUEST_ID)).isNull()
        val httpRequest = MockHttpServletRequest()
        val id = "someValue"
        Mockito.`when`(keyGenerator.generate()).thenReturn(id)
        firetailMdc.generateAndSetMDC(httpRequest)
        assertThat(httpRequest.headerNames.toList()).isEmpty()
        assertThat(firetailMdc.get(REQUEST_ID)).isEqualTo(id)
        assertThat(firetailMdc.get(CORRELATION_ID)).isEqualTo(id)
    }

    @Test
    fun generateId() {
        val keyGenerator = KeyGenerator()
        assertThat(keyGenerator.generate()).isNotNull().isNotEmpty()
    }
}
