package io.firetail.logging.base

import org.springframework.context.annotation.Import

@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Import(FiretailConfig::class)
annotation class Firetail(val deploy: Boolean = true)
