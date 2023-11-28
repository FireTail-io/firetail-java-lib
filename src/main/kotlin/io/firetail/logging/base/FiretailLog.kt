package io.firetail.logging.base

import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.time.ZoneOffset

data class FiretailLog(
    val version: String = "1.0.0-alpha",
    val dateCreated: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val executionTime: Int = 0,
    val request: FtRequest,
    val response: FtResponse,
)

data class FtRequest(
    val httpProtocol: String = "HTTP",
    val method: String = "GET",
    val body: String = "",
    val headers: Map<String, List<String>> = mapOf(),
    val ip: String,
    val resource: String?,
    val uri: String,
)

data class FtResponse(
    val statusCode: Int = HttpStatus.OK.value(),
    val body: String = "",
    val headers: Map<String, List<String>> = mapOf(),
)
