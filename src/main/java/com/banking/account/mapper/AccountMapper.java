package com.banking.account.mapper;

import com.banking.account.document.AccountDocument;
import com.banking.account.dto.AccountResponse;
import com.banking.account.document.MovementDocument;
import com.banking.account.dto.MovementResponse;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class AccountMapper {

    public AccountResponse toResponse(AccountDocument doc) {
        AccountResponse response = new AccountResponse();
        response.setId(doc.getId());
        response.setAccountNumber(doc.getAccountNumber());
        response.setCustomerId(doc.getCustomerId());
        response.setAccountType(com.banking.account.dto.AccountType.valueOf(doc.getAccountType()));
        response.setProfile(com.banking.account.dto.AccountProfile.valueOf(doc.getProfile()));
        response.setStatus(com.banking.account.dto.AccountStatus.valueOf(doc.getStatus()));
        response.setBalance(doc.getBalance() == null ? "0.00" : doc.getBalance().toPlainString());
        response.setCurrency(doc.getCurrency());
        if (doc.getCreatedAt() != null) {
            response.setCreatedAt(OffsetDateTime.ofInstant(doc.getCreatedAt(), ZoneOffset.UTC));
        }
        return response;
    }

    public MovementResponse toResponse(MovementDocument doc) {
        MovementResponse response = new MovementResponse();
        response.setId(doc.getId());
        response.setAccountId(doc.getAccountId());
        response.setType(MovementResponse.TypeEnum.valueOf(doc.getType()));
        response.setAmount(doc.getAmount() == null ? "0.00" : doc.getAmount().toPlainString());
        response.setBalanceAfter(doc.getBalanceAfter() == null
                ? "0.00" : doc.getBalanceAfter().toPlainString());
        if (doc.getTimestamp() != null) {
            response.setTimestamp(OffsetDateTime.ofInstant(doc.getTimestamp(), ZoneOffset.UTC));
        }
        return response;
    }
}
