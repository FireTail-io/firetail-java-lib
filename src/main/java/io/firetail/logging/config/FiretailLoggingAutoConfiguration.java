package io.firetail.logging.config;

import net.logstash.logback.encoder.LogstashEncoder;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import de.idealo.whitelabels.logback.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import io.firetail.logging.client.RestTemplateSetHeaderInterceptor;
import io.firetail.logging.filter.SpringLoggingFilter;
import io.firetail.logging.util.UniqueIDGenerator;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "logging.firetail")
public class FiretailLoggingAutoConfiguration {

    private static final String FIRETAIL_APPENDER_NAME = "FIRETAIL";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FiretailLoggingAutoConfiguration.class);

    private String url = "http://localhost:3100";
    private String ignorePatterns;
    private boolean logHeaders;
    private String trustStoreLocation;
    private String trustStorePassword;
    private String requestIdHeaderName = "X-Request-ID";
    private String correlationIdHeaderName = "X-Correlation-ID";


    @Value("${spring.application.name:-}")
    String name;

    @Bean
    public UniqueIDGenerator generator() {
        return new UniqueIDGenerator(requestIdHeaderName, correlationIdHeaderName);
    }

    @Bean
    public SpringLoggingFilter loggingFilter() {
        return new SpringLoggingFilter(generator(), ignorePatterns, logHeaders);
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnBean
    public RestTemplate existingRestTemplate(final RestTemplate restTemplate) {
        List<ClientHttpRequestInterceptor> interceptorList = new ArrayList<ClientHttpRequestInterceptor>();
        interceptorList.add(new RestTemplateSetHeaderInterceptor());
        restTemplate.setInterceptors(interceptorList);
        return restTemplate;
    }

    @Bean
    @ConditionalOnProperty(value = "logging.firetail.enabled", matchIfMissing = true, havingValue = "true")
    public HttpAppender firetailAppender() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        HttpAppender httpAppender = new HttpAppender();
	httpAppender.setContext(loggerContext);
	httpAppender.setName(FIRETAIL_APPENDER_NAME);

	HttpClient httpClient = new HttpClient();
        httpClient.setDestination(url);
        httpAppender.setHttpClient(httpClient);

        LogstashEncoder encoder = new LogstashEncoder();
        encoder.setContext(loggerContext);
        encoder.setIncludeContext(true);
        encoder.setCustomFields("{\"appname\":\"" + name + "\"}");
	encoder.start();

	httpAppender.setEncoder(encoder);
	httpAppender.start();

        LOGGER.info("Firetail Appender running...");

	loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(httpAppender);
	return httpAppender;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTrustStoreLocation() {
        return trustStoreLocation;
    }

    public void setTrustStoreLocation(String trustStoreLocation) {
        this.trustStoreLocation = trustStoreLocation;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getIgnorePatterns() {
        return ignorePatterns;
    }

    public void setIgnorePatterns(String ignorePatterns) {
        this.ignorePatterns = ignorePatterns;
    }

    public boolean isLogHeaders() {
        return logHeaders;
    }

    public void setLogHeaders(boolean logHeaders) {
        this.logHeaders = logHeaders;
    }

    public String getRequestIdHeaderName() {
        return requestIdHeaderName;
    }

    public void setRequestIdHeaderName(String requestIdHeaderName) {
        this.requestIdHeaderName = requestIdHeaderName;
    }

    public String getCorrelationIdHeaderName() {
        return correlationIdHeaderName;
    }

    public void setCorrelationIdHeaderName(String correlationIdHeaderName) {
        this.correlationIdHeaderName = correlationIdHeaderName;
    }
}
