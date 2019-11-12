package eu.atos.seal.rm.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Endpoint descriptor, where requests can be made or responses sent.
 */
@ApiModel(description = "Endpoint descriptor, where requests can be made or responses sent.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-11-07T15:11:31.760Z")

public class EndpointType   {
  @JsonProperty("type")
  private String type = null;

  @JsonProperty("method")
  private String method = null;

  @JsonProperty("url")
  private String url = null;

  public EndpointType type(String type) {
    this.type = type;
    return this;
  }

  /**
   * String identifying the kind of endpoint (depends on each protocol)
   * @return type
  **/
  @ApiModelProperty(example = "SSOService", value = "String identifying the kind of endpoint (depends on each protocol)")


  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public EndpointType method(String method) {
    this.method = method;
    return this;
  }

  /**
   * String identifying the method to access the endpoint (depends on each protocol, i.e. HTTP-POST).
   * @return method
  **/
  @ApiModelProperty(example = "HTTP-POST", value = "String identifying the method to access the endpoint (depends on each protocol, i.e. HTTP-POST).")


  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public EndpointType url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Access url of the endpoint
   * @return url
  **/
  @ApiModelProperty(example = "https://esmo.uji.es/gw/saml/idp/SSOService.php", value = "Access url of the endpoint")


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EndpointType endpointType = (EndpointType) o;
    return Objects.equals(this.type, endpointType.type) &&
        Objects.equals(this.method, endpointType.method) &&
        Objects.equals(this.url, endpointType.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, method, url);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EndpointType {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
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

