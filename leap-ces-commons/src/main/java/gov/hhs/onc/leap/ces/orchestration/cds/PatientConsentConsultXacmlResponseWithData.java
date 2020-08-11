package gov.hhs.onc.leap.ces.orchestration.cds;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"xacmlResponse", "payload"})
public class PatientConsentConsultXacmlResponseWithData {

    @JsonProperty("xacmlResponse")
    private XacmlResponse xacmlResponse;

    @JsonProperty("payload")
    private String payload;

    @JsonProperty("xacmlResponse")
    public XacmlResponse getXacmlResponse() {
        return xacmlResponse;
    }

    @JsonProperty("xacmlResponse")
    public void setXacmlResponse(XacmlResponse xacmlResponse) {
        this.xacmlResponse = xacmlResponse;
    }

    @JsonProperty("payload")
    public String getPayload() {
        return payload;
    }

    @JsonProperty("payload")
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
