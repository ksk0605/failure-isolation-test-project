# 장애 격리 안정성 패턴 에시 프로젝트: Resilience4j 서킷 브레이커, Timeout, Fallback

## 📖 프로젝트 소개

이 프로젝트는 MSA(마이크로서비스 아키텍처) 환경에서 외부 서비스의 장애가 우리 시스템 전체로 전파되는 것을 막는 장애격리룰 학습하고 실습하기 위해 만들어졌습니다.

"부동산 종합 리포트 서비스"라는 가상의 시나리오를 통해, Spring Boot와 Resilience4j를 사용하여 다음과 같은 문제를 해결하는 과정을 단계별로 보여줍니다.

* 이 프로젝트의 전체 구현 과정과 상세한 설명은 [블로그 글](https://velog.io/@ksk0605/%EB%82%B4-%EC%84%9C%EB%B2%84%EB%8A%94-%EC%A3%BD%EC%A7%80-%EC%95%8A%EB%8A%94%EB%8B%A4-Spring-Boot%EC%99%80-Resilience4j%EB%A1%9C-%EC%84%9C%ED%82%B7-%EB%B8%8C%EB%A0%88%EC%9D%B4%EC%BB%A4-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0)에서 확인하실 수 있습니다.
* [각종 설정](https://github.com/ksk0605/failure-isolation-test-project/blob/main/property-report-service/src/main/resources/application.yml)을 직접 수정해가면서 서버 호출을 시도하면 이해도를 높일 수 있습니다. 

-----

## 🏛️ 프로젝트 구조

이 프로젝트는 Gradle 멀티 모듈로 구성되어 있습니다.

- **`property-report-service`**: 메인 애플리케이션입니다. 외부 API를 호출하고, 서킷 브레이커와 같은 안정성 패턴이 적용되어 있습니다.
- **`external-api-simulator`**: 5개의 외부 API의 동작(정상, 응답 지연, 간헐적 실패 등)을 흉내 내는 시뮬레이터 서버입니다.

-----

## 🚀 시작하기

### 사전 요구사항

- Java 17 이상
- Gradle 8.0 이상

### 실행 방법

이 프로젝트는 2개의 애플리케이션으로 구성되어 있으므로, 각각 다른 터미널에서 실행해야 합니다.

**1. 외부 API 시뮬레이터 실행 (8081 포트)**

```bash
# 프로젝트 루트 디렉토리에서 실행
./gradlew :external-api-simulator:bootRun
```

**2. 부동산 리포트 서비스 실행 (8080 포트)**

```bash
# 다른 터미널을 열고, 프로젝트 루트 디렉토리에서 실행
./gradlew :property-report-service:bootRun
```

이제 두 서버가 모두 실행되었습니다.

-----

## 🧪 시나리오 테스트 방법

Postman이나 `curl`을 사용하여 `property-report-service`의 API를 호출하며 안정성 패턴의 효과를 직접 확인해 보세요.

**1. 기본 리포트 조회 (성공)**

`market-price` API가 정상일 때, 모든 정보가 포함된 리포트가 반환됩니다.

```bash
curl -X GET "http://localhost:8080/api/v1/property-report?address=강남구"
```

**2. 서킷 브레이커 OPEN 시뮬레이션**

`market-price` API를 의도적으로 장애 상태로 만들어 봅시다.

```bash
# 1. market-price API를 장애 상태로 변경
curl -X POST http://localhost:8081/api/control/break/market-price

# 2. 리포트 API를 여러 번(5~10회) 호출하여 서킷 브레이커를 OPEN 시킵니다.
curl -X GET "http://localhost:8080/api/v1/property-report?address=강남구"
```

- **확인 포인트:**
    - `property-report-service` 로그: `MarketPriceClientFallback`이 실행되는 `WARN` 로그와 서킷 상태가 `CLOSED -> OPEN`으로 변경되는 로그를 확인합니다.
    - `external-api-simulator` 로그: `[market-price]` API로 들어오던 요청이 어느 순간부터 더 이상 들어오지 않는 것을 확인합니다. (장애 격리)
    - API 응답: 500 에러 대신, `marketPrice` 필드가 Fallback 데이터로 채워진 200 OK 응답을 받게 됩니다.

**3. 서킷 브레이커 복구 시뮬레이션 (HALF\_OPEN → CLOSED)**

`wait-duration-in-open-state` (기본 1분)이 지난 후, 서킷은 `HALF_OPEN` 상태가 됩니다. 이때 외부 서비스가 복구되었다고 알려주고, 서킷이 다시 닫히는지 확인합니다.

```bash
# 1. (서킷이 OPEN되고 1분 후) market-price API를 정상 상태로 복구
curl -X POST http://localhost:8081/api/control/recover/market-price

# 2. 리포트 API를 다시 호출하여 탐색 요청(Probe Request)을 보냅니다.
curl -X GET "http://localhost:8080/api/v1/property-report?address=강남구"
```

- **확인 포인트:**
    - `external-api-simulator` 로그: `[market-price]` API로 딱 한 번의 요청이 들어오는 것을 확인합니다.
    - `property-report-service` 로그: 서킷 상태가 `HALF_OPEN -> CLOSED`로 변경되는 로그를 확인합니다.
    - 이후 요청은 다시 정상적으로 처리됩니다.

