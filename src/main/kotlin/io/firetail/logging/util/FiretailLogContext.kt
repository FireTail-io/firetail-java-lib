package io.firetail.logging.util

import io.firetail.logging.base.Constants.Companion.CORRELATION_ID
import io.firetail.logging.base.Constants.Companion.REQUEST_ID
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC

class FiretailLogContext(private val keyGenerator: KeyGenerator = KeyGenerator()) {
    fun generateAndSetMDC(request: HttpServletRequest) {
        MDC.clear()
        MDC.put(REQUEST_ID, getValue(request, REQUEST_ID))
        MDC.put(CORRELATION_ID, getValue(request, CORRELATION_ID))
    }

    private fun getValue(request: HttpServletRequest, key: String): String {
        return request.getHeader(key) ?: keyGenerator.generate()
    }
}
