package io.firetail.logging.base

import io.firetail.logging.servlet.FiretailHeaderInterceptor
import io.firetail.logging.util.FiretailLogContext
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.web.client.RestTemplate
import java.util.function.Consumer

@ConditionalOnProperty("logging.firetail.enabled")
class FiretailBeanFactory {

    @Bean
    fun firetailLogContext(): FiretailLogContext = FiretailLogContext()

    @Bean
    fun firetailTemplate(firetailConfig: FiretailConfig): FiretailTemplate {
        return FiretailTemplate(firetailConfig)
    }

    @Bean
    fun firetailHeaderInterceptor(restTemplate: RestTemplate): FiretailHeaderInterceptor {
        val ftHeader = FiretailHeaderInterceptor()
        restTemplate.interceptors.add(ftHeader)
        return ftHeader
    }

    @Bean
    fun firetailMapper(): FiretailMapper = FiretailMapper()

//    @Bean
//    fun conditionalDeploymentBeanPostProcessor(beanFactory: ConfigurableListableBeanFactory): ConditionalDeploymentBeanPostProcessor {
//        return ConditionalDeploymentBeanPostProcessor(beanFactory)
//    }

//    class ConditionalDeploymentBeanPostProcessor(private val beanFactory: ConfigurableListableBeanFactory) {
//        init {
//            scanAndDeploy()
//        }
//
//        private fun scanAndDeploy() {
//            val scanner = ClassPathScanningCandidateComponentProvider(false)
//            scanner.addIncludeFilter(AnnotationTypeFilter(Firetail::class.java))
//            scanner.findCandidateComponents("io.firetail.logging") // Specify the package to scan
//                .forEach(
//                    Consumer { beanDefinition: BeanDefinition ->
//                        val className = beanDefinition.beanClassName
//                        try {
//                            val clazz = Class.forName(className)
//                            val annotation: Firetail = clazz.getAnnotation(Firetail::class.java)
//                            if (annotation.deploy) {
//                                beanFactory.registerSingleton(className!!, clazz.getDeclaredConstructor().newInstance())
//                            }
//                        } catch (e: ClassNotFoundException) {
//                            e.printStackTrace()
//                        } catch (e: IllegalAccessException) {
//                            e.printStackTrace()
//                        } catch (e: InstantiationException) {
//                            e.printStackTrace()
//                        }
//                    },
//                )
//        }
//    }
}
