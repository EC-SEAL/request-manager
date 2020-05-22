package eu.atos.seal.rm.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

/**
 * UpdateDataRequest
 */

@Validated
public class UpdateDataRequest
{
  @JsonProperty("sessionId")
  private String sessionId = null;

  @JsonProperty("variableName")
  private String variableName = null;
  
  @JsonProperty("dataObject")
  private String dataObject = null;

  public UpdateDataRequest dataObject(String dataObject) {
    this.dataObject = dataObject;
    return this;
  }

  /**
   * Get dataObject
   * @return dataObject
  **/
  @ApiModelProperty(value = "")


  public String getDataObject() {
    return dataObject;
  }

  public void setDataObject(String dataObject) {
    this.dataObject = dataObject;
  }

  public UpdateDataRequest sessionId(String sessionId) {
    this.sessionId = sessionId;
    return this;
  }

  /**
   * Get sessionId
   * @return sessionId
  **/
  @ApiModelProperty(value = "")


  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public UpdateDataRequest variableName(String variableName) {
    this.variableName = variableName;
    return this;
  }

  /**
   * Get variableName
   * @return variableName
  **/
  @ApiModelProperty(value = "")


  public String getVariableName() {
    return variableName;
  }

  public void setVariableName(String variableName) {
    this.variableName = variableName;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UpdateDataRequest updateDataRequest = (UpdateDataRequest) o;
    return Objects.equals(this.dataObject, updateDataRequest.dataObject) &&
        Objects.equals(this.sessionId, updateDataRequest.sessionId) &&
        Objects.equals(this.variableName, updateDataRequest.variableName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataObject, sessionId, variableName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UpdateDataRequest {\n");
    
    sb.append("    dataObject: ").append(toIndentedString(dataObject)).append("\n");
    sb.append("    sessionId: ").append(toIndentedString(sessionId)).append("\n");
    sb.append("    variableName: ").append(toIndentedString(variableName)).append("\n");
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