package com.loopers.propertyreportservice.fallback

import com.loopers.propertyreportservice.LandRegistry
import com.loopers.propertyreportservice.LandRegistryClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LandRegistryClientFallback : LandRegistryClient {
    override fun getLandRegistry(address: String): LandRegistry {
        log.warn("LandRegistryClient Fallback 실행. 주소: $address")
        // 실패 시 반환할 기본(default) 데이터
        return LandRegistry(officialPrice = 0, owner = "정보 조회 불가")
    }

    companion object {
        private val log = LoggerFactory.getLogger(LandRegistryClientFallback::class.java)
    }
}
