package io.firetail.logging.servlet

import com.fasterxml.jackson.databind.ObjectMapper
import io.firetail.logging.core.FiretailData
import io.firetail.logging.core.FtRequest
import io.firetail.logging.core.FtResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.HashMap

class FiretailMapper {
    private val objectMapper = ObjectMapper()
    fun from(request: HttpServletRequest, response: HttpServletResponse, executionTime: Long): FiretailData {
        return FiretailData(request = from(request), response = from(response), executionTime = executionTime.toInt())
    }

    fun from(request: HttpServletRequest): FtRequest {
        val headers = request.headerNames
            .toList()
            .mapIndexed { _, value -> value to listOf(request.getHeader(value)) }
            .toMap()

        return FtRequest(
            httpProtocol = request.protocol,
            method = request.method,
            headers = headers,
            ip = request.remoteAddr,
            resource = request.requestURI,
            uri = request.requestURL.toString(), // FT calls the defines the URI as URL.
        )
    }

    fun from(response: HttpServletResponse): FtResponse {
        val headers = response.headerNames
            .mapIndexed { _, value -> value to listOf(response.getHeader(value)) }
            .toMap()
        return FtResponse(
            statusCode = response.status,
            body = "",
            headers = headers,
        )
    }

    fun getResult(result: String): String {
        return objectMapper.readValue(result, HashMap::class.java)
            .get("message") as String
    }

    fun from(fireTailData: List<FiretailData>): String {
        return fireTailData.joinToString("\n") { objectMapper.writeValueAsString(it) }
    }
}
