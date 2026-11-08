package com.banking.account.service.impl;

import com.banking.account.config.AccountProperties;
import com.banking.account.document.AccountDocument;
import com.banking.account.dto.TransferRequest;
import com.banking.account.event.AccountEventPublisher;
import com.banking.account.exception.BadRequestException;
import com.banking.account.exception.ConflictException;
import com.banking.account.exception.NotFoundException;
import com.banking.account.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Fachada de transferencias: delega la orquestación a ms-transaction-service.
 * No mueve saldo localmente.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransferFacade {

    private final AccountRepository accountRepository;
    private final WebClient.Builder webClientBuilder;
    private final AccountEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final AccountProperties properties;

    @Value("${banking.transaction-service.base-url:http://ms-transaction-service}")
    private String transactionServiceBaseUrl;

    @TimeLimiter(name = "transactionService")
    public Mono<com.banking.account.dto.OperationAcceptedResponse> transfer(
            TransferRequest request, String transactionId) {

        // 1. Validar que source y target no sean la misma cuenta (HTTP 400)
        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            return Mono.error(new BadRequestException(
                    "Source account and target account cannot be the same"));
        }

        Mono<AccountDocument> source = accountRepository.findById(request.getSourceAccountId())
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Source account not found: " + request.getSourceAccountId())));

        Mono<AccountDocument> target = accountRepository.findById(request.getTargetAccountId())
                .switchIfEmpty(Mono.error(new NotFoundException(
                        "Target account not found: " + request.getTargetAccountId())));

        return Mono.zip(source, target)
                .flatMap(tuple -> validate(tuple.getT1(), tuple.getT2(), request))
                .then(callTransactionService(request, transactionId));
    }

    private Mono<Void> validate(AccountDocument source, AccountDocument target,
                                TransferRequest request) {
        return Mono.defer(() -> {
            // 2. Validar FIXED_TERM (HTTP 400)
            if ("FIXED_TERM".equals(source.getAccountType())) {
                return Mono.error(new BadRequestException(
                        "Source account with id " + source.getId() + " cannot be FIXED_TERM for transfers"));
            }
            if ("FIXED_TERM".equals(target.getAccountType())) {
                return Mono.error(new BadRequestException(
                        "Target account with id " + target.getId() + " cannot be FIXED_TERM for transfers"));
            }

            // 3. Validar que ambas cuentas estén ACTIVE (HTTP 409)
            if (!"ACTIVE".equals(source.getStatus())) {
                return Mono.error(new ConflictException(
                        "Source account is " + source.getStatus()));
            }
            if (!"ACTIVE".equals(target.getStatus())) {
                return Mono.error(new ConflictException(
                        "Target account is not ACTIVE"));
            }

            // 4. Validar saldo suficiente en la cuenta origen (HTTP 409)
            BigDecimal amount = new BigDecimal(request.getAmount());
            if (source.getBalance() == null || source.getBalance().compareTo(amount) < 0) {
                return Mono.error(new ConflictException("Insufficient funds"));
            }
            return Mono.empty();
        });
    }

    private Mono<com.banking.account.dto.OperationAcceptedResponse> callTransactionService(
            TransferRequest request, String transactionId) {
        return webClientBuilder.baseUrl(transactionServiceBaseUrl).build()
                .post()
                .uri("/api/v1/transactions")
                .header("X-Transaction-ID", transactionId)
                .header("X-Channel-ID", "ACCOUNT_FACADE")
                .bodyValue(toTransactionPayload(request, transactionId))
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(30))
                .map(response -> {
                    com.banking.account.dto.OperationAcceptedResponse accepted =
                            new com.banking.account.dto.OperationAcceptedResponse();
                    Object operationId = response.get("transactionId");
                    accepted.setOperationId(operationId != null
                            ? UUID.fromString(String.valueOf(operationId)) : UUID.randomUUID());
                    accepted.setStatus(com.banking.account.dto.OperationAcceptedResponse.StatusEnum.ACCEPTED);
                    accepted.setMessage("Transfer accepted for processing by Transaction Service");
                    return accepted;
                })
                .onErrorResume(ex -> {
                    log.error("Error delegating to transaction-service: {}", ex.getMessage());
                    return Mono.error(new ConflictException(
                            "Transaction Service is unavailable, transfer not accepted"));
                });
    }

    private Map<String, Object> toTransactionPayload(TransferRequest request, String transactionId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("transactionId", transactionId);
        payload.put("type", "TRANSFER");
        payload.put("sourceAccountId", String.valueOf(request.getSourceAccountId()));
        payload.put("targetAccountId", String.valueOf(request.getTargetAccountId()));
        payload.put("amount", request.getAmount());
        payload.put("currency", request.getCurrency());
        if (request.getDescription() != null) {
            payload.put("description", request.getDescription());
        }
        return payload;
    }
}