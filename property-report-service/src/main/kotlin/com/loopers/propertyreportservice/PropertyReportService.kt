package com.loopers.propertyreportservice

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PropertyReportService(
    private val propertyInfoClient: PropertyInfoClient,
    private val landRegistryClient: LandRegistryClient,
    private val marketPriceClient: MarketPriceClient,
    private val amenitiesClient: AmenitiesClient,
    private val newsClient: NewsClient
) {
    fun generateReport(address: String): PropertyReportResponse {
        log.info("리포트 생성 시작: $address")

        // 5개의 API를 순차적으로 호출
        val propertyInfo = propertyInfoClient.getPropertyInfo(address)
        val landRegistry = landRegistryClient.getLandRegistry(address) // 여기서 3초 지연 발생
        val marketPrice = marketPriceClient.getMarketPrice(address)   // 여기서 간헐적 실패 발생
        val amenities = amenitiesClient.getAmenities(address)
        val news = newsClient.searchNews(address)

        log.info("모든 API 호출 완료")

        return PropertyReportResponse(
            propertyInfo = propertyInfo,
            landRegistry = landRegistry,
            marketPrice = marketPrice,
            amenities = amenities,
            news = news
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(PropertyReportService::class.java)
    }
}
