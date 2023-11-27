package io.firetail.logging.base

import io.firetail.logging.servlet.FiretailHeaderInterceptor
import io.firetail.logging.util.FiretailLogContext
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

class FiretailBeanFactory {

    @Bean
    fun firetailLogContext(): FiretailLogContext = FiretailLogContext()

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
