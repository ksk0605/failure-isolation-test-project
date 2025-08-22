package com.loopers.externalapisimulator

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.atomic.AtomicLong

@RestController
@RequestMapping("/api")
class ExternalApiSimulatorController {
    private val log = LoggerFactory.getLogger(this::class.java)

    // --- 상태 제어를 위한 변수들 ---
    // market-price API의 현재 상태 (기본값: 장애)
    private var isMarketPriceApiHealthy = false
    // 각 API별 호출 횟수를 기록할 카운터
    private val propertyInfoCounter = AtomicLong(0)
    private val landRegistryCounter = AtomicLong(0)
    private val marketPriceCounter = AtomicLong(0)
    private val amenitiesCounter = AtomicLong(0)
    private val newsCounter = AtomicLong(0)

    // === 상태 제어용 엔드포인트 ===
    @PostMapping("/control/recover/{apiName}")
    fun recoverApi(@PathVariable apiName: String): ResponseEntity<String> {
        return when (apiName) {
            "market-price" -> {
                isMarketPriceApiHealthy = true
                val message = "OK. 'market-price' API is now recovered."
                log.warn("### {}", message)
                ResponseEntity.ok(message)
            }
            else -> ResponseEntity.badRequest().body("Unknown API name: $apiName")
        }
    }

    @PostMapping("/control/break/{apiName}")
    fun breakApi(@PathVariable apiName: String): ResponseEntity<String> {
        return when (apiName) {
            "market-price" -> {
                isMarketPriceApiHealthy = false
                val message = "OK. 'market-price' API is now broken."
                log.warn("### {}", message)
                ResponseEntity.ok(message)
            }
            else -> ResponseEntity.badRequest().body("Unknown API name: $apiName")
        }
    }

    // 1. 건축물대장 정보 API (내부 MSA) - 빠르고 안정적
    @GetMapping("/internal/property-info/{address}")
    fun getPropertyInfo(@PathVariable address: String): Map<String, Any> {
        val count = propertyInfoCounter.incrementAndGet()
        log.info("[property-info] Request #{}: address={}", count, address)
        return mapOf("address" to address, "size" to 110, "type" to "아파트")
    }

    // 2. 국토교통부 실거래가 API - 매우 느림
    @GetMapping("/gov/land-registry/{address}")
    fun getLandRegistry(@PathVariable address: String): Map<String, Any> {
        val count = landRegistryCounter.incrementAndGet()
        log.info("[land-registry] Request #{}: address={}. Simulating 3s delay...", count, address)
        Thread.sleep(3000)
        log.info("[land-registry] Request #{} finished.", count)
        return mapOf("officialPrice" to 10_0000_0000, "owner" to "홍길동")
    }

    // 3. 상업용 부동산 시세 API - 상태에 따라 실패/성공
    @GetMapping("/startup/market-price/{address}")
    fun getMarketPrice(@PathVariable address: String): Map<String, Any> {
        val count = marketPriceCounter.incrementAndGet()
        log.info("[market-price] Request #{}: address={}. Current state: {}", count, address, if(isMarketPriceApiHealthy) "HEALTHY" else "BROKEN")

        if (!isMarketPriceApiHealthy) {
            // 장애 상태일 경우 503 에러 발생
            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "일시적인 서버 오류")
        }

        return mapOf("marketPrice" to 12_0000_0000, "trend" to "상승")
    }

    // 4. 지도 API
    @GetMapping("/map/amenities/{address}")
    fun getAmenities(@PathVariable address: String): Map<String, Any> {
        val count = amenitiesCounter.incrementAndGet()
        log.info("[amenities-map] Request #{}: address={}", count, address)
        return mapOf("subway" to "강남역", "school" to "역삼초등학교")
    }

    // 5. 뉴스 검색 API
    @GetMapping("/news/search")
    fun searchNews(@RequestParam query: String): Map<String, Any> {
        val count = newsCounter.incrementAndGet()
        log.info("[news-search] Request #{}: query={}", count, query)
        return mapOf("news" to listOf("${query} 인근 재개발 계획 발표", "GTX 노선 확정"))
    }

//    // 2. 국토교통부 실거래가 API - 매우 느림
//    @GetMapping("/gov/land-registry/{address}")
//    fun getLandRegistry(@PathVariable address: String): Map<String, Any> {
//        Thread.sleep(3000) // 3초 지연 시뮬레이션
//        return mapOf("officialPrice" to 10_0000_0000, "owner" to "홍길동")
//    }
//
//    private val requestCount = java.util.concurrent.atomic.AtomicInteger(0)
//
//    // 3. 상업용 부동산 시세 API - 3번에 한번씩 실패
//    @GetMapping("/startup/market-price/{address}")
//    fun getMarketPrice(@PathVariable address: String): Map<String, Any> {
//        if (requestCount.incrementAndGet() % 3 == 0) {
//            throw ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "일시적인 서버 오류")
//        }
//        return mapOf("marketPrice" to 12_0000_0000, "trend" to "상승")
//    }
//
//    // 4. 지도 API - 안정적
//    @GetMapping("/map/amenities/{address}")
//    fun getAmenities(@PathVariable address: String): Map<String, Any> {
//        return mapOf("subway" to "강남역", "school" to "역삼초등학교")
//    }
//
//    // 5. 뉴스 검색 API - 안정적
//    @GetMapping("/news/search")
//    fun searchNews(@RequestParam query: String): Map<String, Any> {
//        return mapOf("news" to listOf("${query} 인근 재개발 계획 발표", "GTX 노선 확정"))
//    }
}


