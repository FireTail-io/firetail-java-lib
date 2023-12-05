package io.firetail.logging.servlet

import jakarta.servlet.ServletOutputStream
import jakarta.servlet.WriteListener
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ServletOutputStreamWrapper(private val outputStream: OutputStream) : ServletOutputStream() {
    private val copy: ByteArrayOutputStream = ByteArrayOutputStream()

    override fun write(b: Int) {
        outputStream.write(b)
        copy.write(b)
    }

    fun getCopy(): ByteArray {
        return copy.toByteArray()
    }

    override fun isReady(): Boolean {
        return true
    }

    override fun setWriteListener(writeListener: WriteListener) {}
}
