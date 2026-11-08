package com.banking.account.service.impl;

import com.banking.account.config.AccountProperties;
import com.banking.account.document.AccountDocument;
import com.banking.account.exception.DependencyUnavailableException;
import com.banking.account.repository.AccountRepository;
import com.banking.account.service.CardVerificationClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Verifica tarjeta de crédito llamando a ms-card-service por API (Parte II).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiCardVerificationClient implements CardVerificationClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${banking.card-service.base-url:http://ms-card-service}")
    private String cardServiceBaseUrl;

    @Value("${banking.card-service.verify-path:/api/v1/cards/customers/{customerId}}")
    private String verifyPath;

    @Override
    public Mono<Boolean> hasCreditCard(UUID customerId) {
        return webClientBuilder.baseUrl(cardServiceBaseUrl).build()
                .get()
                .uri(verifyPath, customerId)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> body != null && body.contains("CREDIT"))
                .onErrorResume(ex -> {
                    log.warn("ms-card-service no disponible: {}", ex.getMessage());
                    return Mono.error(new DependencyUnavailableException(
                            "ms-card-service is unavailable, cannot verify credit card"));
                });
    }
}
