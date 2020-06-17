package gov.hhs.onc.leap.ces.common.clients.model.sls;

import com.fasterxml.jackson.annotation.*;

/**
 * author: ddecouteau
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id","msgType","origin","status","result","processingTime","notes", "isNew"})
public class LabelingResult {

    @JsonProperty("id")
    private String id;

    @JsonProperty("msgType")
    private String msgType;

    @JsonProperty("origin")
    private String origin;

    @JsonProperty("status")
    private String status;

    @JsonProperty("result")
    private String result;

    @JsonProperty("processingTime")
    private String processingTime;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("isNew")
    private boolean isNew = true;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("msgType")
    public String getMsgType() {
        return msgType;
    }

    @JsonProperty("msgType")
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @JsonProperty("origin")
    public String getOrigin() {
        return origin;
    }

    @JsonProperty("origin")
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("processingTime")
    public String getProcessingTime() {
        return processingTime;
    }

    @JsonProperty("processingTime")
    public void setProcessingTime(String processingTime) {
        this.processingTime = processingTime;
    }

    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @JsonProperty("isNew")
    public boolean isNew() {
        return isNew;
    }

    @JsonProperty("isNew")
    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
