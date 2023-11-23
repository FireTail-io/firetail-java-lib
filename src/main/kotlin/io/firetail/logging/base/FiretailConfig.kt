package io.firetail.logging.base

import io.firetail.logging.servlet.FiretailFilter
import io.firetail.logging.servlet.FiretailHeaderInterceptor
import io.firetail.logging.util.FiretailLogContext
import io.firetail.logging.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.client.RestTemplate

@Configuration
@Import(
    StringUtils::class,
    FiretailFilter::class,
)
@ConditionalOnProperty("logging.firetail.enabled")
class FiretailConfig @Autowired constructor(
    @Value("\${firetail.ignorePatterns:#null}")
    val ignorePatterns: String?,
    @Value("\${firetail.logHeaders:false}")
    val logHeaders: Boolean = false,
    @Value("\${firetail.url:http://localhost:8500}")
    val url: String,
    @Value("\${firetail.apiKey:not-defined}")
    val apiKey: String,
) {

    val key = "X-FT-API-KEY"

    @Bean
    fun firetailLogContext(): FiretailLogContext = FiretailLogContext()

    @Bean
    fun firetailTemplate(): FiretailTemplate {
        return FiretailTemplate(this)
    }

    @Bean
    fun firetailHeaderInterceptor(): FiretailHeaderInterceptor = FiretailHeaderInterceptor()

    @Bean
    fun wireInterceptor(
        restTemplate: RestTemplate,
        firetailHeaderInterceptor: FiretailHeaderInterceptor,
    ): RestTemplate {
        restTemplate.interceptors.add(firetailHeaderInterceptor)
        return restTemplate
    }
}
