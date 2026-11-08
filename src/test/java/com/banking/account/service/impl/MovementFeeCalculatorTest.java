package com.banking.account.service.impl;

import com.banking.account.config.AccountProperties;
import com.banking.account.document.AccountDocument;
import com.banking.account.exception.UnprocessableEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovementFeeCalculatorTest {

    private MovementFeeCalculator calculator;
    private AccountProperties properties;

    @BeforeEach
    void setUp() {
        properties = new AccountProperties();
        properties.getSavings().setMaxMonthlyMovements(5);
        properties.getSavings().setExtraMovementFee(new BigDecimal("2.00"));
        properties.getTransactions().setMaxFreeDeposits(5);
        properties.getTransactions().setMaxFreeWithdrawals(5);
        properties.getTransactions().setPerTransactionFee(new BigDecimal("1.00"));
        calculator = new MovementFeeCalculator(properties);
    }

    private AccountDocument account(String type, int monthlyMovements) {
        AccountDocument account = AccountDocument.builder().build();
        account.setAccountType(type);
        account.setMonthlyMovements(monthlyMovements);
        return account;
    }

    @Test
    void savingsWithinFreeQuotaNoFee() {
        assertTrue(calculator.feeFor(account("SAVINGS", 0), false)
                .compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    void savingsExceedingMonthlyQuotaChargesExtraFee() {
        BigDecimal fee = calculator.feeFor(account("SAVINGS", 5), false);
        // 2.00 por superar el límite mensual + 1.00 por superar cupo gratuito
        assertEquals(0, fee.compareTo(new BigDecimal("3.00")));
    }

    @Test
    void checkingHasNoMonthlyLimitByDefault() {
        assertTrue(calculator.feeFor(account("CHECKING", 1000), false)
                .compareTo(new BigDecimal("1.00")) == 0);
    }

    @Test
    void fixedTermSecondMovementSameDayFails() {
        AccountDocument fixedTerm = account("FIXED_TERM", 0);
        fixedTerm.setLastMovementDayOfMonth(java.time.LocalDate.now(
                java.time.ZoneOffset.UTC).getDayOfMonth());
        assertThrows(UnprocessableEntityException.class,
                () -> calculator.validateFixedTerm(fixedTerm));
    }

    @Test
    void fixedTermMovementOnDifferentDayAllowed() {
        AccountDocument fixedTerm = account("FIXED_TERM", 0);
        fixedTerm.setLastMovementDayOfMonth(null);
        calculator.validateFixedTerm(fixedTerm);
        fixedTerm.setLastMovementDayOfMonth(1);
        if (java.time.LocalDate.now(java.time.ZoneOffset.UTC).getDayOfMonth() != 1) {
            calculator.validateFixedTerm(fixedTerm);
        }
    }
}
