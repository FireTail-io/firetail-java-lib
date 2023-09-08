package io.firetail.logging.util

import io.firetail.logging.config.Constants.Companion.CORRELATION_ID
import io.firetail.logging.config.Constants.Companion.REQUEST_ID
import org.slf4j.MDC
import javax.servlet.http.HttpServletRequest

class LogContext(private val generator: Generator = Generator()) {
    fun generateAndSetMDC(request: HttpServletRequest) {
        MDC.clear()
        MDC.put(REQUEST_ID, getValue(request, REQUEST_ID))
        MDC.put(CORRELATION_ID, getValue(request, CORRELATION_ID))
    }

    private fun getValue(request: HttpServletRequest, key: String): String {
        return request.getHeader(key) ?: generator.generate()
    }
}
