package io.firetail.logging.base

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
    fun firetailTemplate(firetailConfig: FiretailConfig): FiretailTemplate {
        return FiretailTemplate(firetailConfig)
    }

    @Bean
    fun firetailHeaderInterceptor(restTemplate: RestTemplate): FiretailHeaderInterceptor {
        val ftHeader = FiretailHeaderInterceptor()
        restTemplate.interceptors.add(ftHeader)
        return ftHeader
    }

    @Bean
    fun firetailMapper(): FiretailMapper = FiretailMapper()
}
