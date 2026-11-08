package com.banking.account.repository;

import com.banking.account.document.AccountDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AccountRepository extends ReactiveMongoRepository<AccountDocument, UUID> {

    Mono<AccountDocument> findByAccountNumber(String accountNumber);

    Flux<AccountDocument> findByCustomerIdAndAccountType(UUID customerId, String accountType);

    Flux<AccountDocument> findByCustomerIdAndAccountTypeAndStatus(
            UUID customerId, String accountType, String status);

    Flux<AccountDocument> findByCustomerId(UUID customerId);

    Mono<Boolean> existsByAccountNumber(String accountNumber);
}
