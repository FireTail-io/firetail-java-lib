package io.firetail.logging.core

import io.firetail.logging.core.FiretailLogger.Companion.LOGGER
import io.firetail.logging.servlet.FiretailMapper
import io.firetail.logging.spring.FiretailConfig
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class FiretailBuffer(
    private val firetailConfig: FiretailConfig,
    private val firetailTemplate: FiretailTemplate,
    private val firetailMapper: FiretailMapper = FiretailMapper(),
) {
    private val buffer: MutableList<FiretailData> = mutableListOf()
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val flushCallback = mutableListOf<FiretailData>()
    private val bufferLock = ReentrantLock()

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

    // Caller sycronizes access before calling this function
    fun add(item: FiretailData) {
        bufferLock.lock()
        try {
            buffer.add(item)
            if (buffer.size >= firetailConfig.capacity) {
                flush()
            }
        } finally {
            bufferLock.unlock()
        }
    }

    // Threadsafe - write and reset the cached data
    fun flush(): String {
        bufferLock.lock()
        try {
            if (buffer.isNotEmpty()) {
                LOGGER.debug("Buffer flushing ${buffer.size}")
                val result = firetailTemplate.send(buffer)
                buffer.clear()
                return firetailMapper.getResult(result)
            }
        } finally {
            bufferLock.unlock()
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
