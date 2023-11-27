package io.firetail.logging

import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import io.firetail.logging.base.FiretailConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject

@SpringBootTest(classes = [FiretailConfig::class])
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
@TestPropertySource(
    properties = [
        "logging.firetail.enabled=true",
        "firetail.url=http://localhost:\${wiremock.server.port}",
    ],
)
class MockServer {
    @Value("\${firetail.url}")
    private lateinit var firetailUrl: String

    @Autowired
    lateinit var firetailConfig: FiretailConfig

    @Test
    fun testExternalServiceIntegration() {
        stubFor(
            post(firetailConfig.logsBulk).willReturn(
                ok().withBody("Mocked Response"),
            ),
        )
        val url = "${firetailUrl}${firetailConfig.logsBulk}"
        val firetailLog = FiretailDataSerialization.firetailLog()

        val response = RestTemplate().postForObject<String>(url, firetailLog)

        assertEquals("Mocked Response", response)
    }
}
