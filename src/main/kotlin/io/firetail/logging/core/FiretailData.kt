package io.firetail.logging.core

import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.time.ZoneOffset

data class FiretailData(
    val version: String = "1.0.0-alpha",
    val dateCreated: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000L,
    val executionTime: Int = 0,
    val request: FtRequest = FtRequest(),
    val response: FtResponse = FtResponse(),
)

data class FtRequest(
    val httpProtocol: String = "HTTP",
    val method: String = "GET",
    val body: String = "",
    val headers: Map<String, List<String>> = mapOf(),
    val ip: String = "127.0.0.1",
    val resource: String? = "",
    val uri: String = "/",
)

data class FtResponse(
    val statusCode: Int = HttpStatus.OK.value(),
    val body: String = "",
    val headers: Map<String, List<String>> = mapOf(),
)
