package io.firetail.logging.base

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

class FiretailMapper {
    fun from(request: HttpServletRequest, response: HttpServletResponse, executionTime: Long): FiretailLog {
        return FiretailLog(request = from(request), response = from(response), executionTime = executionTime.toInt())
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
}
