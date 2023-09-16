package io.firetail.logging.util;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.util.UUID;

public class UniqueIDGenerator {

    private final String requestIdHeaderName;
    private final String correlationIdHeaderName;

    public UniqueIDGenerator(String requestIdHeaderName, String correlationIdHeaderName) {
        this.requestIdHeaderName = requestIdHeaderName;
        this.correlationIdHeaderName = correlationIdHeaderName;
    }

    public void generateAndSetMDC(HttpServletRequest request) {
        String requestId = request.getHeader(requestIdHeaderName);
        if (requestId == null)
            requestId = UUID.randomUUID().toString();
        MDC.put(requestIdHeaderName, requestId);

        String correlationId = request.getHeader(correlationIdHeaderName);
        if (correlationId == null)
            correlationId = UUID.randomUUID().toString();
        MDC.put(correlationIdHeaderName, correlationId);
    }

}
