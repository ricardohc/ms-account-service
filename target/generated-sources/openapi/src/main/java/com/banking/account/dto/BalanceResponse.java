package com.banking.account.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * BalanceResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-08T14:50:03.955803600-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class BalanceResponse {

  private UUID accountId;

  private String availableBalance;

  private String currency;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime lastUpdated;

  public BalanceResponse accountId(UUID accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Get accountId
   * @return accountId
  */
  @Valid 
  @Schema(name = "accountId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountId")
  public UUID getAccountId() {
    return accountId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }

  public BalanceResponse availableBalance(String availableBalance) {
    this.availableBalance = availableBalance;
    return this;
  }

  /**
   * Get availableBalance
   * @return availableBalance
  */
  
  @Schema(name = "availableBalance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("availableBalance")
  public String getAvailableBalance() {
    return availableBalance;
  }

  public void setAvailableBalance(String availableBalance) {
    this.availableBalance = availableBalance;
  }

  public BalanceResponse currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
  */
  
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BalanceResponse lastUpdated(OffsetDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  /**
   * Get lastUpdated
   * @return lastUpdated
  */
  @Valid 
  @Schema(name = "lastUpdated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastUpdated")
  public OffsetDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(OffsetDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BalanceResponse balanceResponse = (BalanceResponse) o;
    return Objects.equals(this.accountId, balanceResponse.accountId) &&
        Objects.equals(this.availableBalance, balanceResponse.availableBalance) &&
        Objects.equals(this.currency, balanceResponse.currency) &&
        Objects.equals(this.lastUpdated, balanceResponse.lastUpdated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, availableBalance, currency, lastUpdated);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BalanceResponse {\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    availableBalance: ").append(toIndentedString(availableBalance)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    lastUpdated: ").append(toIndentedString(lastUpdated)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

