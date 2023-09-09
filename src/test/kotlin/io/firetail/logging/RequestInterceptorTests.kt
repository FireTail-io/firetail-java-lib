package io.firetail.logging

import io.firetail.logging.base.Constants
import io.firetail.logging.base.FiretailConfig
import io.firetail.logging.base.FiretailLogger
import io.firetail.logging.util.LogContext
import io.firetail.logging.util.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@WebMvcTest
@ContextConfiguration(
    classes = [
        RequestInterceptorTests.SimpleController::class,
        FiretailConfig::class,
        FiretailLogger::class,
        StringUtils::class,
        ApplicationContext::class,
    ],
)
@ExtendWith(SpringExtension::class)
class RequestInterceptorTests {

    @Autowired
    private lateinit var stringUtils: StringUtils

    @MockBean
    private lateinit var firetailLogger: FiretailLogger

    @Autowired
    private lateinit var logContext: LogContext

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun testWiring() {
        assertThat(stringUtils).isNotNull
        assertThat(firetailLogger).isNotNull
        assertThat(logContext).isNotNull
    }

    @Test
    fun fireTailRequestLoggingAndResponse() {
        MDC.clear()
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        verify(firetailLogger)
            .logRequest(any()) // Called once
        verify(firetailLogger)
            .logResponse(any(), any(), any()) // Called once

        // Headers are set
        assertThat(result.response.headerNames)
            .contains(Constants.REQUEST_ID, Constants.CORRELATION_ID)

        assertThat(MDC.get(Constants.REQUEST_ID))
            .isNotBlank()
            .isEqualTo(result.response.getHeaderValue(Constants.REQUEST_ID))
        assertThat(MDC.get(Constants.CORRELATION_ID))
            .isNotBlank()
            .isEqualTo(result.response.getHeaderValue(Constants.CORRELATION_ID))
    }

    // Emulates a general MVC controller for which we want to
    // assert Firetail calls have been made.
    @RestController
    internal class SimpleController {

        @GetMapping("/hello")
        fun sayHello(): String {
            return "hello"
        }
    }
}
