package com.banking.account.repository;

import com.banking.account.document.MovementDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MovementRepository extends ReactiveMongoRepository<MovementDocument, UUID> {

    Flux<MovementDocument> findByAccountIdOrderByTimestampDesc(UUID accountId, Pageable pageable);

    Mono<Long> countByAccountId(UUID accountId);
}
