package com.banking.account.controller;

import com.banking.account.api.AccountApi;
import com.banking.account.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class AccountApiImpl implements AccountApi {

    @Override
    public Mono<ResponseEntity<AccountPage>> findAllAccounts(
            String xCorrelationID, String xChannelID, Integer page, Integer size,
            ServerWebExchange exchange) {
        AccountPage p = new AccountPage();
        p.setContent(List.of());
        p.setPage(page);
        p.setSize(size);
        p.setTotalElements(0);
        return Mono.just(ResponseEntity.ok(p));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(
            String xCorrelationID, String xChannelID, UUID xTransactionID,
            Mono<AccountCreateRequest> accountCreateRequest, ServerWebExchange exchange) {
        return accountCreateRequest.map(req -> {
            AccountResponse r = new AccountResponse();
            r.setId(UUID.randomUUID());
            r.setAccountNumber("001-0000001");
            r.setCustomerId(req.getCustomerId());
            r.setAccountType(req.getAccountType());
            r.setProfile(req.getProfile());
            r.setStatus(AccountStatus.ACTIVE);
            r.setBalance(req.getInitialAmount() != null ? req.getInitialAmount() : "0.00");
            r.setCurrency(req.getCurrency());
            r.setCreatedAt(OffsetDateTime.from(LocalDateTime.now()));
            return ResponseEntity.status(HttpStatus.CREATED).body(r);
        });
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> findAccountById(
            UUID accountId, String xCorrelationID, String xChannelID,
            ServerWebExchange exchange) {
        AccountResponse r = new AccountResponse();
        r.setId(accountId);
        r.setAccountNumber("001-0000001");
        r.setStatus(AccountStatus.ACTIVE);
        r.setBalance("1000.00");
        r.setCurrency("PEN");
        r.setCreatedAt(OffsetDateTime.from(LocalDateTime.now()));
        return Mono.just(ResponseEntity.ok(r));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(
            UUID accountId, String xCorrelationID, String xChannelID,
            UUID xTransactionID, Mono<AccountUpdateRequest> accountUpdateRequest,
            ServerWebExchange exchange) {
        return accountUpdateRequest.map(req -> {
            AccountResponse r = new AccountResponse();
            r.setId(accountId);
            r.setAccountNumber("001-0000001");
            r.setProfile(req.getProfile());
            r.setStatus(req.getStatus());
            r.setBalance("1000.00");
            r.setCurrency("PEN");
            r.setCreatedAt(OffsetDateTime.from(LocalDateTime.now()));
            return ResponseEntity.ok(r);
        });
    }

    @Override
    public Mono<ResponseEntity<Void>> deactivateAccount(
            UUID accountId, String xCorrelationID, String xChannelID,
            UUID xTransactionID, ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.noContent().build());
    }

    @Override
    public Mono<ResponseEntity<MovementPage>> findAccountMovements(
            UUID accountId, String xCorrelationID, String xChannelID,
            Integer page, Integer size, ServerWebExchange exchange) {
        MovementPage p = new MovementPage();
        p.setContent(List.of());
        p.setPage(page);
        p.setSize(size);
        p.setTotalElements(0);
        return Mono.just(ResponseEntity.ok(p));
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> findAccountBalance(
            UUID accountId, String xCorrelationID, String xChannelID,
            ServerWebExchange exchange) {
        BalanceResponse r = new BalanceResponse();
        r.setAccountId(accountId);
        r.setAvailableBalance("1000.00");
        r.setCurrency("PEN");
        r.setLastUpdated(OffsetDateTime.from(LocalDateTime.now()));
        return Mono.just(ResponseEntity.ok(r));
    }

    @Override
    public Mono<ResponseEntity<OperationAcceptedResponse>> createTransfer(
            String xCorrelationID, String xChannelID, UUID xTransactionID,
            Mono<TransferRequest> transferRequest, ServerWebExchange exchange) {
        return transferRequest.map(req -> {
            OperationAcceptedResponse r = new OperationAcceptedResponse();
            r.setOperationId(UUID.randomUUID());
            r.setStatus(OperationAcceptedResponse.StatusEnum.ACCEPTED);
            r.setMessage("Transferencia aceptada para procesamiento");
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(r);
        });
    }
}
