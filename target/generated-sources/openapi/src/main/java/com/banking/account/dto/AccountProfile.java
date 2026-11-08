package com.banking.account.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets AccountProfile
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-11-08T14:50:03.955803600-05:00[America/Lima]", comments = "Generator version: 7.6.0")
public enum AccountProfile {
  
  STANDARD("STANDARD"),
  
  VIP("VIP"),
  
  PYME("PYME");

  private String value;

  AccountProfile(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static AccountProfile fromValue(String value) {
    for (AccountProfile b : AccountProfile.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

