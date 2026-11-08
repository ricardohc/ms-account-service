package com.banking.account.controller;

import com.banking.account.api.AccountApi;
import com.banking.account.dto.AccountCreateRequest;
import com.banking.account.dto.AccountPage;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.AccountUpdateRequest;
import com.banking.account.dto.BalanceResponse;
import com.banking.account.dto.MovementPage;
import com.banking.account.dto.OperationAcceptedResponse;
import com.banking.account.dto.TransferRequest;
import com.banking.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountApiImpl implements AccountApi {

    private final AccountService accountService;

    @Override
    public Mono<ResponseEntity<AccountPage>> findAllAccounts(
            String xCorrelationID, String xChannelID, Integer page, Integer size,
            ServerWebExchange exchange) {
        // Listado paginado simple del servicio (contrato: AccountPage).
        return accountService.listAccounts(page, size)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(
            String xCorrelationID, String xChannelID, UUID xTransactionID,
            Mono<AccountCreateRequest> accountCreateRequest, ServerWebExchange exchange) {
        return accountCreateRequest
                .flatMap(req -> accountService.create(req, xTransactionID.toString()))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> findAccountById(
            UUID accountId, String xCorrelationID, String xChannelID,
            ServerWebExchange exchange) {
        return accountService.findById(accountId).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(
            UUID accountId, String xCorrelationID, String xChannelID,
            UUID xTransactionID, Mono<AccountUpdateRequest> accountUpdateRequest,
            ServerWebExchange exchange) {
        return accountUpdateRequest
                .flatMap(req -> accountService.update(accountId, req))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deactivateAccount(
            UUID accountId, String xCorrelationID, String xChannelID,
            UUID xTransactionID, ServerWebExchange exchange) {
        return accountService.deactivate(accountId)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    @Override
    public Mono<ResponseEntity<MovementPage>> findAccountMovements(
            UUID accountId, String xCorrelationID, String xChannelID,
            Integer page, Integer size, ServerWebExchange exchange) {
        return accountService.findMovements(accountId, page, size).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> findAccountBalance(
            UUID accountId, String xCorrelationID, String xChannelID,
            ServerWebExchange exchange) {
        return accountService.findBalance(accountId).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<OperationAcceptedResponse>> createTransfer(
            String xCorrelationID, String xChannelID, UUID xTransactionID,
            Mono<TransferRequest> transferRequest, ServerWebExchange exchange) {
        return transferRequest
                .flatMap(req -> accountService.transfer(req, xTransactionID.toString()))
                .map(response -> ResponseEntity.status(HttpStatus.ACCEPTED).body(response));
    }
}
