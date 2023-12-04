package io.firetail.logging.core

import io.firetail.logging.spring.FiretailConfig
import io.firetail.logging.servlet.SpringRequestWrapper
import io.firetail.logging.servlet.SpringResponseWrapper
import org.slf4j.LoggerFactory

class FiretailLogger (val firetailConfig: FiretailConfig) {
    fun logRequest(wrappedRequest: SpringRequestWrapper) =
        if (firetailConfig.logHeaders) {
            logWithHeaders(wrappedRequest)
        } else {
            logNoHeaders(wrappedRequest)
        }

    private fun logNoHeaders(wrappedRequest: SpringRequestWrapper) {
        LOGGER.info(
            "${FiretailTemplate.logRequestPrefix} method: ${wrappedRequest.method}, uri: ${wrappedRequest.requestURI}",
        )
    }

    private fun logWithHeaders(wrappedRequest: SpringRequestWrapper) {
        LOGGER.info(
            "${FiretailTemplate.logRequestPrefix} " +
                    "method: ${wrappedRequest.method}, " +
                    "uri: ${wrappedRequest.requestURI}, " +
                    "headers: ${wrappedRequest.allHeaders}",
        )
    }

    fun logResponse(
        wrappedResponse: SpringResponseWrapper,
        status: Int = wrappedResponse.status,
        duration: Long,
    ) {
        LOGGER.info(
            "${FiretailTemplate.logResponsePrefix} ms: $duration, status: $status, headers: ${wrappedResponse.allHeaders}",
        )
    }
    companion object {
        val LOGGER = LoggerFactory.getLogger(FiretailLogger::class.java)
    }
}