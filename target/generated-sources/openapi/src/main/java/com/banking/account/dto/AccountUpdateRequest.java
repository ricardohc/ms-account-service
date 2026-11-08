package com.banking.account.dto;

import java.net.URI;
import java.util.Objects;
import com.banking.account.dto.AccountProfile;
import com.banking.account.dto.AccountStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * AccountUpdateRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-08T06:34:41.436025700-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public class AccountUpdateRequest {

  private AccountProfile profile;

  private AccountStatus status;

  public AccountUpdateRequest profile(AccountProfile profile) {
    this.profile = profile;
    return this;
  }

  /**
   * Get profile
   * @return profile
  */
  @Valid 
  @Schema(name = "profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profile")
  public AccountProfile getProfile() {
    return profile;
  }

  public void setProfile(AccountProfile profile) {
    this.profile = profile;
  }

  public AccountUpdateRequest status(AccountStatus status) {
    this.status = status;
    return this;
  }

  /**
   * Get status
   * @return status
  */
  @Valid 
  @Schema(name = "status", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("status")
  public AccountStatus getStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AccountUpdateRequest accountUpdateRequest = (AccountUpdateRequest) o;
    return Objects.equals(this.profile, accountUpdateRequest.profile) &&
        Objects.equals(this.status, accountUpdateRequest.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profile, status);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AccountUpdateRequest {\n");
    sb.append("    profile: ").append(toIndentedString(profile)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

