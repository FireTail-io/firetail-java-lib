package io.firetail.logging.client

import io.firetail.logging.Constants.Companion.CORRELATION_ID
import io.firetail.logging.Constants.Companion.REQUEST_ID
import org.slf4j.MDC
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

class RestTemplateSetHeaderInterceptor : ClientHttpRequestInterceptor {
    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        request.headers.add(CORRELATION_ID, MDC.get(CORRELATION_ID))
        request.headers.add(REQUEST_ID, MDC.get(REQUEST_ID))
        return execution.execute(request, body)
    }
}
