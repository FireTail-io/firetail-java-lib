package io.firetail.logging.wrapper

import java.io.ByteArrayOutputStream
import java.io.OutputStream
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

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
