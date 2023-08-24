package io.firetail.logging.wrapper

import io.firetail.logging.Constants.Companion.empty
import org.apache.commons.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import java.util.function.Consumer
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class SpringRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private var body: ByteArray

    init {
        body = try {
            IOUtils.toByteArray(request.inputStream)
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
