package io.firetail.logging.base

import com.fasterxml.jackson.databind.ObjectMapper
import io.firetail.logging.servlet.SpringRequestWrapper
import io.firetail.logging.servlet.SpringResponseWrapper
import io.firetail.logging.util.StringUtils
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

@ConditionalOnProperty("logging.firetail.enabled")
class FiretailTemplate(private val firetailConfig: FiretailConfig) {

    private val objectMapper = ObjectMapper()
    private val stringUtils: StringUtils = StringUtils()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(this::class.java)
    }

    fun send(fireTailLog: FiretailLog) {
        post(objectMapper.writeValueAsString(fireTailLog))
    }

    private fun post(jsonBody: String) {
        // Set up the connection for a POST request
        val uploadUrl = firetailConfig.url + firetailConfig.logsBulk
        val connection = URL(uploadUrl).openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = false
        connection.setRequestProperty(firetailConfig.key, firetailConfig.apiKey)
        connection.setRequestProperty("CONTENT-TYPE", "application/nd-json")

        // Write the JSON body to the request
        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.writeBytes(jsonBody)
        outputStream.flush()
        outputStream.close()
    }

    fun logRequest(wrappedRequest: SpringRequestWrapper) =
        if (firetailConfig.logHeaders) {
            logWithHeaders(wrappedRequest)
        } else {
            logNoHeaders(wrappedRequest)
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
        if (firetailConfig.logHeaders) {
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
}
