package gov.hhs.onc.leap.ces.orchestration.cds;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"obligation", "document"})
public class DocumentWithObligations {

    @JsonProperty("obligation")
    private String obligation;

    @JsonProperty("document")
    private String document;

    @JsonProperty("obligation")
    public String getObligation() {
        return obligation;
    }

    @JsonProperty("obligation")
    public void setObligation(String obligation) {
        this.obligation = obligation;
    }

    @JsonProperty("document")
    public String getDocument() {
        return document;
    }

    @JsonProperty("document")
    public void setDocument(String document) {
        this.document = document;
    }
}
