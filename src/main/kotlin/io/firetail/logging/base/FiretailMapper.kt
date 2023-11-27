package io.firetail.logging.base

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class FiretailMapper {
    fun from(request: HttpServletRequest, response: HttpServletResponse, startTime: Long): FiretailLog {
        return FiretailLog(request = from(request), response = from(response), executionTime = startTime.toInt())
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
            uri = request.requestURI,
            ip = request.remoteAddr,
            resource = request.queryString,
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
