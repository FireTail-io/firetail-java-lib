package io.firetail.logging.base

import java.time.LocalDateTime
import java.time.ZoneOffset

data class FiretailLog(
    val version: String = "1.0.0-alpha",
    val dateCreated: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val executionTime: Int,
    val request: FtRequest,
    val response: FtResponse,
)

data class FtRequest(
    val headers: Map<String, List<String>>,
    val httpProtocol: String,
    val method: String,
    val body: String = "",
    val ip: String,
    val resource: String,
    val uri: String,
)

data class FtResponse(
    val statusCode: Int,
    val body: String,
    val headers: Map<String, List<String>>,
)
