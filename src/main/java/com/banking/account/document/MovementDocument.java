package com.banking.account.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movements")
public class MovementDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID accountId;

    private String type;

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private Instant timestamp;
}
