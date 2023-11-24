package io.firetail.logging.base

import java.time.LocalDateTime
import java.time.ZoneOffset

data class FireTailLog(
    val version: String = "1.0.0-alpha",
    val dateCreated: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val executionTime: Int,
    val request: Request,
    val response: Response,
)

data class Request(
    val headers: Headers,
    val httpProtocol: String,
    val method: String,
    val body: String = "",
    val ip: String,
    val resource: String,
    val uri: String,
)

data class Headers(
    val key: List<String>,
)

data class Response(
    val statusCode: Int,
    val body: String,
    val headers: Headers,
)
