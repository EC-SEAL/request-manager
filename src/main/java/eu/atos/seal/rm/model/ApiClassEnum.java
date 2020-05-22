package eu.atos.seal.rm.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets apiClassEnum
 */
public enum ApiClassEnum 
{
  
  SM("SM"), // Session Manager
  
  CM("CM"), // Metadata - Configuration Manager
  
  CL("CL"), // API Gateway Client
  
  SPCL("SPCL"), // API Gateway Service Provider
  
  RM("RM"), // Request Manager
  
  SP("SP"), // SP Service
  
  AS("AS"), // Authentication Source
  
  IS("IS"), // Identity Source
  
  PER("PER"), // Persistence
  
  IDBOOT("IDBOOT"), // IDBootstrapping
  
  LINK("LINK"), // IDLinking
  
  LINKAPP ("LINKAPP"), // API Gateway Link
  
  REVOKED("REVOKED"); // Revocation
  
  private String value;

  ApiClassEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ApiClassEnum fromValue(String text) {
    for (ApiClassEnum b : ApiClassEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}