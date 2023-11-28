package io.firetail.logging.base

import io.firetail.logging.servlet.FiretailFilter
import io.firetail.logging.util.StringUtils
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestTemplate

@Configuration
@Import(
    StringUtils::class,
    FiretailFilter::class,
    FiretailBeanFactory::class,
    RestTemplate::class,
)
@ConditionalOnProperty("logging.firetail.enabled")
class FiretailConfig @Autowired constructor(
    @Value("\${firetail.ignorePatterns:#null}")
    val ignorePatterns: String? = null,
    @Value("\${firetail.logHeaders:false}")
    val logHeaders: Boolean = false,
    @Value("\${firetail.url:http://localhost:8500}")
    val url: String,
    @Value("\${firetail.apikey:not-defined}")
    val apikey: String = "not-defined",
) {

    val key = "x-ft-api-key"
    val logsBulk = "/logs/bulk"

    @PostConstruct
    fun logStatus() {
        LoggerFactory.getLogger(FiretailConfig::class.java).info("Firetail Initialized. url: $url")
    }
}
