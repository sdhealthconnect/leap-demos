package gov.hhs.onc.leap.ces.orchestration.model;

import com.google.common.base.Splitter;

import java.util.Iterator;
import java.util.UUID;


public class V2Message {
    private final String V2_PIPE = "|";
    private final String V2_HAT = "^";

    private String sendingOrganizationSegment;
    private String receivingOrganizationSegment;
    private String messageDateTime;
    private String messageId;
    private String messageType;
    private String receivingOrganizationName;
    private String receivingOrganizationOID;
    private String receivingOrganizationSystem;
    private String sendingOrganizationName;
    private String sendingOrganizationOID;
    private String sendingOrganizationSystem;
    private String patientId;
    private String patientIdCodeSystemAndName;
    private String patientIdCodeSystem;
    private UUID uuid;

    public V2Message(String message) {
        processMSHSegment(message);
        processPatient(message);
    }

    void processMSHSegment(String msg) {
        Iterable<String> mshLines = Splitter.on(System.getProperty("line.separator")).split(msg);
        Iterator mshIter = mshLines.iterator();
        //set MSH Info
        String mshLine = (String)mshIter.next();

        Iterable<String> segments = Splitter.on(V2_PIPE).split(mshLine);
        Iterator iter = segments.iterator();
        
        int pos = 1;
        while (iter.hasNext()) {
            String segment = (String)iter.next();
            switch (pos) {
                case 4:
                    //sending facility
                    sendingOrganizationSegment = segment;
                    setSendingOrganization(sendingOrganizationSegment);
                    break;
                case 6:
                    receivingOrganizationSegment = segment;
                    setReceivingOrganization(receivingOrganizationSegment);
                case 7:
                    messageDateTime = segment;
                    break;
                case 10:
                    //message control id
                    messageId = segment;
                    break;
                case 12:
                    messageType = segment;
                    break;
            //nothing
                default:
                    break;
            }
            pos++;
        }
    }
    
    private void setReceivingOrganization(String line) {
        Iterable<String> segments = Splitter.on(V2_HAT).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String s = (String)iter.next();
            if (pos == 1) {
                receivingOrganizationName = s;
            }
            else if (pos == 2) {
                receivingOrganizationOID = s;
            }
            else if (pos == 3) {
                receivingOrganizationSystem = "urn:ietf:rfc:3986";
            }
            else {
                //nothing here
            }
            pos++;
        }
    }
    
    private void setSendingOrganization(String line) {
        Iterable<String> segments = Splitter.on(V2_HAT).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String s = (String)iter.next();
            if (pos == 1) {
                sendingOrganizationName = s;
            }
            else if (pos == 2) {
                sendingOrganizationOID = s;
            }
            else if (pos == 3) {
                sendingOrganizationSystem = "urn:ietf:rfc:3986";
            }
            else {
                //nothing here
            }
            pos++;
        }
    }
    
    void processPatient(String msg) {
        Iterable<String> msgLines = Splitter.on(System.getProperty("line.separator")).split(msg);
        Iterator iter = msgLines.iterator();
        //set MSH Info
        while (iter.hasNext()) {
            String line = (String)iter.next();
            if (line.indexOf("PID|") > -1) {
                processPatientSegment(line);
                break;
            }
        }
    }
    
    private void processPatientSegment(String line) {
        Iterable<String> segments = Splitter.on(V2_PIPE).split(line);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String s = (String)iter.next();
            if (pos == 4) {
                setPatientIdentifiers(s);
                break;
            }
            pos++;
        }
    }
    
    private void setPatientIdentifiers(String s) {
        Iterable<String> segments = Splitter.on(V2_HAT).split(s);
        Iterator iter = segments.iterator();
        int pos = 1;
        while (iter.hasNext()) {
            String field = (String)iter.next();
            if (pos == 1) {
                patientId = field; 
            }
            else if (pos == 5) {
                patientIdCodeSystemAndName = field;
            }
            pos++;
        }
        //create mapping for this but for time being
        patientIdCodeSystem = "http://hl7.org/fhir/sid/us-ssn";
    }

    public String getMessageDateTime() {
        return this.messageDateTime;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public String getReceivingOrganizationName() {
        return this.receivingOrganizationName;
    }

    public String getReceivingOrganizationOID() {
        return this.receivingOrganizationOID;
    }

    public String getReceivingOrganizationSystem() {
        return this.receivingOrganizationSystem;
    }

    public String getSendingOrganizationName() {
        return this.sendingOrganizationName;
    }

    public String getSendingOrganizationOID() {
        return this.sendingOrganizationOID;
    }

    public String getSendingOrganizationSystem() {
        return this.sendingOrganizationSystem;
    }

    public String getPatientId() {
        return this.patientId;
    }

    public String getPatientIdCodeSystemAndName() {
        return this.patientIdCodeSystemAndName;
    }

    public String getPatientIdCodeSystem() {
        return this.patientIdCodeSystem;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
