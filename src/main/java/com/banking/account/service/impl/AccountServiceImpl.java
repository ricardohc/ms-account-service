package com.banking.account.service.impl;

import com.banking.account.config.AccountProperties;
import com.banking.account.document.AccountDocument;
import com.banking.account.document.MovementDocument;
import com.banking.account.dto.AccountCreateRequest;
import com.banking.account.dto.AccountPage;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.AccountStatus;
import com.banking.account.dto.AccountType;
import com.banking.account.dto.AccountUpdateRequest;
import com.banking.account.dto.BalanceResponse;
import com.banking.account.dto.MovementPage;
import com.banking.account.dto.MovementResponse;
import com.banking.account.dto.OperationAcceptedResponse;
import com.banking.account.dto.TransferRequest;
import com.banking.account.event.AccountEventPublisher;
import com.banking.account.exception.ConflictException;
import com.banking.account.exception.NotFoundException;
import com.banking.account.exception.UnprocessableEntityException;
import com.banking.account.mapper.AccountMapper;
import com.banking.account.repository.AccountRepository;
import com.banking.account.repository.MovementRepository;
import com.banking.account.service.AccountService;
import com.banking.account.service.CardVerificationClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;
    private final AccountMapper mapper;
    private final AccountProperties properties;
    private final CardVerificationClient cardVerificationClient;
    private final AccountEventPublisher eventPublisher;
    private final TransferFacade transferFacade;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<AccountResponse> create(AccountCreateRequest request, String transactionId) {
        return validateCreationRules(request)
                .then(generateAccountNumber(request.getCurrency()))
                .map(accountNumber -> buildDocument(request, accountNumber))
                .flatMap(accountRepository::save)
                .flatMap(saved -> registerOpeningMovementIfAny(saved, request)
                        .then(publishEvent("AccountCreated", saved))
                        .thenReturn(mapper.toResponse(saved)));
    }

    private Mono<Void> validateCreationRules(AccountCreateRequest request) {
        AccountType type = request.getAccountType();
        com.banking.account.dto.AccountProfile profile = request.getProfile();

        Mono<Void> businessRules = Mono.defer(() -> {
            if (type == AccountType.SAVINGS || type == AccountType.FIXED_TERM) {
                if (profile == com.banking.account.dto.AccountProfile.PYME) {
                    return Mono.error(new UnprocessableEntityException(
                            "PYME profile only allows CHECKING accounts"));
                }
            }
            if (request.getInitialAmount() != null) {
                BigDecimal min = minimumOpening(profile);
                if (new BigDecimal(request.getInitialAmount()).compareTo(min) < 0) {
                    return Mono.error(new UnprocessableEntityException(
                            "Initial amount below configured minimum opening amount " + min));
                }
            }
            if (profile == com.banking.account.dto.AccountProfile.VIP
                    || profile == com.banking.account.dto.AccountProfile.PYME) {
                return cardVerificationClient.hasCreditCard(request.getCustomerId())
                        .flatMap(has -> Boolean.TRUE.equals(has) ? Mono.empty()
                                : Mono.error(new UnprocessableEntityException(
                                "Profile " + profile + " requires a bank credit card")));
            }
            return Mono.empty();
        });

        Mono<Void> typeRules = Mono.defer(() -> {
            if (type == AccountType.SAVINGS) {
                return accountRepository.findByCustomerIdAndAccountType(
                                request.getCustomerId(), AccountType.SAVINGS.name())
                        .hasElements()
                        .flatMap(exists -> exists
                                ? Mono.error(new ConflictException(
                                "PERSONAL customer already has a SAVINGS account"))
                                : Mono.empty());
            }
            return Mono.empty();
        });

        return businessRules.then(typeRules);
    }

    private BigDecimal minimumOpening(com.banking.account.dto.AccountProfile profile) {
        AccountProperties.MinimumOpening min = properties.getMinimumOpeningAmount();
        return switch (profile) {
            case VIP -> min.getVip();
            case PYME -> min.getPyme();
            default -> min.getStandard();
        };
    }

    private AccountDocument buildDocument(AccountCreateRequest request, String accountNumber) {
        return AccountDocument.builder()
                .id(UUID.randomUUID())
                .accountNumber(accountNumber)
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType().name())
                .profile(request.getProfile().name())
                .status(AccountStatus.ACTIVE.name())
                .balance(request.getInitialAmount() == null
                        ? BigDecimal.ZERO : new BigDecimal(request.getInitialAmount()))
                .currency(request.getCurrency())
                .holders(request.getHolders() == null ? List.of() : request.getHolders())
                .signers(request.getSigners() == null ? List.of() : request.getSigners())
                .monthlyMovements(0)
                .monthlyMovementsMonth(AccountDocument.currentMonth())
                .createdAt(Instant.now())
                .build();
    }

    private Mono<String> generateAccountNumber(String currency) {
        return Mono.fromSupplier(() -> {
            String number = String.format("%s-%013d",
                    currency.equals("USD") ? "002" : "001",
                    Math.abs(UUID.randomUUID().getLeastSignificantBits()) % 10_000_000_000_000L);
            return number;
        });
    }

    private Mono<Void> registerOpeningMovementIfAny(AccountDocument account,
                                                    AccountCreateRequest request) {
        if (request.getInitialAmount() == null
                || new BigDecimal(request.getInitialAmount()).compareTo(BigDecimal.ZERO) == 0) {
            return Mono.empty();
        }
        MovementDocument movement = MovementDocument.builder()
                .id(UUID.randomUUID())
                .accountId(account.getId())
                .type("DEPOSIT")
                .amount(new BigDecimal(request.getInitialAmount()))
                .balanceAfter(account.getBalance())
                .timestamp(Instant.now())
                .build();
        return movementRepository.save(movement).then();
    }

    private Mono<Void> publishEvent(String eventType, AccountDocument account) {
        return Mono.fromRunnable(() -> {
            try {
                String payload = objectMapper.writeValueAsString(java.util.Map.of(
                        "eventType", eventType,
                        "accountId", String.valueOf(account.getId()),
                        "customerId", String.valueOf(account.getCustomerId()),
                        "accountType", account.getAccountType()));
                eventPublisher.publish(eventType, payload);
            } catch (Exception ex) {
                log.warn("No se pudo serializar evento {}: {}", eventType, ex.getMessage());
            }
        });
    }

    @Override
    public Mono<AccountResponse> findById(UUID accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new NotFoundException("Account not found: " + accountId)))
                .map(mapper::toResponse);
    }

    @Override
    public Mono<AccountResponse> update(UUID accountId, AccountUpdateRequest request) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new NotFoundException("Account not found: " + accountId)))
                .map(account -> {
                    if (request.getProfile() != null) {
                        account.setProfile(request.getProfile().name());
                    }
                    if (request.getStatus() != null) {
                        account.setStatus(request.getStatus().name());
                    }
                    return account;
                })
                .flatMap(accountRepository::save)
                .flatMap(saved -> publishEvent("AccountUpdated", saved)
                        .thenReturn(mapper.toResponse(saved)));
    }

    @Override
    public Mono<Void> deactivate(UUID accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new NotFoundException("Account not found: " + accountId)))
                .flatMap(account -> {
                    if (account.getBalance() != null
                            && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                        return Mono.error(new ConflictException(
                                "Cannot deactivate an account with a non-zero balance"));
                    }
                    account.setStatus(AccountStatus.INACTIVE.name());
                    return accountRepository.save(account)
                            .flatMap(saved -> publishEvent("AccountDeactivated", saved))
                            .then();
                });
    }

    @Override
    public Mono<MovementPage> findMovements(UUID accountId, int page, int size) {
        return accountRepository.existsById(accountId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new NotFoundException("Account not found: " + accountId));
                    }
                    Pageable pageable = PageRequest.of(page, size);
                    Mono<List<MovementResponse>> content =
                            movementRepository.findByAccountIdOrderByTimestampDesc(accountId, pageable)
                                    .map(mapper::toResponse)
                                    .collectList();
                    Mono<Long> total = movementRepository.countByAccountId(accountId);
                    return Mono.zip(content, total)
                            .map(tuple -> {
                                MovementPage movementPage = new MovementPage();
                                movementPage.setContent(tuple.getT1());
                                movementPage.setPage(page);
                                movementPage.setSize(size);
                                movementPage.setTotalElements(tuple.getT2().intValue());
                                return movementPage;
                            });
                });
    }

    @Override
    public Mono<BalanceResponse> findBalance(UUID accountId) {
        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new NotFoundException("Account not found: " + accountId)))
                .map(account -> {
                    BalanceResponse response = new BalanceResponse();
                    response.setAccountId(account.getId());
                    response.setAvailableBalance(account.getBalance() == null
                            ? "0.00" : account.getBalance().toPlainString());
                    response.setCurrency(account.getCurrency());
                    response.setLastUpdated(account.getCreatedAt() == null
                            ? OffsetDateTimeMin()
                            : java.time.OffsetDateTime.ofInstant(account.getCreatedAt(), ZoneOffset.UTC));
                    return response;
                });
    }

    private java.time.OffsetDateTime OffsetDateTimeMin() {
        return java.time.OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
    }

    @Override
    public Mono<OperationAcceptedResponse> transfer(TransferRequest request, String transactionId) {
        return transferFacade.transfer(request, transactionId);
    }

    @Override
    public Mono<AccountPage> listAccounts(int page, int size) {
        Mono<List<AccountResponse>> content = accountRepository.findAll()
                .skip((long) page * size)
                .take(size)
                .map(mapper::toResponse)
                .collectList();
        return accountRepository.count().zipWith(content)
                .map(tuple -> {
                    AccountPage accountPage = new AccountPage();
                    accountPage.setContent(tuple.getT2());
                    accountPage.setPage(page);
                    accountPage.setSize(size);
                    accountPage.setTotalElements(tuple.getT1().intValue());
                    return accountPage;
                });
    }
}
