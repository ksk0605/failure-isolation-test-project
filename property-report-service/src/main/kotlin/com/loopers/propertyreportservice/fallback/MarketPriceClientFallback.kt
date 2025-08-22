package com.loopers.propertyreportservice.fallback

import com.loopers.propertyreportservice.MarketPrice
import com.loopers.propertyreportservice.MarketPriceClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MarketPriceClientFallback : MarketPriceClient {
    override fun getMarketPrice(address: String): MarketPrice {
        log.warn("MarketPriceClient Fallback 실행. 주소: $address")
        return MarketPrice(marketPrice = 0, trend = "정보 조회 불가")
    }

    companion object {
        private val log = LoggerFactory.getLogger(MarketPriceClientFallback::class.java)
    }
}
