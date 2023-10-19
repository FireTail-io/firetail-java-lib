package io.firetail.logging.servlet

import io.firetail.logging.base.Constants.Companion.CORRELATION_ID
import io.firetail.logging.base.Constants.Companion.REQUEST_ID
import org.slf4j.MDC
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class FiretailHeaderInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        with(request) {
            headers.add(CORRELATION_ID, MDC.get(CORRELATION_ID))
            headers.add(REQUEST_ID, MDC.get(REQUEST_ID))
        }
        return execution.execute(request, body)
    }
}
