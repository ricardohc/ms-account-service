package com.banking.account.config;

import com.banking.account.event.AccountEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EventConfig {

    /**
     * Publicador no-op cuando Kafka está deshabilitado (banking.events.enabled=false).
     * Permite desarrollo y pruebas locales sin broker.
     */
    @Bean
    @ConditionalOnProperty(name = "banking.events.enabled", havingValue = "false", matchIfMissing = true)
    public AccountEventPublisher noopAccountEventPublisher() {
        return (eventType, payloadJson) -> log
                .debug("Event {} skipped (Kafka disabled): {}", eventType, payloadJson);
    }
}
