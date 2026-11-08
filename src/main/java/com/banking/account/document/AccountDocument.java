package com.banking.account.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class AccountDocument {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String accountNumber;

    private UUID customerId;

    private String accountType;

    private String profile;

    private String status;

    private BigDecimal balance;

    private String currency;

    private List<UUID> holders = new ArrayList<>();

    private List<UUID> signers = new ArrayList<>();

    /** Movimientos de depósito/retiro usados en el mes en curso (cupo gratuito). */
    @Builder.Default
    private int monthlyMovements = 0;

    /** Mes de conteo, formato YYYY-MM en UTC. */
    @Builder.Default
    private String monthlyMovementsMonth = currentMonth();

    /** Día del mes (UTC) del último movimiento para la regla FIXED_TERM. */
    @Builder.Default
    private Integer lastMovementDayOfMonth = null;

    @CreatedDate
    private Instant createdAt;

    @Version
    private Long version;

    public static String currentMonth() {
        return java.time.YearMonth.now(java.time.ZoneOffset.UTC).toString();
    }
}
