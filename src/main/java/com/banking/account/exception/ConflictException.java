package com.banking.account.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends DomainException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, "CONFLICT", message);
    }
}
