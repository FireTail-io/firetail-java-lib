package io.firetail.logging.config

import io.firetail.logging.filter.FiretailFilter
import io.firetail.logging.filter.FiretailLogger
import io.firetail.logging.util.LogContext
import io.firetail.logging.util.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    FiretailLogger::class,
    StringUtils::class,
    LogContext::class,
    FiretailFilter::class,
)
class FiretailConfig @Autowired constructor(
    @Value("\${firetail.ignorePatterns:#null}")
    val ignorePatterns: String?,
    @Value("\${firetail.logHeaders:false}")
    val logHeaders: Boolean = false,
) {
    @Autowired
    lateinit var context: ApplicationContext
}
