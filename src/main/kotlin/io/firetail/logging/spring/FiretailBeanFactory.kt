package io.firetail.logging.spring

import io.firetail.logging.core.FiretailBuffer
import io.firetail.logging.core.FiretailTemplate
import io.firetail.logging.servlet.FiretailHeaderInterceptor
import io.firetail.logging.servlet.FiretailMapper
import io.firetail.logging.util.FiretailMDC
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@ConditionalOnClass(FiretailConfig::class)
class FiretailBeanFactory {

    @Bean
    fun firetailMDC(): FiretailMDC = FiretailMDC()

    @Bean
    fun firetailMapper(): FiretailMapper = FiretailMapper()

    @Bean
    fun firetailTemplate(firetailConfig: FiretailConfig, firetailMapper: FiretailMapper): FiretailTemplate {
        return FiretailTemplate(firetailConfig, firetailMapper)
    }

    @Bean
    fun firetailBuffer(firetailConfig: FiretailConfig,
                       firetailTemplate: FiretailTemplate,
                       firetailMapper: FiretailMapper): FiretailBuffer =
        FiretailBuffer(firetailConfig, firetailTemplate, firetailMapper)

    @Bean
    fun firetailHeaderInterceptor(restTemplate: RestTemplate): FiretailHeaderInterceptor {
        val ftHeader = FiretailHeaderInterceptor()
        restTemplate.interceptors.add(ftHeader)
        return ftHeader
    }
}
