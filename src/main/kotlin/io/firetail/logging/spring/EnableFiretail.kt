package io.firetail.logging.spring

import org.springframework.context.annotation.Import

/**
 * Include this annotation to enable deployment of Firetail support
 * classes
 */
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(FiretailConfig::class)
annotation class EnableFiretail
