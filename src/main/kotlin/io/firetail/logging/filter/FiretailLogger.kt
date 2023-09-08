package io.firetail.logging.filter

import io.firetail.logging.config.Constants
import io.firetail.logging.util.StringUtils
import io.firetail.logging.wrapper.SpringRequestWrapper
import io.firetail.logging.wrapper.SpringResponseWrapper
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.LoggerFactory

class FiretailLogger(private val stringUtils: StringUtils = StringUtils(), private val logHeaders: Boolean = false) {
    fun logRequest(wrappedRequest: SpringRequestWrapper) {
        if (logHeaders) {
            logWithHeaders(wrappedRequest)
        } else {
            logNoHeaders(wrappedRequest)
        }
    }

    private fun logNoHeaders(wrappedRequest: SpringRequestWrapper) {
        LOGGER.info(
            "Request: method={}, uri={}, payload={}, audit={}",
            wrappedRequest.method,
            wrappedRequest.requestURI,
            stringUtils.toString(wrappedRequest.inputStream.readAllBytes(), wrappedRequest.characterEncoding),
            StructuredArguments.value(Constants.AUDIT, true),
        )
    }

    private fun logWithHeaders(wrappedRequest: SpringRequestWrapper) {
        LOGGER.info(
            "Request: method={}, uri={}, payload={}, headers={}, audit={}",
            wrappedRequest.method,
            wrappedRequest.requestURI,
            stringUtils.toString(wrappedRequest.inputStream.readAllBytes(), wrappedRequest.characterEncoding),
            wrappedRequest.allHeaders,
            StructuredArguments.value(Constants.AUDIT, true),
        )
    }

    fun logResponse(
        startTime: Long,
        wrappedResponse: SpringResponseWrapper,
        status: Int = wrappedResponse.status,
    ) {
        val duration = System.currentTimeMillis() - startTime
        wrappedResponse.characterEncoding = stringUtils.charSet()
        if (logHeaders) {
            logWithHeaders(duration, status, wrappedResponse)
        } else {
            logNoHeaders(duration, status, wrappedResponse)
        }
    }

    private fun logNoHeaders(
        duration: Long,
        status: Int,
        wrappedResponse: SpringResponseWrapper,
    ) {
        LOGGER.info(
            "Response({} ms): status={}, payload={}, audit={}",
            StructuredArguments.value(Constants.RESPONSE_TIME, duration),
            StructuredArguments.value(Constants.RESPONSE_STATUS, status),
            stringUtils.toString(wrappedResponse.contentAsByteArray),
            StructuredArguments.value(Constants.AUDIT, true),
        )
    }

    private fun logWithHeaders(
        duration: Long,
        status: Int,
        wrappedResponse: SpringResponseWrapper,
    ) {
        LOGGER.info(
            "Response({} ms): status={}, payload={}, headers={}, audit={}",
            StructuredArguments.value(Constants.RESPONSE_TIME, duration),
            StructuredArguments.value(Constants.RESPONSE_STATUS, status),
            stringUtils.toString(wrappedResponse.contentAsByteArray),
            wrappedResponse.allHeaders,
            StructuredArguments.value(Constants.AUDIT, true),
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }
}
