package io.firetail.logging

class Constants {
    companion object {
        const val REQUEST_ID = "X-Request-ID"
        const val CORRELATION_ID = "X-Correlation-ID"
        const val OP_NAME = "X-Operation-Name"
        const val RESPONSE_TIME = "X-Response-Time"
        const val RESPONSE_STATUS = "X-Response-Status"
        val empty = ByteArray(0)
    }
}
