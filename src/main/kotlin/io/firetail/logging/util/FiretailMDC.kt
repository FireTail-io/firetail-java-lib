package io.firetail.logging.util

import io.firetail.logging.core.Constants.Companion.CORRELATION_ID
import io.firetail.logging.core.Constants.Companion.REQUEST_ID
import jakarta.servlet.http.HttpServletRequest

class FiretailMDC(private val keyGenerator: KeyGenerator = KeyGenerator()) {

    private val contextData: ThreadLocal<MutableMap<String, String>> = ThreadLocal.withInitial { mutableMapOf() }

    fun put(key: String, value: String) {
        contextData.get()[key] = value
    }

    fun get(key: String): String? {
        return contextData.get()[key]
    }

    fun clear() {
        contextData.remove()
    }

    fun generateAndSetMDC(request: HttpServletRequest) {
        clear()
        put(REQUEST_ID, getValue(request, REQUEST_ID))
        put(CORRELATION_ID, getValue(request, CORRELATION_ID))
    }

    private fun getValue(request: HttpServletRequest, key: String): String {
        return request.getHeader(key) ?: keyGenerator.generate()
    }
}
