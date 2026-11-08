package com.banking.account.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Verificación de tarjeta de crédito vía ms-card-service (Parte II).
 * No se lee la base de Card directamente.
 */
public interface CardVerificationClient {

    Mono<Boolean> hasCreditCard(UUID customerId);

    static CardVerificationClient fallback(UUID customerId) {
        return id -> Mono.error(new WebClientResponseException(
                HttpStatus.SERVICE_UNAVAILABLE.value(), "Service Unavailable", null, null, null));
    }
}
