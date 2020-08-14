package eu.atos.seal.rm.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import eu.atos.seal.rm.model.SessionData;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
//import javax.validation.constraints.*;

/**
 * The response object for all APIs of the SM microservice.
 */
@ApiModel(description = "The response object for all APIs of the SM microservice.")
@Validated

public class SessionMngrResponseOLD   {
  /**
   * To be checked whether all agree.
   */
  public enum CodeEnum {
    NEW("NEW"),
    
    OK("OK"),
    
    ERROR("ERROR");

    private String value;

    CodeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CodeEnum fromValue(String text) {
      for (CodeEnum b : CodeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("code")
  private CodeEnum code = null;

  @JsonProperty("sessionData")
  private SessionData sessionData = null;

  @JsonProperty("additionalData")
  private String additionalData = null;

  @JsonProperty("error")
  private String error = null;

  public SessionMngrResponseOLD code(CodeEnum code) {
    this.code = code;
    return this;
  }

  /**
   * To be checked whether all agree.
   * @return code
  **/
  @ApiModelProperty(example = "OK", value = "To be checked whether all agree.")


  public CodeEnum getCode() {
    return code;
  }

  public void setCode(CodeEnum code) {
    this.code = code;
  }

  public SessionMngrResponseOLD sessionData(SessionData sessionData) {
    this.sessionData = sessionData;
    return this;
  }

  /**
   * All data repated to the session, inluing ID and session objects.
   * @return sessionData
  **/
  @ApiModelProperty(value = "All data repated to the session, inluing ID and session objects.")

  @Valid

  public SessionData getSessionData() {
    return sessionData;
  }

  public void setSessionData(SessionData sessionData) {
    this.sessionData = sessionData;
  }

  public SessionMngrResponseOLD additionalData(String additionalData) {
    this.additionalData = additionalData;
    return this;
  }

  /**
   * Where the token is returned on token requests
   * @return additionalData
  **/
  @ApiModelProperty(value = "Where the token is returned on token requests")


  public String getAdditionalData() {
    return additionalData;
  }

  public void setAdditionalData(String additionalData) {
    this.additionalData = additionalData;
  }

  public SessionMngrResponseOLD error(String error) {
    this.error = error;
    return this;
  }

  /**
   * The error message
   * @return error
  **/
  @ApiModelProperty(value = "The error message")


  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SessionMngrResponse sessionMngrResponse = (SessionMngrResponse) o;
    return Objects.equals(this.code, sessionMngrResponse.getCode()) &&
        Objects.equals(this.sessionData, sessionMngrResponse.getSessionData()) &&
        Objects.equals(this.additionalData, sessionMngrResponse.getAdditionalData()) &&
        Objects.equals(this.error, sessionMngrResponse.getError());
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, sessionData, additionalData, error);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SessionMngrResponse {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    sessionData: ").append(toIndentedString(sessionData)).append("\n");
    sb.append("    additionalData: ").append(toIndentedString(additionalData)).append("\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
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