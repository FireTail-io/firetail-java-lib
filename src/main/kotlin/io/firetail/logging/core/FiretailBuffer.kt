package io.firetail.logging.core

import io.firetail.logging.servlet.FiretailMapper
import io.firetail.logging.spring.FiretailConfig
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class FiretailBuffer(
    private val firetailConfig: FiretailConfig,
    private val firetailTemplate: FiretailTemplate,
    private val firetailMapper: FiretailMapper = FiretailMapper(),
) {
    private val buffer: MutableList<FiretailData> = mutableListOf()
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val flushCallback = mutableListOf<FiretailData>()

    init {
        // Schedule the periodic flush task
        scheduler.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    flush()
                }
            },
            firetailConfig.flushIntervalMillis,
            firetailConfig.flushIntervalMillis,
            TimeUnit.MILLISECONDS,
        )
    }

    fun add(item: FiretailData) {
        buffer.add(item)
        if (buffer.size >= firetailConfig.capacity) {
            flush()
        }
    }

    fun flush(): String {
        if (buffer.isNotEmpty()) {
            LOGGER.debug("Buffer flushing ${buffer.size}")
            val result = firetailTemplate.send(buffer.toList())
            buffer.clear()
            return firetailMapper.getResult(result)
        }
        return ""
    }

    fun get(): List<FiretailData> {
        return buffer.toList()
    }

    // Cleanup method to stop the scheduler
    fun stop() {
        scheduler.shutdown()
    }

    fun size(): Int {
        return buffer.size
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(FiretailBuffer::class.java)
    }
}
