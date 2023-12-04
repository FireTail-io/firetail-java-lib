package io.firetail.logging

import io.firetail.logging.core.Constants
import io.firetail.logging.core.FiretailBuffer
import io.firetail.logging.spring.EnableFiretail
import io.firetail.logging.core.FiretailLogger
import io.firetail.logging.core.FiretailTemplate
import io.firetail.logging.servlet.FiretailFilter
import io.firetail.logging.servlet.FiretailHeaderInterceptor
import io.firetail.logging.servlet.FiretailMapper
import io.firetail.logging.util.FiretailMDC
import io.firetail.logging.util.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@SpringBootTest(
    classes = [
        RequestInterceptorTests.SimpleController::class,
    ],
)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@TestPropertySource(
    properties = [
        "firetail.url=http://localhost:\${wiremock.server.port}",
    ],
)
@EnableFiretail
class RequestInterceptorTests {

    @MockBean
    private lateinit var firetailLogger: FiretailLogger

    @Autowired
    private lateinit var firetailMapper: FiretailMapper

    @Autowired
    private lateinit var firetailTemplate: FiretailTemplate

    @Autowired
    private lateinit var stringUtils: StringUtils

    @Autowired
    private lateinit var firetailHeaderInterceptor: FiretailHeaderInterceptor

    @Autowired
    private lateinit var firetailBuffer: FiretailBuffer

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var firetailFilter: FiretailFilter

    @Autowired
    private lateinit var firetailMDC: FiretailMDC

    @Test
    fun testWiring() {
        assertThat(stringUtils).isNotNull
        assertThat(firetailTemplate).isNotNull
        assertThat(firetailMDC).isNotNull
        assertThat(firetailFilter).isNotNull
        assertThat(firetailMapper).isNotNull
        assertThat(restTemplate).isNotNull
        assertThat(firetailBuffer).isNotNull
        assertThat(restTemplate.interceptors).isNotEmpty.contains(firetailHeaderInterceptor)
    }

    @Test
    fun fireTailRequestLoggingAndResponse() {
        firetailMDC.clear()
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        assertThat(result.response.headerNames)
            .contains(Constants.REQUEST_ID, Constants.CORRELATION_ID)

        verify(firetailLogger)
            .logRequest(any()) // Called once

        verify(firetailLogger)
            .logResponse(any(), any(), any()) // Called once

        assertThat(firetailMDC.get(Constants.REQUEST_ID))
            .isNotNull()
            .isEqualTo(result.response.getHeaderValue(Constants.REQUEST_ID))

        assertThat(firetailMDC.get(Constants.CORRELATION_ID))
            .isNotNull()
            .isEqualTo(result.response.getHeaderValue(Constants.CORRELATION_ID))

        assertThat(firetailBuffer.size() == 1)
        assertThat(firetailBuffer.flush()).isEqualTo("success")
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
