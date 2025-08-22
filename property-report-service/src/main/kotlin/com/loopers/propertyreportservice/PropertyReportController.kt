package com.loopers.propertyreportservice

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/property-report")
class PropertyReportController(
    private val propertyReportService: PropertyReportService
) {
    @GetMapping
    fun getReport(@RequestParam address: String): PropertyReportResponse {
        return propertyReportService.generateReport(address)
    }
}
