package gov.hhs.onc.leap.ces.orchestration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.card.ConsentConsultCardClient;
import gov.hhs.onc.leap.ces.common.clients.model.card.*;
import gov.hhs.onc.leap.ces.sls.client.SLSRequestClient;
import gov.hhs.onc.leap.ces.orchestration.model.V2Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author duanedecouteau
 */
@Component
@Slf4j
public class V2OrchestrationService {

    private ConsentConsultCardClient client;
    private SLSRequestClient slsClient;
    
    @Value("${cds.host.url}")    
    private String CDS_HOST;  // = "https://sdhc-leap.appspot.com";
    
    @Value("${sls.host.url}")
    private String SLS_HOST; // = "http://localhost:9091";
    
    public Boolean processMessage(String msg) {
        try {
            V2Message v2Message = new V2Message(msg);
            PatientConsentConsultHookResponse decisionResult = requestAuthorization(v2Message);
            String decision = decisionResult.getCards().get(0).getExtension().getDecision();
            if (decision.equals("CONSENT_PERMIT")) {
                //make call to sls to investigate
                String slsResponse = produceSLSOutcome(msg, v2Message);
                log.info(slsResponse);
                log.info("SLS PROCESSING SUCCESSFUL");
            }
            else {
                return Boolean.FALSE;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();;
            log.error(ex.getMessage());
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
        
    private PatientConsentConsultHookResponse requestAuthorization(V2Message v2Message) {
        
        PatientId patient = new PatientId().setSystem(v2Message.getPatientIdCodeSystem()).setValue(v2Message.getPatientId());
        Actor actor = new Actor().setSystem(v2Message.getReceivingOrganizationSystem()).setValue(v2Message.getReceivingOrganizationOID());
        Context ctx = new Context().setPatientId(Arrays.asList(patient))
                                   .setPurposeOfUse(Context.PurposeOfUse.TREAT)
                                   .setScope(Context.Scope.PATIENT_PRIVACY)
                                   .setActor(Arrays.asList(actor));

        PatientConsentConsultHookRequest request = new PatientConsentConsultHookRequest()
                                   .setContext(ctx)
                                   .setHook("patient-consent-consult")
                                   .setHookInstance(UUID.randomUUID().toString());
        
        
        client = new ConsentConsultCardClient(CDS_HOST);
        PatientConsentConsultHookResponse res = new PatientConsentConsultHookResponse();

        try {
            res = client.getConsentDecision(request);
            log.info(new ObjectMapper().writeValueAsString(res));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
   
        return res;
    }
    
    private String produceSLSOutcome(String msg, V2Message v2Message) {
        slsClient = new SLSRequestClient(SLS_HOST);
        String res = slsClient.requestLabelingSecured(UUID.randomUUID().toString(), v2Message.getSendingOrganizationOID(), "V2", "2.5", msg);
        return res;
    }
}
