package com.banking.account.service;

import com.banking.account.dto.AccountCreateRequest;
import com.banking.account.dto.AccountPage;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.AccountUpdateRequest;
import com.banking.account.dto.BalanceResponse;
import com.banking.account.dto.MovementPage;
import com.banking.account.dto.TransferRequest;
import com.banking.account.dto.OperationAcceptedResponse;
import reactor.core.publisher.Mono;

public interface AccountService {

    Mono<AccountResponse> create(AccountCreateRequest request, String transactionId);

    Mono<AccountResponse> findById(java.util.UUID accountId);

    Mono<AccountResponse> update(java.util.UUID accountId, AccountUpdateRequest request);

    Mono<Void> deactivate(java.util.UUID accountId);

    Mono<MovementPage> findMovements(java.util.UUID accountId, int page, int size);

    Mono<BalanceResponse> findBalance(java.util.UUID accountId);

    Mono<OperationAcceptedResponse> transfer(TransferRequest request, String transactionId);

    Mono<AccountPage> listAccounts(int page, int size);
}
