package com.banking.account.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * TransferRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-08T15:34:58.402910600-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class TransferRequest {

  private UUID sourceAccountId;

  private UUID targetAccountId;

  private String amount;

  private String currency;

  private String description;

  public TransferRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TransferRequest(UUID sourceAccountId, UUID targetAccountId, String amount, String currency) {
    this.sourceAccountId = sourceAccountId;
    this.targetAccountId = targetAccountId;
    this.amount = amount;
    this.currency = currency;
  }

  public TransferRequest sourceAccountId(UUID sourceAccountId) {
    this.sourceAccountId = sourceAccountId;
    return this;
  }

  /**
   * Get sourceAccountId
   * @return sourceAccountId
  */
  @NotNull @Valid 
  @Schema(name = "sourceAccountId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("sourceAccountId")
  public UUID getSourceAccountId() {
    return sourceAccountId;
  }

  public void setSourceAccountId(UUID sourceAccountId) {
    this.sourceAccountId = sourceAccountId;
  }

  public TransferRequest targetAccountId(UUID targetAccountId) {
    this.targetAccountId = targetAccountId;
    return this;
  }

  /**
   * Get targetAccountId
   * @return targetAccountId
  */
  @NotNull @Valid 
  @Schema(name = "targetAccountId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("targetAccountId")
  public UUID getTargetAccountId() {
    return targetAccountId;
  }

  public void setTargetAccountId(UUID targetAccountId) {
    this.targetAccountId = targetAccountId;
  }

  public TransferRequest amount(String amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Get amount
   * @return amount
  */
  @NotNull @Pattern(regexp = "^(?!0+(\\.0+)?$)[0-9]+(\\.[0-9]{1,2})?$") 
  @Schema(name = "amount", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public TransferRequest currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
  */
  @NotNull @Pattern(regexp = "^[A-Z]{3}$") 
  @Schema(name = "currency", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public TransferRequest description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransferRequest transferRequest = (TransferRequest) o;
    return Objects.equals(this.sourceAccountId, transferRequest.sourceAccountId) &&
        Objects.equals(this.targetAccountId, transferRequest.targetAccountId) &&
        Objects.equals(this.amount, transferRequest.amount) &&
        Objects.equals(this.currency, transferRequest.currency) &&
        Objects.equals(this.description, transferRequest.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceAccountId, targetAccountId, amount, currency, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransferRequest {\n");
    sb.append("    sourceAccountId: ").append(toIndentedString(sourceAccountId)).append("\n");
    sb.append("    targetAccountId: ").append(toIndentedString(targetAccountId)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

