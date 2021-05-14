package gov.hhs.onc.leap.fhir.connectathon.consent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.card.ConsentConsultCardClient;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookRequest;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookResponse;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlRequest;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlResponse;
import gov.hhs.onc.leap.ces.common.clients.xacml.ConsentConsultXacmlClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FHIRConnectathonService {
    private static final Logger log = LoggerFactory.getLogger(FHIRConnectathonService.class);
    @Value("${cds.host.url}")
    private String CDS_HOST;  // = "https://sdhc-leap.appspot.com";

    private ConsentConsultCardClient cardClient;
    private ConsentConsultXacmlClient xacmlClient;
    private String msg;

    public String authRequest(String msg) {

        this.msg = msg;
        String res = "Authorization request processing failed";
        //determine which client
        if (msg.contains("hook")) {
            res = processCDSHooksRequest();
        }
        else if (msg.contains("AccessSubject")) {
            res = processXACMLRequest();
        }
        else {
            res = res + ": Unable to determine request type";
        }

        return res;
    }

    private String processCDSHooksRequest() {
        String res = "CDSHooks Request Failed";
        try {
            ObjectMapper mapper = new ObjectMapper();
            PatientConsentConsultHookRequest cardRequest = mapper.readValue(msg, PatientConsentConsultHookRequest.class);
            cardClient = new ConsentConsultCardClient(CDS_HOST);
            PatientConsentConsultHookResponse cardResponse = cardClient.getConsentDecision(cardRequest);
            res = mapper.writeValueAsString(cardResponse);
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            res = res + ex.getMessage();
        }
        System.out.println("PROCESS CDS HOOKS RESPONSE "+ res);
        return res;
    }

    private String processXACMLRequest() {
        String res = "XACML Request Failed";
        try {
            ObjectMapper mapper = new ObjectMapper();
            XacmlRequest request = mapper.readValue(msg, XacmlRequest.class);
            xacmlClient = new ConsentConsultXacmlClient(CDS_HOST);
            XacmlResponse response = xacmlClient.getConsentDecision(request);
            res = mapper.writeValueAsString(response);
        }
        catch (Exception ex) {
            log.error(ex.getMessage());
            res = res + ex.getMessage();
        }
        System.out.println("PROCESS XACML RESPONSE "+res);
        return res;
    }



}
