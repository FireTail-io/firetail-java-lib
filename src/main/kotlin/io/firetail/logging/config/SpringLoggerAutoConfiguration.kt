package io.firetail.logging.config

import io.firetail.logging.client.RestTemplateSetHeaderInterceptor
import io.firetail.logging.filter.SpringLoggerFilter
import io.firetail.logging.util.UniqueIDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate
import java.util.*
import javax.annotation.PostConstruct

// import net.logstash.logback.appender.LogstashTcpSocketAppender;
// import net.logstash.logback.encoder.LogstashEncoder;
@Configuration
@ConfigurationProperties(prefix = "logging.logstash")
class SpringLoggerAutoConfiguration {
    // private static final String FIRETAIL_APPENDER_NAME = "FIRETAIL";
    var url = "localhost:8500"
    var ignorePatterns: String? = null
    var isLogHeaders = false
    var trustStoreLocation: String? = null
    var trustStorePassword: String? = null

    @Value("\${spring.application.name:-}")
    lateinit var name: String

    @Autowired(required = false)
    var template: Optional<RestTemplate>? = null

    @Bean
    fun generator(): UniqueIDGenerator {
        return UniqueIDGenerator()
    }

    @Bean
    fun loggingFilter(): SpringLoggerFilter {
        return SpringLoggerFilter(generator(), ignorePatterns, isLogHeaders)
    }

    @Bean
    @ConditionalOnMissingBean(RestTemplate::class)
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        val interceptorList: MutableList<ClientHttpRequestInterceptor> = ArrayList()
        interceptorList.add(RestTemplateSetHeaderInterceptor())
        restTemplate.interceptors = interceptorList
        return restTemplate
    }

    /* rewrite this method to send data to firetail backend
	@Bean
	@ConditionalOnProperty("logging.firetail.enabled")
	public FiretailTcpSocketAppender firetailAppender() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		LogstashTcpSocketAppender logstashTcpSocketAppender = new LogstashTcpSocketAppender();
		logstashTcpSocketAppender.setName(FIRETAIL_APPENDER_NAME);
		logstashTcpSocketAppender.setContext(loggerContext);
		logstashTcpSocketAppender.addDestination(url);
		if (trustStoreLocation != null) {
			SSLConfiguration sslConfiguration = new SSLConfiguration();
			KeyStoreFactoryBean factory = new KeyStoreFactoryBean();
			factory.setLocation(trustStoreLocation);
			if (trustStorePassword != null)
				factory.setPassword(trustStorePassword);
			sslConfiguration.setTrustStore(factory);
			logstashTcpSocketAppender.setSsl(sslConfiguration);
		}
		LogstashEncoder encoder = new LogstashEncoder();
		encoder.setContext(loggerContext);
		encoder.setIncludeContext(true);
		encoder.setCustomFields("{\"appname\":\"" + name + "\"}");
		encoder.start();
		logstashTcpSocketAppender.setEncoder(encoder);
		logstashTcpSocketAppender.start();
		loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(logstashTcpSocketAppender);
		return logstashTcpSocketAppender;
	} */
    @PostConstruct
    fun init() {
        template!!.ifPresent { restTemplate: RestTemplate ->
            val interceptorList: MutableList<ClientHttpRequestInterceptor> = ArrayList()
            interceptorList.add(RestTemplateSetHeaderInterceptor())
            restTemplate.interceptors = interceptorList
        }
    }
}
