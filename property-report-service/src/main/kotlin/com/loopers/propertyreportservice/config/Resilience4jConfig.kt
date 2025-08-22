package com.loopers.propertyreportservice.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.core.registry.EntryAddedEvent
import io.github.resilience4j.core.registry.EntryRemovedEvent
import io.github.resilience4j.core.registry.EntryReplacedEvent
import io.github.resilience4j.core.registry.RegistryEventConsumer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CustomRegistryEventConsumer : RegistryEventConsumer<CircuitBreaker> {
    companion object {
        private val log = LoggerFactory.getLogger(CustomRegistryEventConsumer::class.java)
    }

    override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<CircuitBreaker>) {
        entryAddedEvent.addedEntry.eventPublisher
            .onFailureRateExceeded { event ->
                log.warn(
                    "{} failure rate {}%",
                    event.circuitBreakerName,
                    event.failureRate
                )
            }
            .onError { event -> log.error("{} ERROR!!", event.circuitBreakerName) }
            .onStateTransition { event ->
                log.info(
                    "{} state {} -> {}",
                    event.circuitBreakerName,
                    event.stateTransition.fromState,
                    event.stateTransition.toState
                )
            }
    }

    override fun onEntryRemovedEvent(entryRemoveEvent: EntryRemovedEvent<CircuitBreaker>) {
        // 엔트리 삭제시 발생시킬 이벤트가 있으면 구현
    }

    override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<CircuitBreaker>) {
        // 엔트리 교체시 발생시킬 이벤트가 있으면 구현
    }
}
