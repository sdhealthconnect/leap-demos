package gov.hhs.onc.leap.ces.ccda.orchestration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.card.ConsentConsultCardClient;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookRequest;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookResponse;
import gov.hhs.onc.leap.ces.common.clients.model.card.Card;
import gov.hhs.onc.leap.ces.common.clients.model.card.Extension;
import gov.hhs.onc.leap.ces.fhir.client.utils.FHIRAudit;
import gov.hhs.onc.leap.ces.orchestration.cds.*;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.Obligations;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.Response;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlRequest;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlResponse;
import gov.hhs.onc.leap.ces.common.clients.xacml.ConsentConsultXacmlClient;
import gov.hhs.onc.leap.ces.sls.client.SLSRequestClient;
import gov.hhs.onc.leap.ces.orchestration.model.LabelingResult;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(CCDAOrchestrationService.class);
    private ConsentConsultCardClient hookClient;
    private ConsentConsultXacmlClient xacmlClient;
    private SLSRequestClient slsClient;

    private PatientConsentConsultHookRequestWithData hookRequestWithData;
    private PatientConsentConsultHookRequest hookRequest;
    private PatientConsentConsultHookResponse hookResponse;
    private PatientConsentConsultXacmlRequestWithData xacmlRequestWithData;
    private XacmlRequest xacmlRequest;
    private XacmlResponse xacmlResponse;

    private String decision;
    private String action = "";
    private String label = "";

    private FHIRAudit fhirAudit  = new FHIRAudit();
    
    @Value("${cds.host.url}")    
    private String CDS_HOST;  // = "https://sdhc-leap.appspot.com";
    
    @Value("${sls.host.url}")
    private String SLS_HOST; // = "http://localhost:9091";

    public PatientConsentConsultHookResponseWithData processDocumentCDSHooks(PatientConsentConsultHookRequestWithData hookRequestWithData) {
        String ccda = hookRequestWithData.getPayload();
        PatientConsentConsultHookResponseWithData hookResponseWithData = new PatientConsentConsultHookResponseWithData();
        try {
            this.hookRequestWithData = hookRequestWithData;
            this.hookRequest = hookRequestWithData.getHookRequest();
            PatientConsentConsultHookRequest request = hookRequestWithData.getHookRequest();
            ConsentConsultCardClient cardClient = new ConsentConsultCardClient(CDS_HOST);
            String requestString = new ObjectMapper().writeValueAsString(request);
            hookResponse = cardClient.getConsentDecision(requestString);

            Card card = hookResponse.getCards().get(0);
            Extension extension = card.getExtension();
            decision = extension.getDecision();
            action = "";
            label = "";
            try {
                if ("CONSENT_PERMIT".equals(decision)) {
                    action = extension.getObligations().get(0).getObligationId().getCode();
                    label = extension.getObligations().get(0).getParameters().getCodes().get(0).getCode();
                }
            }
            catch (Exception ex) {
                //no obligations to process
            }
            log.info("Decision: "+decision+" Action: "+action+" Label: "+label);
            if ("CONSENT_PERMIT".equals(decision)) {
                List<gov.hhs.onc.leap.ces.common.clients.model.card.Obligations> lObligations = hookResponse.getCards().get(0).getExtension().getObligations();
                //call sls
                SLSRequestClient slsRequestClient = new SLSRequestClient(SLS_HOST);
                UUID uuid = UUID.randomUUID();
                String id = uuid.toString();
                String lablelResultString = slsRequestClient.requestLabelingSecured(id, "Connect CARD", "CCDA", "v3", ccda);
                LabelingResult slsResult = new ObjectMapper().readValue(lablelResultString, LabelingResult.class);
                log.info("SLS: " + lablelResultString);
                log.info("Entering Privacy Protective Flow");
                if ("REDACT".equals(action) && "RESTRICTED".equals(slsResult.getResult())) {
                    //there is no NLP in play in this demonstration so remove all and not change
                    ccda = "Contents of this document have been redacted based on Patient Privacy concerns - SLS";
                    log.info(ccda);
                } else if ("REDACT".equals(action) && !"RESTRICTED".equals(slsResult.getResult())) {
                    ccda = "Contents of this document have been redacted due to Patient Privacy concerns - NLP";
                    log.info(ccda);
                } else if (("".equals(action) || action.isEmpty() || action == null) && "RESTRICTED".equals(slsResult.getResult())) {
                    ccda = "Contents of this document have been redacted due to Patient Privacy concerns - Organization Policy";
                    log.info(ccda);
                    //maybe apply confidentiality code later
                } else if (("".equals(action) || action.isEmpty() || action == null) && !"RESTRICTED".equals(slsResult.getResult())) {
                    log.info("No Patient constraints and SLS have determined Documents Structured components are not restricted - Allowing forward.");
                } else {
                    log.info("Unable to determine any constraints on this document.  Allowing forward.");
                }
                log.info("Audit Consent Decision");
                try {
                    auditConsentDecisionCDSHooks();
                } catch (Exception ex) {
                }
            }
            hookResponseWithData.setHookResponse(hookResponse);
            hookResponseWithData.setPayload(ccda);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return hookResponseWithData;
    }

    public PatientConsentConsultXacmlResponseWithData processDocumentXacml(PatientConsentConsultXacmlRequestWithData xacmlRequestWithData) {
        String ccda = xacmlRequestWithData.getPayload();
        PatientConsentConsultXacmlResponseWithData xacmlResponseWithData = new PatientConsentConsultXacmlResponseWithData();
        try {
            this.xacmlRequestWithData = xacmlRequestWithData;
            this.xacmlRequest = xacmlRequestWithData.getXacmlRequest();

            xacmlClient = new ConsentConsultXacmlClient(CDS_HOST);
            xacmlResponse = xacmlClient.getConsentDecision(xacmlRequest);
            Response response = xacmlResponse.getResponse().get(0);
            decision = response.getDecision();
            if ("Permit".equals(decision)) {
                action = response.getObligations().get(0).getObligationId().getCode();
                label = response.getObligations().get(0).getAttributeAssignments().get(0).getSystemCodes().get(0).getCode();
            }
            System.out.println("Decision: "+decision+" Action: "+action+" Label: "+label);
            log.info("Decision: "+decision+" Action: "+action+" Label: "+label);
            if (response.getDecision().equals("Permit")) {
                Obligations obligation = response.getObligations().get(0);
                //call sls
                SLSRequestClient slsRequestClient = new SLSRequestClient(SLS_HOST);
                UUID uuid = UUID.randomUUID();
                String id = uuid.toString();
                String lablelResultString = slsRequestClient.requestLabelingSecured(id, "Connect XACML", "CCDA", "v3", ccda);
                LabelingResult slsResult = new ObjectMapper().readValue(lablelResultString, LabelingResult.class);
                log.info("SLS: " + lablelResultString);
                log.info("Entering Privacy Protective Flow");
                if ("REDACT".equals(action) && "RESTRICTED".equals(slsResult.getResult())) {
                    //there is no NLP in play in this demonstration so remove all and not change
                    ccda = "Contents of this document have been redacted based on Patient Privacy concerns - SLS";
                    log.info(ccda);
                } else if ("REDACT".equals(action) && !"RESTRICTED".equals(slsResult.getResult())) {
                    ccda = "Contents of this document have been redacted due to Patient Privacy concerns - NLP";
                    log.info(ccda);
                } else if (("".equals(action) || action.isEmpty() || action == null) && "RESTRICTED".equals(slsResult.getResult())) {
                    ccda = "Contents of this document have been redacted due to Patient Privacy concerns - Organization Policy";
                    log.info(ccda);
                    //maybe apply confidentiality code later
                } else if (("".equals(action) || action.isEmpty() || action == null) && !"RESTRICTED".equals(slsResult.getResult())) {
                    log.info("No Patient constraints and SLS have determined Documents Structured components are not restricted - Allowing forward.");
                } else {
                    log.info("Unable to determine any constraints on this document.  Allowing forward.");
                }
                log.info("Audit Consent Decision");
                try {
                    auditConsentDecisionXacml();
                } catch (Exception ex) {
                }
            }
            xacmlResponseWithData.setXacmlResponse(xacmlResponse);
            xacmlResponseWithData.setPayload(ccda);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return xacmlResponseWithData;
    }

    public PatientConsentConsultHookResponse requestAuthorizationCDSHooks(PatientConsentConsultHookRequest hookRequest) {
        PatientConsentConsultHookResponse res = new PatientConsentConsultHookResponse();
        hookClient = new ConsentConsultCardClient(CDS_HOST);

        try {
            res = hookClient.getConsentDecision(hookRequest);
            log.info(new ObjectMapper().writeValueAsString(res));
            log.info("Audit Consent Decision");
            auditConsentDecisionCDSHooks();
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
            auditConsentDecisionXacml();
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return res;
    }

    public String processDocumentWithObligation(DocumentWithObligations documentWithObligations) {
        String res = "";
        String ccda = documentWithObligations.getDocument();
        try {
            String action = documentWithObligations.getObligation();
            SLSRequestClient slsRequestClient = new SLSRequestClient(SLS_HOST);
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();
            String lablelResultString = slsRequestClient.requestLabelingSecured(id, "Doc and Obligation", "CCDA", "v3", ccda);
            LabelingResult slsResult = new ObjectMapper().readValue(lablelResultString, LabelingResult.class);
            log.info("SLS: " + lablelResultString);
            log.info("Entering Privacy Protective Flow");
            if ("REDACT".equals(action) && "RESTRICTED".equals(slsResult.getResult())) {
                //there is no NLP in play in this demonstration so remove all and not change
                ccda = "Contents of this document have been redacted based on Patient Privacy concerns - SLS";
                log.info(ccda);
            } else if ("REDACT".equals(action) && !"RESTRICTED".equals(slsResult.getResult())) {
                ccda = "Contents of this document have been redacted due to Patient Privacy concerns - NLP";
                log.info(ccda);
            } else if (("".equals(action) || action.isEmpty() || action == null) && "RESTRICTED".equals(slsResult.getResult())) {
                ccda = "Contents of this document have been redacted due to Patient Privacy concerns - Organization Policy";
                log.info(ccda);
                //maybe apply confidentiality code later
            } else if (("".equals(action) || action.isEmpty() || action == null) && !"RESTRICTED".equals(slsResult.getResult())) {
                log.info("No Patient constraints and SLS have determined Documents Structured components are not restricted - Allowing forward.");
            } else {
                log.info("Unable to determine any constraints on this document.  Allowing forward.");
            }
        }
        catch (Exception ex) {
            log.error("Failed to process CCDA document "+ex.getMessage());
        }
        res = ccda;
        return res;
    }

    private void auditConsentDecisionCDSHooks() {
        try {
            fhirAudit.auditConsentDecision(hookRequest, hookResponse);
        }
        catch  (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void auditConsentDecisionXacml() {
        try {
            fhirAudit.auditConsentDecision(xacmlRequest, xacmlResponse);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
