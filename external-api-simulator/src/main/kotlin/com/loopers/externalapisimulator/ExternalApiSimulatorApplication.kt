package com.loopers.externalapisimulator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExternalApiSimulatorApplication

fun main(args: Array<String>) {
    runApplication<ExternalApiSimulatorApplication>(*args)
}
