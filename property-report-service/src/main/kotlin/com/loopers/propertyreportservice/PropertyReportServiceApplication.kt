package com.loopers.propertyreportservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class PropertyReportServiceApplication

fun main(args: Array<String>) {
    runApplication<PropertyReportServiceApplication>(*args)
}
