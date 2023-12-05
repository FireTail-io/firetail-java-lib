package io.firetail.logging.core

class Constants {
    companion object {
        const val REQUEST_ID = "X-Request-ID"
        const val CORRELATION_ID = "X-Correlation-ID"
        const val OP_NAME = "X-Operation-Name"
        const val RESPONSE_TIME = "X-Response-Time"
        const val RESPONSE_STATUS = "X-Response-Status"
        const val AUDIT = "audit"

        // const val FIRETAIL_APPENDER_NAME = "FIRETAIL"
        val empty = ByteArray(0)
    }
}
