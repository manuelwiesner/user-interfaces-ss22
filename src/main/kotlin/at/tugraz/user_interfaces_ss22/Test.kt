package at.tugraz.user_interfaces_ss22

import kotlin.system.measureNanoTime
import kotlin.time.*
import kotlin.time.Duration.Companion.nanoseconds

fun main(args: Array<String>) {
    // TODO: Used for testing
    ESP32(8080).use { esp32 ->

        esp32.read() // discard initial read since it includes connection time

        val packetEvery = 200.toDuration(DurationUnit.MILLISECONDS)

        var totalDuration = Duration.ZERO
        var iterations = 0

        while (true) {
            val start = System.nanoTime()

            val duration = measureNanoTime {
                val packet = esp32.read()
                println("Received: $packet")
            }.nanoseconds

            totalDuration = totalDuration.plus(duration)
            iterations++

            println("Took $duration, average ${totalDuration.div(iterations)}")

            while (System.nanoTime() - start < packetEvery.inWholeNanoseconds) {
                print('.')
                Thread.sleep(1)
            }
            println()
        }
    }
}
