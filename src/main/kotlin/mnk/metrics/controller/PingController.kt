package mnk.metrics.controller

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank
import kotlin.collections.mapOf

@Validated
@Controller("/ping")
open class PingController {
    private val logger: Logger = LoggerFactory.getLogger(PingController::class.java)
    @Inject lateinit var meterRegistry: MeterRegistry

    @Get("/{name}")
    fun ping(@NotBlank name: String): String {
        meterRegistry
            .counter("rest.ping", "controller", "index", "action", "ping")
            .increment()
        logger.info("{}", mapOf("name" to name))
        return "pong $name"
    }
}
