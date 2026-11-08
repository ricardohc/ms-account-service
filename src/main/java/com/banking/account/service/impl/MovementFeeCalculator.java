package com.banking.account.service.impl;

import com.banking.account.config.AccountProperties;
import com.banking.account.document.AccountDocument;
import com.banking.account.exception.UnprocessableEntityException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Cálculo de comisiones por movimiento y reglas de cuenta FIXED_TERM.
 */
@Component
public class MovementFeeCalculator {

    private final AccountProperties properties;

    public MovementFeeCalculator(AccountProperties properties) {
        this.properties = properties;
    }

    /**
     * Regla FIXED_TERM: solo un movimiento (depósito o retiro) en el día del mes.
     * Segundo movimiento el mismo día → 422.
     */
    public void validateFixedTerm(AccountDocument account) {
        if (!"FIXED_TERM".equals(account.getAccountType())) {
            return;
        }
        int today = LocalDate.now(ZoneOffset.UTC).getDayOfMonth();
        if (account.getLastMovementDayOfMonth() != null
                && account.getLastMovementDayOfMonth() == today) {
            throw new UnprocessableEntityException(
                    "FIXED_TERM account allows only one movement per month on its day");
        }
    }

    /**
     * Comisión por movimiento excedente:
     * - Si la cuenta supera el cupo mensual de movimientos de su tipo → comisión extra.
     * - Si supera el número de depósitos/retiros gratuitos → comisión por transacción.
     */
    public BigDecimal feeFor(AccountDocument account, boolean isWithdrawal) {
        BigDecimal fee = BigDecimal.ZERO;

        AccountProperties.Savings savings = properties.getSavings();
        AccountProperties.Checking checking = properties.getChecking();

        if ("SAVINGS".equals(account.getAccountType())
                && savings.getMaxMonthlyMovements() != null
                && account.getMonthlyMovements() >= savings.getMaxMonthlyMovements()) {
            fee = fee.add(savings.getExtraMovementFee());
        } else if ("CHECKING".equals(account.getAccountType())
                && checking.getMaxMonthlyMovements() != null
                && account.getMonthlyMovements() >= checking.getMaxMonthlyMovements()) {
            fee = fee.add(checking.getExtraMovementFee());
        }

        AccountProperties.Transactions tx = properties.getTransactions();
        int freeLimit = isWithdrawal
                ? tx.getMaxFreeWithdrawals() : tx.getMaxFreeDeposits();
        if (account.getMonthlyMovements() >= freeLimit) {
            fee = fee.add(tx.getPerTransactionFee());
        }
        return fee;
    }
}
