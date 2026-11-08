package com.banking.account.service.impl;

import com.banking.account.config.AccountProperties;
import com.banking.account.document.AccountDocument;
import com.banking.account.dto.AccountCreateRequest;
import com.banking.account.dto.AccountProfile;
import com.banking.account.dto.AccountType;
import com.banking.account.exception.ConflictException;
import com.banking.account.exception.UnprocessableEntityException;
import com.banking.account.event.AccountEventPublisher;
import com.banking.account.mapper.AccountMapper;
import com.banking.account.repository.AccountRepository;
import com.banking.account.repository.MovementRepository;
import com.banking.account.service.CardVerificationClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

class AccountServiceImplRulesTest {

    private static final UUID CUSTOMER_ID =
            UUID.fromString("5eb3b5a0-9c11-4dbf-b5a1-4a1e5b1a2a01");

    private AccountRepository accountRepository;
    private MovementRepository movementRepository;
    private CardVerificationClient cardClient;
    private AccountServiceImpl service;

    @BeforeEach
    void setUp() {
        accountRepository = Mockito.mock(AccountRepository.class);
        movementRepository = Mockito.mock(MovementRepository.class);
        cardClient = Mockito.mock(CardVerificationClient.class);
        AccountMapper mapper = new AccountMapper();
        AccountProperties properties = new AccountProperties();
        AccountEventPublisher publisher = (type, payload) -> { };
        TransferFacade transferFacade = Mockito.mock(TransferFacade.class);

        service = new AccountServiceImpl(accountRepository, movementRepository, mapper,
                properties, cardClient, publisher, transferFacade, new ObjectMapper());
    }

    private AccountCreateRequest request(AccountType type, AccountProfile profile) {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setCustomerId(CUSTOMER_ID);
        request.setAccountType(type);
        request.setProfile(profile);
        request.setCurrency("PEN");
        return request;
    }

    @Test
    void personalSecondSavingsConflicts() {
        Mockito.when(accountRepository.findByCustomerIdAndAccountType(
                        CUSTOMER_ID, AccountType.SAVINGS.name()))
                .thenReturn(Flux.just(new AccountDocument()));

        StepVerifier.create(service.create(request(AccountType.SAVINGS,
                        AccountProfile.STANDARD), "tx-1"))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void vipWithoutCreditCardFails() {
        Mockito.when(accountRepository.findByCustomerIdAndAccountType(
                        Mockito.any(), Mockito.any()))
                .thenReturn(Flux.empty());
        Mockito.when(cardClient.hasCreditCard(CUSTOMER_ID)).thenReturn(Mono.just(false));

        StepVerifier.create(service.create(request(AccountType.SAVINGS,
                        AccountProfile.VIP), "tx-2"))
                .expectError(UnprocessableEntityException.class)
                .verify();
    }

    @Test
    void pymeWithoutCreditCardFails() {
        Mockito.when(cardClient.hasCreditCard(CUSTOMER_ID)).thenReturn(Mono.just(false));

        StepVerifier.create(service.create(request(AccountType.CHECKING,
                        AccountProfile.PYME), "tx-3"))
                .expectError(UnprocessableEntityException.class)
                .verify();
    }

    @Test
    void pymeWithSavingsTypeFails() {
        StepVerifier.create(service.create(request(AccountType.SAVINGS,
                        AccountProfile.PYME), "tx-4"))
                .expectError(UnprocessableEntityException.class)
                .verify();
    }

    @Test
    void vipWithCreditCardSucceeds() {
        Mockito.when(accountRepository.findByCustomerIdAndAccountType(
                        Mockito.any(), Mockito.any()))
                .thenReturn(Flux.empty());
        Mockito.when(cardClient.hasCreditCard(CUSTOMER_ID)).thenReturn(Mono.just(true));
        Mockito.when(accountRepository.save(Mockito.any(AccountDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mockito.when(movementRepository.save(Mockito.any()))
                .thenReturn(Mono.just(new com.banking.account.document.MovementDocument()));

        StepVerifier.create(service.create(request(AccountType.SAVINGS,
                        AccountProfile.VIP), "tx-5"))
                .expectNextCount(1)
                .verifyComplete();
    }
}
