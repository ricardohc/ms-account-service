package com.banking.account.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableEntityException extends DomainException {
    public UnprocessableEntityException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "UNPROCESSABLE_ENTITY", message);
    }
}
