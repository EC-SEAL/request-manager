package eu.atos.seal.rm.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.atos.seal.rm.model.ApiClassEnum;
import eu.atos.seal.rm.model.ApiConnectionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;


/**
 * Endpoint descriptor, where requests can be made
 */
@ApiModel(description = "Endpoint descriptor, where requests can be made")
@Validated

public class PublishedApiType   {
  @JsonProperty("apiClass")
  private ApiClassEnum apiClass = null;

  @JsonProperty("apiCall")
  private String apiCall = null;

  @JsonProperty("apiConnectionType")
  private ApiConnectionType apiConnectionType = null;

  @JsonProperty("apiEndpoint")
  private String apiEndpoint = null;

  public PublishedApiType apiClass(ApiClassEnum apiClass) {
    this.apiClass = apiClass;
    return this;
  }

  /**
   * Get apiClass
   * @return apiClass
  **/
  @ApiModelProperty(value = "")

  @Valid

  public ApiClassEnum getApiClass() {
    return apiClass;
  }

  public void setApiClass(ApiClassEnum apiClass) {
    this.apiClass = apiClass;
  }

  public PublishedApiType apiCall(String apiCall) {
    this.apiCall = apiCall;
    return this;
  }

  /**
   * Get apiCall
   * @return apiCall
  **/
  @ApiModelProperty(value = "")

  @Valid

  public String getApiCall() {
    return apiCall;
  }

  public void setApiCall(String apiCall) {
    this.apiCall = apiCall;
  }

  public PublishedApiType apiConnectionType(ApiConnectionType apiConnectionType) {
    this.apiConnectionType = apiConnectionType;
    return this;
  }

  /**
   * Get apiConnectionType
   * @return apiConnectionType
  **/
  @ApiModelProperty(value = "")

  @Valid

  public ApiConnectionType getApiConnectionType() {
    return apiConnectionType;
  }

  public void setApiConnectionType(ApiConnectionType apiConnectionType) {
    this.apiConnectionType = apiConnectionType;
  }

  public PublishedApiType apiEndpoint(String apiEndpoint) {
    this.apiEndpoint = apiEndpoint;
    return this;
  }

  /**
   * Get apiEndpoint
   * @return apiEndpoint
  **/
  @ApiModelProperty(value = "")


  public String getApiEndpoint() {
    return apiEndpoint;
  }

  public void setApiEndpoint(String apiEndpoint) {
    this.apiEndpoint = apiEndpoint;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PublishedApiType publishedApiType = (PublishedApiType) o;
    return Objects.equals(this.apiClass, publishedApiType.apiClass) &&
        Objects.equals(this.apiCall, publishedApiType.apiCall) &&
        Objects.equals(this.apiConnectionType, publishedApiType.apiConnectionType) &&
        Objects.equals(this.apiEndpoint, publishedApiType.apiEndpoint);
  }

  @Override
  public int hashCode() {
    return Objects.hash(apiClass, apiCall, apiConnectionType, apiEndpoint);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PublishedApiType {\n");
    
    sb.append("    apiClass: ").append(toIndentedString(apiClass)).append("\n");
    sb.append("    apiCall: ").append(toIndentedString(apiCall)).append("\n");
    sb.append("    apiConnectionType: ").append(toIndentedString(apiConnectionType)).append("\n");
    sb.append("    apiEndpoint: ").append(toIndentedString(apiEndpoint)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}