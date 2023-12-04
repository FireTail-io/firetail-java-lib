package io.firetail.logging

import io.firetail.logging.spring.FiretailConfig
import io.firetail.logging.core.FiretailTemplate
import io.firetail.logging.servlet.FiretailFilter
import io.firetail.logging.util.FiretailMDC
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import kotlin.test.assertNotNull

@ContextConfiguration(
    classes = [
        RequestInterceptorTests.SimpleController::class,
        RestTemplate::class,
    ],
)
@ExtendWith(SpringExtension::class)
class FiretailDisabledTest {

    @Autowired(required = false)
    private val firetailConfig: FiretailConfig? = null

    @Autowired(required = false)
    private val firetailTemplate: FiretailTemplate? = null

    @Autowired(required = false)
    private val firetailFilter: FiretailFilter? = null

    @Autowired(required = false)
    private val firetailLogContext: FiretailMDC? = null

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Test
    fun assertNotWired() {
        assertNull(firetailTemplate)
        assertNull(firetailConfig)
        assertNull(firetailFilter)
        assertNull(firetailLogContext)
        assertNotNull(restTemplate)
        // Clean interceptor as FT interceptor is disabled
        assertThat(restTemplate.interceptors).isEmpty()
    }
}
