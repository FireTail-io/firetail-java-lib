package io.firetail.logging.filter

import io.firetail.logging.Constants.Companion.CORRELATION_ID
import io.firetail.logging.Constants.Companion.OP_NAME
import io.firetail.logging.Constants.Companion.REQUEST_ID
import io.firetail.logging.Constants.Companion.RESPONSE_STATUS
import io.firetail.logging.Constants.Companion.RESPONSE_TIME
import io.firetail.logging.util.UniqueIDGenerator
import io.firetail.logging.wrapper.SpringRequestWrapper
import io.firetail.logging.wrapper.SpringResponseWrapper
import net.logstash.logback.argument.StructuredArguments
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SpringLoggerFilter(
    private val generator: UniqueIDGenerator,
    private val ignorePatterns: String?,
    private val logHeaders: Boolean,
) : OncePerRequestFilter() {
    @Autowired
    lateinit var context: ApplicationContext

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        if (ignorePatterns != null && request.requestURI.matches(ignorePatterns.toRegex())) {
            chain.doFilter(request, response)
        } else {
            generator.generateAndSetMDC(request)
            try {
                getHandlerMethod(request)
            } catch (e: Exception) {
                LOGGER.trace("Cannot get handler method")
            }
            val startTime = System.currentTimeMillis()
            val wrappedRequest = SpringRequestWrapper(request)
            if (logHeaders) {
                LOGGER.info(
                    "Request: method={}, uri={}, payload={}, headers={}, audit={}",
                    wrappedRequest.method,
                    wrappedRequest.requestURI,
                    IOUtils.toString(
                        wrappedRequest.inputStream,
                        wrappedRequest.characterEncoding,
                    ),
                    wrappedRequest.allHeaders,
                    StructuredArguments.value("audit", true),
                )
            } else {
                LOGGER.info(
                    "Request: method={}, uri={}, payload={}, audit={}",
                    wrappedRequest.method,
                    wrappedRequest.requestURI,
                    IOUtils.toString(
                        wrappedRequest.inputStream,
                        wrappedRequest.characterEncoding,
                    ),
                    StructuredArguments.value("audit", true),
                )
            }
            val wrappedResponse = SpringResponseWrapper(response)
            wrappedResponse.setHeader(REQUEST_ID, MDC.get(REQUEST_ID))
            wrappedResponse.setHeader(CORRELATION_ID, MDC.get(CORRELATION_ID))
            try {
                chain.doFilter(wrappedRequest, wrappedResponse)
            } catch (e: Exception) {
                logResponse(startTime, wrappedResponse, 500)
                throw e
            }
            logResponse(startTime, wrappedResponse, wrappedResponse.status)
        }
    }

    private fun logResponse(startTime: Long, wrappedResponse: SpringResponseWrapper, overriddenStatus: Int) {
        val duration = System.currentTimeMillis() - startTime
        wrappedResponse.characterEncoding = "UTF-8"
        if (logHeaders) {
            LOGGER.info(
                "Response({} ms): status={}, payload={}, headers={}, audit={}",
                StructuredArguments.value(RESPONSE_TIME, duration),
                StructuredArguments.value(RESPONSE_STATUS, overriddenStatus),
                IOUtils.toString(
                    wrappedResponse.contentAsByteArray,
                    wrappedResponse.characterEncoding,
                ),
                wrappedResponse.allHeaders,
                StructuredArguments.value("audit", true),
            )
        } else {
            LOGGER.info(
                "Response({} ms): status={}, payload={}, audit={}",
                StructuredArguments.value(RESPONSE_TIME, duration),
                StructuredArguments.value(RESPONSE_STATUS, overriddenStatus),
                IOUtils.toString(wrappedResponse.contentAsByteArray, wrappedResponse.characterEncoding),
                StructuredArguments.value("audit", true),
            )
        }
    }

    private fun getHandlerMethod(request: HttpServletRequest) {
        val mappings = context.getBean("requestMappingHandlerMapping") as RequestMappingHandlerMapping
        val nullableHandler = mappings.getHandler(request)
        if (Objects.nonNull(nullableHandler)) {
            val handler = nullableHandler?.handler as HandlerMethod
            MDC.put(OP_NAME, handler.beanType.simpleName + "." + handler.method.name)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SpringLoggerFilter::class.java)
    }
}
