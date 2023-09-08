package io.firetail.logging

import io.firetail.logging.config.FiretailConfig
import io.firetail.logging.filter.FiretailLogger
import io.firetail.logging.util.LogContext
import io.firetail.logging.util.StringUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@WebMvcTest
@ContextConfiguration(
    classes = [
        RequestInterceptorTests.SimpleController::class,
        FiretailConfig::class,
        ApplicationContext::class,
    ],
)
@ExtendWith(SpringExtension::class)
class RequestInterceptorTests {

    @Autowired
    private lateinit var stringUtils: StringUtils

    @Autowired
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
    fun something() {
        mockMvc.perform(MockMvcRequestBuilders.get("/hello"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
    }

    @RestController("/")
    internal class SimpleController {

        @GetMapping("/hello")
        fun sayHello(): String {
            return "hello"
        }
    }
}
