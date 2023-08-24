package io.firetail.logging

import io.firetail.logging.Constants.Companion.CORRELATION_ID
import io.firetail.logging.Constants.Companion.REQUEST_ID
import io.firetail.logging.util.Generator
import io.firetail.logging.util.UniqueIDGenerator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest

class IdGeneratorTests {
    @Test
    fun mdcIsSetFromHeaderValues() {
        val idGenerator = UniqueIDGenerator() // test with default generator
        val httpRequest = MockHttpServletRequest()
        val requestId = "requestId"
        val correlationId = "correlationId"
        httpRequest.addHeader(REQUEST_ID, requestId)
        httpRequest.addHeader(CORRELATION_ID, correlationId)
        idGenerator.generateAndSetMDC(httpRequest)

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
        val idGenerator = UniqueIDGenerator(generator)
        idGenerator.generateAndSetMDC(httpRequest)
        assertThat(httpRequest.headerNames.toList()).isEmpty()
        assertThat(MDC.get(REQUEST_ID)).isEqualTo(id)
        assertThat(MDC.get(CORRELATION_ID)).isEqualTo(id)
    }
}
