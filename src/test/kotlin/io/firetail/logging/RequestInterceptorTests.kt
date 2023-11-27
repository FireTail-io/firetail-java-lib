package io.firetail.logging

import io.firetail.logging.base.Constants
import io.firetail.logging.base.FiretailConfig
import io.firetail.logging.base.FiretailMapper
import io.firetail.logging.base.FiretailTemplate
import io.firetail.logging.servlet.FiretailFilter
import io.firetail.logging.servlet.FiretailHeaderInterceptor
import io.firetail.logging.util.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

// @ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [
        RequestInterceptorTests.SimpleController::class,
        FiretailConfig::class,
        StringUtils::class,
        RestTemplate::class,
        ApplicationContext::class,
    ],
)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@TestPropertySource(
    properties = [
        "logging.firetail.enabled=true",
        "firetail.url=http://localhost:\${wiremock.server.port}",
    ],
)
class RequestInterceptorTests {

    @Autowired
    private lateinit var stringUtils: StringUtils

    @MockBean
    private lateinit var firetailTemplate: FiretailTemplate

    @MockBean
    private lateinit var firetailMapper: FiretailMapper

    @Autowired
    private lateinit var firetailHeaderInterceptor: FiretailHeaderInterceptor

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var firetailFilter: FiretailFilter

    @Test
    fun testWiring() {
        assertThat(stringUtils).isNotNull
        assertThat(firetailTemplate).isNotNull
        assertThat(firetailFilter).isNotNull
        assertThat(firetailMapper).isNotNull
        assertThat(restTemplate).isNotNull
        assertThat(restTemplate.interceptors).isNotEmpty.contains(firetailHeaderInterceptor)
    }

    @Test
    fun fireTailRequestLoggingAndResponse() {
        MDC.clear()
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        verify(firetailTemplate)
            .logRequest(any()) // Called once
        verify(firetailTemplate)
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
