package io.firetail.logging.base

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ConfigurationProperties(prefix = "logging.logstash")
@Import(FiretailConfig::class)
class TcpLogger(val firetailConfig: FiretailConfig) {
    var trustStoreLocation: String? = null
    var trustStorePassword: String? = null

    @Value("\${spring.application.name:-}")
    lateinit var name: String

//    @Bean
//    @ConditionalOnProperty("logging.firetail.enabled")
//    fun firetailAppender(): LogstashTcpSocketAppender {
//        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
//        val logstashTcpSocketAppender = LogstashTcpSocketAppender()
//        logstashTcpSocketAppender.name = FIRETAIL_APPENDER_NAME
//        logstashTcpSocketAppender.context = loggerContext
//        logstashTcpSocketAppender.addDestination(firetailConfig.url)
//        if (trustStoreLocation != null) {
//            val sslConfiguration = SSLConfiguration()
//            val factory = KeyStoreFactoryBean()
//            factory.location = trustStoreLocation
//            if (trustStorePassword != null) factory.password = trustStorePassword
//            sslConfiguration.trustStore = factory
//            logstashTcpSocketAppender.ssl = sslConfiguration
//        }
//        val encoder = LogstashEncoder()
//        encoder.context = loggerContext
//        encoder.isIncludeContext = true
//        encoder.customFields = "{\"appname\":\"$name\"}"
//        encoder.start()
//        logstashTcpSocketAppender.encoder = encoder
//        logstashTcpSocketAppender.start()
//        loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(logstashTcpSocketAppender)
//        return logstashTcpSocketAppender
//    }
}
