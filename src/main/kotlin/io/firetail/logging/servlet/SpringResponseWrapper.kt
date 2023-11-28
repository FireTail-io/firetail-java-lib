package io.firetail.logging.servlet

import io.firetail.logging.base.Constants.Companion.empty
import jakarta.servlet.ServletOutputStream
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponseWrapper
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.util.function.Consumer

class SpringResponseWrapper(response: HttpServletResponse?) : HttpServletResponseWrapper(response) {
    private var outputStream: ServletOutputStream? = null
    private var writer: PrintWriter? = null
    private var copier: ServletOutputStreamWrapper? = null

    override fun getOutputStream(): ServletOutputStream {
        check(writer == null) { "getWriter() has already been called on this response." }
        copier = ServletOutputStreamWrapper(response.outputStream)
        return copier!!
    }

    override fun getWriter(): PrintWriter {
        check(outputStream == null) { "getOutputStream() has already been called on this response." }
        if (writer == null) {
            copier = ServletOutputStreamWrapper(response.outputStream)
            writer = PrintWriter(OutputStreamWriter(copier!!, response.characterEncoding), true)
        }
        return writer!!
    }

    override fun flushBuffer() {
        if (writer != null) {
            writer!!.flush()
        } else if (outputStream != null) {
            copier!!.flush()
        }
    }

    val contentAsByteArray: ByteArray
        get() = if (copier != null) {
            copier!!.getCopy()
        } else {
            empty
        }
    val allHeaders: Map<String, String>
        get() {
            val headers: MutableMap<String, String> = HashMap()
            headerNames.forEach(Consumer { it: String -> headers[it] = getHeader(it) })
            return headers
        }
}
