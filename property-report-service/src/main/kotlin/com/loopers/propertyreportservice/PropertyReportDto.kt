package com.loopers.propertyreportservice

data class PropertyReportResponse(
    val propertyInfo: PropertyInfo,
    val landRegistry: LandRegistry,
    val marketPrice: MarketPrice,
    val amenities: Amenities,
    val news: News
)

// 각 API 응답 DTO
data class PropertyInfo(val address: String, val size: Int, val type: String)
data class LandRegistry(val officialPrice: Long, val owner: String)
data class MarketPrice(val marketPrice: Long, val trend: String)
data class Amenities(val subway: String, val school: String)
data class News(val news: List<String>)
