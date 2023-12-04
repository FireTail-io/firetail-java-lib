package io.firetail.logging.core

import io.firetail.logging.servlet.FiretailMapper
import io.firetail.logging.spring.FiretailConfig
import io.firetail.logging.util.StringUtils
import org.slf4j.LoggerFactory
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class FiretailTemplate(private val firetailConfig: FiretailConfig, private val firetailMapper: FiretailMapper) {

    private val stringUtils: StringUtils = StringUtils()

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FiretailTemplate::class.java)
        const val logRequestPrefix = "Request:"
        const val logResponsePrefix = "Response:"
    }

    fun send(fireTailData: List<FiretailData>): String {
        val jsonBody = firetailMapper.from(fireTailData)
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
            LOGGER.info("Wrote ${fireTailData.size} rows to ${firetailConfig.url}")
            return connection.inputStream.bufferedReader().readText()
        } else {
            LOGGER.error("Failed to dispatch request. Status code: ${connection.responseCode}")
            throw RuntimeException(
                "HTTP POST request failed with status code: ${connection.responseCode}, " +
                    "message: ${connection.inputStream.bufferedReader().readText()}",
            )
        }
    }
}
