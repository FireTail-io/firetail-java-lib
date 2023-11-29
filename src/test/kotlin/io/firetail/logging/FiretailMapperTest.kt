package io.firetail.logging

import io.firetail.logging.servlet.FiretailMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

class FiretailMapperTest {

    private val firetailMapper = FiretailMapper()

    @Test
    fun fromResponse() {
        val mockResponse: HttpServletResponse = Mockito.mock(HttpServletResponse::class.java)
        Mockito.`when`(mockResponse.headerNames).thenReturn(listOf(TEST))
        Mockito.`when`(mockResponse.getHeader(TEST)).thenReturn(TEST_RESULTS)
        val result = firetailMapper.from(mockResponse)
        Assertions.assertThat(result.headers)
            .isNotNull
            .hasFieldOrPropertyWithValue(TEST, listOf(TEST_RESULTS))
    }

    @Test
    fun fromRequest() {
        val mockRequest: HttpServletRequest = Mockito.mock(HttpServletRequest::class.java)

        Mockito.`when`(mockRequest.protocol).thenReturn("HTTP")
        Mockito.`when`(mockRequest.method).thenReturn("GET")
        Mockito.`when`(mockRequest.requestURI).thenReturn("/")
        Mockito.`when`(mockRequest.requestURL).thenReturn(StringBuffer().append("http://blah.com"))
        Mockito.`when`(mockRequest.remoteAddr).thenReturn("127.0.0.1")
        Mockito.`when`(mockRequest.queryString).thenReturn("123")
        Mockito.`when`(mockRequest.getHeader(TEST)).thenReturn(TEST_RESULTS)
        Mockito.`when`(mockRequest.headerNames)
            .thenReturn(Collections.enumeration(Collections.singletonList(TEST)))

        val result = firetailMapper.from(mockRequest)

        Assertions.assertThat(result.headers)
            .isNotNull
            .hasFieldOrPropertyWithValue(TEST, listOf(TEST_RESULTS))
    }

    companion object {
        private const val TEST = "X-TEST"
        private const val TEST_RESULTS = "TEST-RESULTS"
    }
}
