package io.firetail.logging.servlet

import io.firetail.logging.base.Constants.Companion.CORRELATION_ID
import io.firetail.logging.base.Constants.Companion.OP_NAME
import io.firetail.logging.base.Constants.Companion.REQUEST_ID
import io.firetail.logging.base.FiretailConfig
import io.firetail.logging.base.FiretailMapper
import io.firetail.logging.base.FiretailTemplate
import io.firetail.logging.util.FiretailLogContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
@ConditionalOnProperty("logging.firetail.enabled")
class FiretailFilter(
    private val firetailLogContext: FiretailLogContext,
    private val firetailConfig: FiretailConfig,
    private val firetailMapper: FiretailMapper,
) {
    @Autowired
    private lateinit var firetailTemplate: FiretailTemplate

    @Autowired
    lateinit var context: ApplicationContext

    @Bean
    fun firetailRequestFilter(): OncePerRequestFilter {
        return object : OncePerRequestFilter() {
            override fun doFilterInternal(
                request: HttpServletRequest,
                response: HttpServletResponse,
                chain: FilterChain,
            ) {
                if (firetailConfig.ignorePatterns != null && request.requestURI.matches(firetailConfig.ignorePatterns!!.toRegex())) {
                    chain.doFilter(request, response)
                } else {
                    firetailLogContext.generateAndSetMDC(request)
                    try {
                        getHandlerMethod(request)
                    } catch (e: Exception) {
                        LOGGER.trace("Cannot get handler method")
                    }
                    val startTime = System.currentTimeMillis()
                    val wrappedRequest = SpringRequestWrapper(request)
                    firetailTemplate.logRequest(wrappedRequest)
                    val wrappedResponse = SpringResponseWrapper(response)
                    try {
                        with(wrappedResponse) {
                            setHeader(REQUEST_ID, MDC.get(REQUEST_ID))
                            setHeader(CORRELATION_ID, MDC.get(CORRELATION_ID))
                        }
                        chain.doFilter(wrappedRequest, wrappedResponse)
                        firetailTemplate.logResponse(startTime, wrappedResponse)
                        val firetailLog = firetailMapper.from(request, response, startTime)
                    } catch (e: Exception) {
                        firetailTemplate.logResponse(startTime, wrappedResponse, 500)
                        throw e
                    }
                }
            }
        }
    }

    private fun getHandlerMethod(request: HttpServletRequest) {
        val mappings = context.getBean("requestMappingHandlerMapping")
            as RequestMappingHandlerMapping
        val nullableHandler = mappings.getHandler(request)
        if (Objects.nonNull(nullableHandler)) {
            val handler = nullableHandler?.handler as HandlerMethod
            MDC.put(OP_NAME, handler.beanType.simpleName + "." + handler.method.name)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FiretailTemplate::class.java)
    }
}
