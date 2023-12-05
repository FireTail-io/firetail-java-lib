package io.firetail.logging.servlet

import io.firetail.logging.core.Constants.Companion.CORRELATION_ID
import io.firetail.logging.core.Constants.Companion.OP_NAME
import io.firetail.logging.core.Constants.Companion.REQUEST_ID
import io.firetail.logging.core.FiretailBuffer
import io.firetail.logging.core.FiretailLogger
import io.firetail.logging.core.FiretailTemplate
import io.firetail.logging.spring.FiretailConfig
import io.firetail.logging.util.FiretailMDC
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
@ConditionalOnClass(FiretailConfig::class)
class FiretailFilter(
    private val firetailLogContext: FiretailMDC,
    private val firetailLogger: FiretailLogger,
    private val firetailConfig: FiretailConfig,
    private val firetailMapper: FiretailMapper,
) {
    @Autowired
    private lateinit var firetailBuffer: FiretailBuffer

    @Autowired
    lateinit var context: ApplicationContext

    @Bean
    @ConditionalOnClass(FiretailConfig::class)
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
                    firetailLogger.logRequest(wrappedRequest)
                    val wrappedResponse = SpringResponseWrapper(response)
                    try {
                        with(wrappedResponse) {
                            setHeader(REQUEST_ID, firetailLogContext.get(REQUEST_ID))
                            setHeader(CORRELATION_ID, firetailLogContext.get(CORRELATION_ID))
                        }
                        chain.doFilter(wrappedRequest, wrappedResponse)
                        val duration = System.currentTimeMillis() - startTime
                        firetailLogger.logResponse(wrappedResponse, duration = duration)
                        val firetailLog =
                            firetailMapper.from(
                                wrappedRequest,
                                wrappedResponse,
                                duration,
                            )
                        CompletableFuture.runAsync {
                            try {
                                firetailBuffer.add(firetailLog)
                            } catch (e: Exception) {
                                LOGGER.error(e.message)
                                throw e
                            }
                        }
                    } catch (e: Exception) {
                        firetailLogger.logResponse(wrappedResponse, 500, startTime)
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
            firetailLogContext.put(OP_NAME, handler.beanType.simpleName + "." + handler.method.name)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FiretailTemplate::class.java)
    }
}
