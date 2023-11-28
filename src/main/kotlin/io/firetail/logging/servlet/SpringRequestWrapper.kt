package io.firetail.logging.servlet

import io.firetail.logging.base.Constants.Companion.empty
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import java.util.function.Consumer

class SpringRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private var body: ByteArray

    init {
        body = try {
            request.inputStream.readBytes()
        } catch (ex: IOException) {
            empty
        }
    }

    override fun getInputStream(): ServletInputStream {
        return object : ServletInputStream() {
            override fun isFinished(): Boolean {
                return false
            }

            override fun isReady(): Boolean {
                return true
            }

            override fun setReadListener(readListener: ReadListener) {}
            var byteArray = ByteArrayInputStream(body)

            override fun read(): Int {
                return byteArray.read()
            }
        }
    }

    val allHeaders: Map<String, String>
        get() {
            val headers: MutableMap<String, String> = HashMap()
            Collections.list(headerNames)
                .forEach(Consumer { it: String -> headers[it] = getHeader(it) })
            return headers
        }
}
