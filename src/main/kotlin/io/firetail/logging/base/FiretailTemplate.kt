package io.firetail.logging.base

import com.fasterxml.jackson.databind.ObjectMapper
import io.firetail.logging.servlet.SpringRequestWrapper
import io.firetail.logging.servlet.SpringResponseWrapper
import io.firetail.logging.util.StringUtils
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

@ConditionalOnProperty("logging.firetail.enabled")
class FiretailTemplate(private val firetailConfig: FiretailConfig) {

    private val objectMapper = ObjectMapper()
    private val stringUtils: StringUtils = StringUtils()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FiretailTemplate::class.java)
    }

    fun send(fireTailLog: FiretailLog) {
        val jsonBody = objectMapper.writeValueAsString(fireTailLog)
        // Set up the connection for a POST request
        val connection = URL("${firetailConfig.url}${firetailConfig.logsBulk}")
            .openConnection() as HttpURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty(firetailConfig.key, firetailConfig.apikey)
        connection.setRequestProperty("CONTENT-TYPE", "application/nd-json")

        // Write the JSON body to the request
        val outputStream: OutputStream = connection.outputStream
        outputStream.write(jsonBody.toByteArray(Charsets.UTF_8))
        outputStream.close()
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            LOGGER.info("Dispatched request ${fireTailLog.request.resource}")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            LOGGER.info("Failed to dispatch request. Status code ${connection.responseCode}")
            throw RuntimeException("HTTP POST request failed with status code: ${connection.responseCode}")
        }
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
