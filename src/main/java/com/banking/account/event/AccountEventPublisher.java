package com.banking.account.event;

/**
 * Publicador de eventos de dominio (Parte III, Kafka).
 * Tópico: banking.account.events.v1
 */
@FunctionalInterface
public interface AccountEventPublisher {

    void publish(String eventType, String payloadJson);
}
