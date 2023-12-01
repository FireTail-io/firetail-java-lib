package io.firetail.logging.base

import com.fasterxml.jackson.databind.ObjectMapper
import io.firetail.logging.servlet.SpringRequestWrapper
import io.firetail.logging.servlet.SpringResponseWrapper
import io.firetail.logging.util.StringUtils
import org.slf4j.LoggerFactory
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class FiretailTemplate(private val firetailConfig: FiretailConfig) {

    private val objectMapper = ObjectMapper()
    private val stringUtils: StringUtils = StringUtils()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FiretailTemplate::class.java)
        const val logRequestPrefix = "Request:"
        const val logResponsePrefix = "Response:"
    }

    fun send(fireTailLog: FiretailLog) {
        val jsonBody = objectMapper.writeValueAsString(fireTailLog)
        val connection = URL("${firetailConfig.url}${firetailConfig.logsBulk}")
            .openConnection() as HttpURLConnection
        with(connection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty(firetailConfig.key, firetailConfig.apikey)
            setRequestProperty("CONTENT-TYPE", "application/nd-json")
        }

        // Write the JSON body to the request
        val outputStream: OutputStream = connection.outputStream
        with(outputStream) {
            write(jsonBody.toByteArray(Charsets.UTF_8))
            flush()
            close()
        }
        if (connection.responseCode == HttpURLConnection.HTTP_OK) {
            LOGGER.info("Dispatched status: ${HttpURLConnection.HTTP_OK}, request: ${fireTailLog.request.resource}, correlationId: ${fireTailLog.response.headers[Constants.CORRELATION_ID]}")
            return connection.inputStream.bufferedReader().use { it.readText() }
        } else {
            LOGGER.info("Failed to dispatch request. Status code: ${connection.responseCode}, correlationId: ${fireTailLog.response.headers[Constants.CORRELATION_ID]}")
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
            "$logRequestPrefix method: ${wrappedRequest.method}, uri: ${wrappedRequest.requestURI}",
        )
    }

    private fun logWithHeaders(wrappedRequest: SpringRequestWrapper) {
        LOGGER.info(
            "$logRequestPrefix " +
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
            "$logResponsePrefix ms: $duration, status: $status, headers: ${wrappedResponse.allHeaders}",
        )
    }
}
