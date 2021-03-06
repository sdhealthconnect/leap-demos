/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.onc.leap.ces.common.clients.model.xacml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** @author duanedecouteau */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"AttributeId", "Value"})
public class StringAttribute {

  @JsonProperty("AttributeId")
  private String attributeId;

  @JsonProperty("Value")
  private String value;

  /** @return the attributeId */
  @JsonProperty("AttributeId")
  public String getAttributeId() {
    return attributeId;
  }

  /** @param attributeId the attributeId to set */
  @JsonProperty("AttributeId")
  public StringAttribute setAttributeId(String attributeId) {
    this.attributeId = attributeId;
    return this;
  }

  /** @return the value */
  @JsonProperty("Value")
  public String getValue() {
    return value;
  }

  /** @param value the value to set */
  @JsonProperty("Value")
  public StringAttribute setValue(String value) {
    this.value = value;
    return this;
  }
}
