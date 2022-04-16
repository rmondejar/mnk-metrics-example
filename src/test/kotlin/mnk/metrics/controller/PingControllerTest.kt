package mnk.metrics.controller

import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
open class PingControllerTest {

    @Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    @Order(1)
    @DisplayName("Metrics Endpoint Successful Test")
    fun verifyMetricsEndpointTest() {
        // when:
        val request: HttpRequest<Any> = HttpRequest.GET("/metrics")
        val rsp = client.toBlocking().exchange(
            request, Argument.of(Map::class.java)
        )

        // then: 'the endpoint can be accessed'
        assertNotNull(rsp)
        assertEquals(HttpStatus.OK, rsp.status)
        assertNotNull(rsp.body())

        // when:
        val metrics = rsp.body()["names"] as ArrayList<*>

        // then: 'expected metrics are found'
        assertTrue(metrics.contains("jvm.memory.max"))
        assertTrue(metrics.contains("system.cpu.usage"))
        assertFalse(metrics.contains("rest.ping"))
    }

    @Test
    @Order(2)
    @DisplayName("Prometheus Endpoint Successful Test")
    fun verifyPrometheusEndpointTest() {
        // when:
        val request: HttpRequest<Any> = HttpRequest.GET("/prometheus")
        val rsp = client.toBlocking().exchange(
            request, Argument.of(String::class.java)
        )
        // then: 'the endpoint can be accessed'
        assertNotNull(rsp)
        assertEquals(HttpStatus.OK, rsp.status)

        // when:
        val body: String = rsp.body() ?: ""

        // then:
        assertTrue(body.isNotBlank())
        assertTrue(body.contains("jvm"))
        assertTrue(body.contains("executor"))
        assertTrue(body.contains("http"))
        assertFalse(body.contains("rest_ping"))
    }

    @Test
    @Order(3)
    @DisplayName("Ping Endpoint Successful Test")
    fun verifyPingEndpointTest() {
        // given:
        val name = "test"
        // when:
        val request: HttpRequest<Any> = HttpRequest.GET("/ping/$name")
        val rsp = client.toBlocking().exchange(
            request, Argument.of(String::class.java)
        )
        val body = rsp.body()

        // then: 'the endpoint can be accessed'
        assertNotNull(rsp)
        assertEquals(HttpStatus.OK, rsp.status)
        assertNotNull(body)
        assertTrue(body == "pong $name")
    }

    @Test
    @Order(4)
    @DisplayName("Custom Metrics Successful Test")
    fun verifyCustomMetricsTest() {
        // when 'check the metrics endpoint again':
        val request: HttpRequest<Any> = HttpRequest.GET("/metrics")
        val rsp = client.toBlocking().exchange(
            request, Argument.of(Map::class.java)
        )

        // then: 'the metrics endpoint still there'
        assertNotNull(rsp)
        assertEquals(HttpStatus.OK, rsp.status)
        assertNotNull(rsp.body())

        // when:
        val metrics = rsp.body()["names"] as ArrayList<*>

        // then: 'all the metrics are found'
        assertTrue(metrics.contains("jvm.memory.max"))
        assertTrue(metrics.contains("system.cpu.usage"))
        assertTrue(metrics.contains("rest.ping"))
    }

    @Test
    @Order(5)
    @DisplayName("Prometheus Custom Metrics Successful Test")
    fun verifyPrometheusCustomMetricsTest() {
        // when:
        val request: HttpRequest<Any> = HttpRequest.GET("/prometheus")
        val rsp = client.toBlocking().exchange(
            request, Argument.of(String::class.java)
        )
        // then: 'the endpoint can be accessed'
        assertNotNull(rsp)
        assertEquals(HttpStatus.OK, rsp.status)

        // when:
        val body: String = rsp.body() ?: ""

        // then:
        assertTrue(body.isNotBlank())
        assertTrue(body.contains("jvm"))
        assertTrue(body.contains("executor"))
        assertTrue(body.contains("http"))
        assertTrue(body.contains("rest_ping"))
    }
}
