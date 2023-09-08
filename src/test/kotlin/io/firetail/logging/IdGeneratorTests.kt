package io.firetail.logging

import io.firetail.logging.config.Constants.Companion.CORRELATION_ID
import io.firetail.logging.config.Constants.Companion.REQUEST_ID
import io.firetail.logging.util.Generator
import io.firetail.logging.util.LogContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest

class IdGeneratorTests {
    @Test
    fun mdcIsSetFromHeaderValues() {
        val logContext = LogContext() // test with default generator
        val httpRequest = MockHttpServletRequest()
        val requestId = "requestId"
        val correlationId = "correlationId"
        httpRequest.addHeader(REQUEST_ID, requestId)
        httpRequest.addHeader(CORRELATION_ID, correlationId)
        logContext.generateAndSetMDC(httpRequest)

        assertThat(MDC.get(REQUEST_ID)).isEqualTo(requestId)
        assertThat(MDC.get(CORRELATION_ID)).isEqualTo(correlationId)
        assertThat(httpRequest.getHeader(REQUEST_ID)).isEqualTo(requestId)
        assertThat(httpRequest.getHeader(CORRELATION_ID)).isEqualTo(correlationId)
    }

    @Test
    fun mdcIsSetWhenNoHeaderValues() {
        val generator = Mockito.mock(Generator::class.java)
        MDC.clear()
        assertThat(MDC.get(CORRELATION_ID)).isNull()
        assertThat(MDC.get(REQUEST_ID)).isNull()
        val httpRequest = MockHttpServletRequest()
        val id = "someValue"
        Mockito.`when`(generator.generate()).thenReturn(id)
        val idGenerator = LogContext(generator)
        idGenerator.generateAndSetMDC(httpRequest)
        assertThat(httpRequest.headerNames.toList()).isEmpty()
        assertThat(MDC.get(REQUEST_ID)).isEqualTo(id)
        assertThat(MDC.get(CORRELATION_ID)).isEqualTo(id)
    }

    @Test
    fun generateId() {
        val generator = Generator()
        assertThat(generator.generate()).isNotNull().isNotEmpty()
    }
}
