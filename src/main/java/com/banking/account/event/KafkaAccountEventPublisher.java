package com.banking.account.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "banking.events.enabled", havingValue = "true")
public class KafkaAccountEventPublisher implements AccountEventPublisher {

    public static final String TOPIC = "banking.account.events.v1";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(String eventType, String payloadJson) {
        kafkaTemplate.send(TOPIC, eventType, payloadJson);
        log.info("Evento {} publicado en {}", eventType, TOPIC);
    }
}