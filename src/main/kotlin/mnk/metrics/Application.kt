package mnk.metrics

import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {
    build()
        .args(*args)
        .packages("mnk.metrics")
        .start()
}
