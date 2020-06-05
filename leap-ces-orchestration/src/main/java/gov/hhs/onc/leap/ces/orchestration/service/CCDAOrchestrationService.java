package gov.hhs.onc.leap.ces.orchestration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.card.ConsentConsultCardClient;
import gov.hhs.onc.leap.ces.common.clients.model.card.*;
import gov.hhs.onc.leap.ces.common.clients.model.ces.PatientConsentConsultHookRequestWithData;
import gov.hhs.onc.leap.ces.common.clients.model.ces.PatientConsentConsultXacmlRequestWithData;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.Obligations;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.Response;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlRequest;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlResponse;
import gov.hhs.onc.leap.ces.common.clients.xacml.ConsentConsultXacmlClient;
import gov.hhs.onc.leap.ces.sls.client.SLSRequestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author duanedecouteau
 */
@Component
@Slf4j
public class CCDAOrchestrationService {

    private ConsentConsultCardClient hookClient;
    private ConsentConsultXacmlClient xacmlClient;
    private SLSRequestClient slsClient;

    private PatientConsentConsultHookRequestWithData hookRequestWithData;
    private PatientConsentConsultHookRequest hookRequest;
    private PatientConsentConsultXacmlRequestWithData xacmlRequestWithData;
    private XacmlRequest xacmlRequest;

    private String decision;
    private String action;
    private String label;

    
    @Value("${cds.host.url}")    
    private String CDS_HOST;  // = "https://sdhc-leap.appspot.com";
    
    @Value("${sls.host.url}")
    private String SLS_HOST; // = "http://localhost:9091";

    public String processDocumentCDSHooks(PatientConsentConsultHookRequestWithData hookRequestWithData) {
        String ccda = hookRequestWithData.getPayload();
        try {
            this.hookRequestWithData = hookRequestWithData;
            this.hookRequest = hookRequestWithData.getHookRequest();
            PatientConsentConsultHookRequest request = hookRequestWithData.getHookRequest();
            ConsentConsultCardClient cardClient = new ConsentConsultCardClient(CDS_HOST);
            PatientConsentConsultHookResponse response = cardClient.getConsentDecision(request);

            Card card = response.getCards().get(0);
            Extension extension = card.getExtension();
            decision = extension.getDecision();
            action = extension.getObligations().get(0).getObligationId().getCode();
            label = extension.getObligations().get(0).getParameters().getCodes().get(0).getCode();
            log.info("Decision: "+decision+" Action: "+action+" Label: "+label);
            if ("CONSENT_PERMIT".equals(decision)) {
                List<gov.hhs.onc.leap.ces.common.clients.model.card.Obligations> lObligations = response.getCards().get(0).getExtension().getObligations();
                //call sls
                SLSRequestClient slsRequestClient = new SLSRequestClient(SLS_HOST);
                UUID uuid = UUID.randomUUID();
                String id = uuid.toString();
                String lablelResultString = slsRequestClient.requestLabelingSecured(id, "Connect CARD", "CCDA", "v3", ccda);
                log.info("Entering Privacy Protective Services");
                log.info("Audit Consent Decision");
            }
            else {
                throw new IOException();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return ccda;
    }

    public String processDocumentXacml(PatientConsentConsultXacmlRequestWithData xacmlRequestWithData) {
        String ccda = xacmlRequestWithData.getPayload();
        try {
            this.xacmlRequestWithData = xacmlRequestWithData;
            this.xacmlRequest = xacmlRequestWithData.getXacmlRequest();

            XacmlRequest xacmlRequest = xacmlRequestWithData.getXacmlRequest();
            xacmlClient = new ConsentConsultXacmlClient(CDS_HOST);
            XacmlResponse xacmlResponse = xacmlClient.getConsentDecision(xacmlRequest);
            Response response = xacmlResponse.getResponse().get(0);
            decision = response.getDecision();
            action = response.getObligations().get(0).getObligationId().getCode();
            label = response.getObligations().get(0).getAttributeAssignments().get(0).getSystemCodes().get(0).getCode();
            log.info("Decision: "+decision+" Action: "+action+" Label: "+label);
            if (response.getDecision().equals("Permit")) {
                Obligations obligation = response.getObligations().get(0);
                //call sls
                SLSRequestClient slsRequestClient = new SLSRequestClient(SLS_HOST);
                UUID uuid = UUID.randomUUID();
                String id = uuid.toString();
                String labelResultString = slsRequestClient.requestLabelingSecured(id, "Connect XACML", "CCDA", "v3", ccda);
                log.info("Entering Privacy Protective Services");
                log.info("Audit Consent Decision");

            }
            else {
                throw new IOException();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return ccda;
    }

    public PatientConsentConsultHookResponse requestAuthorizationCDSHooks(PatientConsentConsultHookRequest hookRequest) {
        PatientConsentConsultHookResponse res = new PatientConsentConsultHookResponse();
        hookClient = new ConsentConsultCardClient(CDS_HOST);

        try {
            res = hookClient.getConsentDecision(hookRequest);
            log.info(new ObjectMapper().writeValueAsString(res));
            log.info("Audit Consent Decision");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return res;
    }

    public XacmlResponse requestAuthorizationXacml(XacmlRequest xacmlRequest) {
        XacmlResponse res = new XacmlResponse();
        xacmlClient = new ConsentConsultXacmlClient(CDS_HOST);

        try {
            res = xacmlClient.getConsentDecision(xacmlRequest);
            log.info(new ObjectMapper().writeValueAsString(res));
            log.info("Audit Consent Decision");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return res;
    }


}
