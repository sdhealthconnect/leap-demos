package gov.hhs.onc.leap.ces.orchestration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.fha.nhinc.properties.PropertyAccessException;
import gov.hhs.fha.nhinc.properties.PropertyAccessor;
import gov.hhs.onc.leap.ces.common.clients.card.ConsentConsultCardClient;
import gov.hhs.onc.leap.ces.common.clients.model.card.Card;
import gov.hhs.onc.leap.ces.common.clients.model.card.Extension;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookRequest;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookResponse;
import gov.hhs.onc.leap.ces.common.clients.model.sls.LabelingResult;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlRequest;
import gov.hhs.onc.leap.ces.common.clients.xacml.ConsentConsultXacmlClient;
import gov.hhs.onc.leap.ces.orchestration.cds.PatientConsentConsultHookRequestWithData;
import gov.hhs.onc.leap.ces.orchestration.cds.PatientConsentConsultHookResponseWithData;
import gov.hhs.onc.leap.ces.orchestration.cds.PatientConsentConsultXacmlRequestWithData;
import gov.hhs.onc.leap.ces.common.clients.sls.SLSRequestClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final String PROPERTY_FILE = "LEAP.properties";

    private static final Logger log = LoggerFactory.getLogger(CCDAOrchestrationService.class);
    private ConsentConsultCardClient hookClient;
    private ConsentConsultXacmlClient xacmlClient;
    private SLSRequestClient slsClient;

    private PatientConsentConsultHookRequestWithData hookRequestWithData;
    private PatientConsentConsultHookRequest hookRequest;
    private PatientConsentConsultXacmlRequestWithData xacmlRequestWithData;
    private XacmlRequest xacmlRequest;

    private String decision;
    private String action = "";
    private String label = "";


    private String CDS_HOST;

    private String SLS_HOST;

    public PatientConsentConsultHookResponseWithData processDocumentCDSHooks(PatientConsentConsultHookRequestWithData hookRequestWithData) {
        setEnvironment();
        String ccda = hookRequestWithData.getPayload();
        PatientConsentConsultHookResponseWithData hookResponseWithData = new PatientConsentConsultHookResponseWithData();
        try {
            this.hookRequestWithData = hookRequestWithData;
            this.hookRequest = hookRequestWithData.getHookRequest();
            PatientConsentConsultHookRequest request = hookRequestWithData.getHookRequest();
            ConsentConsultCardClient cardClient = new ConsentConsultCardClient(CDS_HOST);
            String requestString = new ObjectMapper().writeValueAsString(request);
            PatientConsentConsultHookResponse response = cardClient.getConsentDecision(requestString);

            Card card = response.getCards().get(0);
            Extension extension = card.getExtension();
            decision = extension.getDecision();
            if ("CONSENT_PERMIT".equals(decision)) {
                action = extension.getObligations().get(0).getObligationId().getCode();
                label = extension.getObligations().get(0).getParameters().getCodes().get(0).getCode();
            }
            log.info("Decision: "+decision+" Action: "+action+" Label: "+label);
            if ("CONSENT_PERMIT".equals(decision)) {
                List<gov.hhs.onc.leap.ces.common.clients.model.card.Obligations> lObligations = response.getCards().get(0).getExtension().getObligations();
                //call sls
                SLSRequestClient slsRequestClient = new SLSRequestClient(SLS_HOST);
                UUID uuid = UUID.randomUUID();
                String id = uuid.toString();
                String lablelResultString = slsRequestClient.requestLabelingSecured(id, "Connect CARD", "CCDA", "v3", ccda);
                LabelingResult slsResult = new ObjectMapper().readValue(lablelResultString, LabelingResult.class);
                log.info("SLS: "+lablelResultString);
                log.info("Entering Privacy Protective Flow");
                if ("REDACT".equals(action) && "RESTRICTED".equals(slsResult.getResult())) {
                    //there is no NLP in play in this demonstration so remove all and not change
                    ccda = "Contents of this document have been redacted based on Patient Privacy concerns - SLS";
                    log.info(ccda);
                }
                else if ("REDACT".equals(action) && !"RESTRICTED".equals(slsResult.getResult())) {
                    ccda = "Contents of this document have been redacted due to Patient Privacy concerns - NLP";
                    log.info(ccda);
                }
                else if (("".equals(action) || action.isEmpty() || action == null) && "RESTRICTED".equals(slsResult.getResult())) {
                    ccda = "Contents of this document have been redacted due to Patient Privacy concerns - Organization Policy";
                    log.info(ccda);
                    //maybe apply confidentiality code later
                }
                else if (("".equals(action) || action.isEmpty() || action == null) && !"RESTRICTED".equals(slsResult.getResult())) {
                    log.info("No Patient constraints and SLS have determined Documents Structured components are not restricted - Allowing forward.");
                }
                else {
                    log.info("Unable to determine any constraints on this document.  Allowing forward.");
                }
                log.info("Audit Consent Decision");
            }
            hookResponseWithData.setHookResponse(response);
            hookResponseWithData.setPayload(ccda);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return hookResponseWithData;
    }

    public PatientConsentConsultHookResponse requestAuthorizationCDSHooks(PatientConsentConsultHookRequest hookRequest) {
        PatientConsentConsultHookResponse res = new PatientConsentConsultHookResponse();
        hookClient = new ConsentConsultCardClient(CDS_HOST);

        try {
            res = hookClient.getConsentDecision(new ObjectMapper().writeValueAsString(hookRequest));
            log.info(new ObjectMapper().writeValueAsString(res));
            log.info("Audit Consent Decision");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return res;
    }

    private void setEnvironment() {
        try {
            CDS_HOST = PropertyAccessor.getInstance().getProperty(PROPERTY_FILE, "cds.host.url"));
        } catch (PropertyAccessException e) {
            log.error(
                    "PropertyAccessException - Default CDS HOST property not defined in LEAP.properties : {} ",
                    e);
        }
        try {
            SLS_HOST = PropertyAccessor.getInstance().getProperty(PROPERTY_FILE, "sls.host.url"));
        } catch (PropertyAccessException e) {
            log.error(
                    "PropertyAccessException - Default SLS HOST property not defined in LEAP.properties : {} ",
                    e);
        }

    }

}
