package com.banking.account.exception;

import org.springframework.http.HttpStatus;

public class DependencyUnavailableException extends DomainException {
    public DependencyUnavailableException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, "DEPENDENCY_UNAVAILABLE", message);
    }
}