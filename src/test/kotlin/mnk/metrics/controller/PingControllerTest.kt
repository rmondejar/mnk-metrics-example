package mnk.metrics.controller

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@MicronautTest
open class PingControllerTest {

    @Inject
    @field:Client("/ping")
    lateinit var client: HttpClient

    @Test
    @DisplayName("Ping Endpoint Successful Test")
    fun verifyPingEndpointTest() {
        // given:
        val name = "test"
        // when:
        val request: HttpRequest<Any> = HttpRequest.GET("/$name")
        val rsp = client.toBlocking().exchange(
            request, Argument.of(String::class.java)
        )

        // then: 'the endpoint can be accessed'
        assertEquals(HttpStatus.OK, rsp.status)
        assertNotNull(rsp.body())

        // when:
        val response = rsp.body()

        // then:
        assertNotNull(response)
        assertTrue(response.equals("pong $name"))
    }
}
