package gov.hhs.onc.leap.ces.orchestration.cds;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"hookResponse", "payload"})
public class PatientConsentConsultHookResponseWithData {

    @JsonProperty("hookResponse")
    private PatientConsentConsultHookResponse hookResponse;
    @JsonProperty("payload")
    private String payload;

    @JsonProperty("hookResponse")
    public PatientConsentConsultHookResponse getHookResponse() {
        return hookResponse;
    }

    @JsonProperty("hookResponse")
    public void setHookResponse(PatientConsentConsultHookResponse hookResponse) {
        this.hookResponse = hookResponse;
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
