package com.banking.account.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Comisiones y límites configurables. Ningún valor se hardcodea.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "banking.account")
public class AccountProperties {

    private MinimumOpening minimumOpeningAmount = new MinimumOpening();
    private Savings savings = new Savings();
    private Checking checking = new Checking();
    private Transactions transactions = new Transactions();

    @Getter
    @Setter
    public static class MinimumOpening {
        private BigDecimal standard = BigDecimal.ZERO;
        private BigDecimal vip = BigDecimal.ZERO;
        private BigDecimal pyme = BigDecimal.ZERO;
    }

    @Getter
    @Setter
    public static class Savings {
        private boolean maintenanceFeeEnabled = false;
        private BigDecimal maintenanceFeeAmount = BigDecimal.ZERO;
        private Integer maxMonthlyMovements = null;
        private BigDecimal extraMovementFee = BigDecimal.ZERO;
    }

    @Getter
    @Setter
    public static class Checking {
        private boolean maintenanceFeeEnabled = true;
        private BigDecimal maintenanceFeeAmount = BigDecimal.ZERO;
        private Integer maxMonthlyMovements = null;
        private BigDecimal extraMovementFee = BigDecimal.ZERO;
    }

    @Getter
    @Setter
    public static class Transactions {
        private int maxFreeDeposits = 0;
        private int maxFreeWithdrawals = 0;
        private BigDecimal perTransactionFee = BigDecimal.ZERO;
    }
}
