package com.loopers.propertyreportservice

import com.loopers.propertyreportservice.fallback.LandRegistryClientFallback
import com.loopers.propertyreportservice.fallback.MarketPriceClientFallback
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "property-info", url = "http://localhost:8081/api/internal")
interface PropertyInfoClient {
    @GetMapping("/property-info/{address}")
    fun getPropertyInfo(@PathVariable address: String): PropertyInfo
}

@FeignClient(
    name = "land-registry",
    url = "http://localhost:8081/api/gov",
    fallback = LandRegistryClientFallback::class // Fallback 클래스 지정
)
interface LandRegistryClient {
    @GetMapping("/land-registry/{address}")
    fun getLandRegistry(@PathVariable address: String): LandRegistry
}

@FeignClient(
    name = "market-price",
    url = "http://localhost:8081/api/startup",
    fallback = MarketPriceClientFallback::class // Fallback 클래스 지정
)
interface MarketPriceClient {
    @GetMapping("/market-price/{address}")
    fun getMarketPrice(@PathVariable address: String): MarketPrice
}

@FeignClient(name = "amenities", url = "http://localhost:8081/api")
interface AmenitiesClient {
    @GetMapping("/map/amenities/{address}")
    fun getAmenities(@PathVariable address: String): Amenities
}

@FeignClient(name = "news", url = "http://localhost:8081/api")
interface NewsClient {
    @GetMapping("/news/search")
    fun searchNews(@RequestParam query: String): News
}
