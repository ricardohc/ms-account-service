package com.banking.account.dto;

import java.net.URI;
import java.util.Objects;
import com.banking.account.dto.AccountProfile;
import com.banking.account.dto.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AccountCreateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-08T14:50:03.955803600-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class AccountCreateRequest {

  private UUID customerId;

  private AccountType accountType;

  private AccountProfile profile;

  private String initialAmount;

  private String currency;

  @Valid
  private List<UUID> holders = new ArrayList<>();

  @Valid
  private List<UUID> signers = new ArrayList<>();

  public AccountCreateRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public AccountCreateRequest(UUID customerId, AccountType accountType, AccountProfile profile, String currency) {
    this.customerId = customerId;
    this.accountType = accountType;
    this.profile = profile;
    this.currency = currency;
  }

  public AccountCreateRequest customerId(UUID customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
  */
  @NotNull @Valid 
  @Schema(name = "customerId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("customerId")
  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

  public AccountCreateRequest accountType(AccountType accountType) {
    this.accountType = accountType;
    return this;
  }

  /**
   * Get accountType
   * @return accountType
  */
  @NotNull @Valid 
  @Schema(name = "accountType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("accountType")
  public AccountType getAccountType() {
    return accountType;
  }

  public void setAccountType(AccountType accountType) {
    this.accountType = accountType;
  }

  public AccountCreateRequest profile(AccountProfile profile) {
    this.profile = profile;
    return this;
  }

  /**
   * Get profile
   * @return profile
  */
  @NotNull @Valid 
  @Schema(name = "profile", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("profile")
  public AccountProfile getProfile() {
    return profile;
  }

  public void setProfile(AccountProfile profile) {
    this.profile = profile;
  }

  public AccountCreateRequest initialAmount(String initialAmount) {
    this.initialAmount = initialAmount;
    return this;
  }

  /**
   * Get initialAmount
   * @return initialAmount
  */
  @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$") 
  @Schema(name = "initialAmount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("initialAmount")
  public String getInitialAmount() {
    return initialAmount;
  }

  public void setInitialAmount(String initialAmount) {
    this.initialAmount = initialAmount;
  }

  public AccountCreateRequest currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Get currency
   * @return currency
  */
  @NotNull @Pattern(regexp = "^[A-Z]{3}$") 
  @Schema(name = "currency", example = "PEN", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public AccountCreateRequest holders(List<UUID> holders) {
    this.holders = holders;
    return this;
  }

  public AccountCreateRequest addHoldersItem(UUID holdersItem) {
    if (this.holders == null) {
      this.holders = new ArrayList<>();
    }
    this.holders.add(holdersItem);
    return this;
  }

  /**
   * Get holders
   * @return holders
  */
  @Valid 
  @Schema(name = "holders", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("holders")
  public List<UUID> getHolders() {
    return holders;
  }

  public void setHolders(List<UUID> holders) {
    this.holders = holders;
  }

  public AccountCreateRequest signers(List<UUID> signers) {
    this.signers = signers;
    return this;
  }

  public AccountCreateRequest addSignersItem(UUID signersItem) {
    if (this.signers == null) {
      this.signers = new ArrayList<>();
    }
    this.signers.add(signersItem);
    return this;
  }

  /**
   * Get signers
   * @return signers
  */
  @Valid 
  @Schema(name = "signers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("signers")
  public List<UUID> getSigners() {
    return signers;
  }

  public void setSigners(List<UUID> signers) {
    this.signers = signers;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountCreateRequest accountCreateRequest = (AccountCreateRequest) o;
    return Objects.equals(this.customerId, accountCreateRequest.customerId) &&
        Objects.equals(this.accountType, accountCreateRequest.accountType) &&
        Objects.equals(this.profile, accountCreateRequest.profile) &&
        Objects.equals(this.initialAmount, accountCreateRequest.initialAmount) &&
        Objects.equals(this.currency, accountCreateRequest.currency) &&
        Objects.equals(this.holders, accountCreateRequest.holders) &&
        Objects.equals(this.signers, accountCreateRequest.signers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(customerId, accountType, profile, initialAmount, currency, holders, signers);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountCreateRequest {\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    accountType: ").append(toIndentedString(accountType)).append("\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    initialAmount: ").append(toIndentedString(initialAmount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    holders: ").append(toIndentedString(holders)).append("\n");
    sb.append("    signers: ").append(toIndentedString(signers)).append("\n");
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

