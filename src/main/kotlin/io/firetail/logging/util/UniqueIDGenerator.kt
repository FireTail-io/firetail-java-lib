package io.firetail.logging.util

import io.firetail.logging.Constants.Companion.CORRELATION_ID
import io.firetail.logging.Constants.Companion.REQUEST_ID
import org.slf4j.MDC
import javax.servlet.http.HttpServletRequest

class UniqueIDGenerator(private val generator: Generator = Generator()) {
    fun generateAndSetMDC(request: HttpServletRequest) {
        MDC.clear()
        var requestId = request.getHeader(REQUEST_ID)
        if (requestId == null) requestId = generator.generate()
        MDC.put(REQUEST_ID, requestId)
        var correlationId = request.getHeader(CORRELATION_ID)
        if (correlationId == null) correlationId = generator.generate()
        MDC.put(CORRELATION_ID, correlationId)
    }
}
