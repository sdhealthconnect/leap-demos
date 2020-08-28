package gov.hhs.onc.leap.ces.v2.orchestration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.card.ConsentConsultCardClient;
import gov.hhs.onc.leap.ces.common.clients.model.card.*;
import gov.hhs.onc.leap.ces.fhir.client.utils.FHIRAudit;
import gov.hhs.onc.leap.ces.orchestration.model.LabelingResult;
import gov.hhs.onc.leap.ces.orchestration.model.V2Message;
import gov.hhs.onc.leap.ces.sls.client.SLSRequestClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author duanedecouteau
 */
@Component
@Slf4j
public class V2OrchestrationService {
    private static final Logger log = LoggerFactory.getLogger(V2OrchestrationService.class);
    private ConsentConsultCardClient client;
    private SLSRequestClient slsClient;
    
    @Value("${cds.host.url}")    
    private String CDS_HOST;  // = "https://sdhc-leap.appspot.com";
    
    @Value("${sls.host.url}")
    private String SLS_HOST; // = "http://localhost:9091";

    private String decision;
    private String action;
    private String label;
    private String slsDecision;
    private String slsDetail;

    private PatientConsentConsultHookRequest authRequest;
    private PatientConsentConsultHookResponse authResponse;

    private FHIRAudit fhirAudit = new FHIRAudit();
    
    public String processMessage(String msg) {
        try {
            V2Message v2Message = new V2Message(msg);
            PatientConsentConsultHookResponse decisionResult = requestAuthorization(v2Message);
            Card card = decisionResult.getCards().get(0);
            Extension extension = card.getExtension();
            decision = extension.getDecision();
            if ("CONSENT_PERMIT".equals(decision)) {
                action = extension.getObligations().get(0).getObligationId().getCode();
                label = extension.getObligations().get(0).getParameters().getCodes().get(0).getCode();
            }
            log.info("Decision: "+decision+" Action: "+action+" Label: "+label);

            if (decision.equals("CONSENT_PERMIT")) {
                //make call to sls to investigate
                LabelingResult slsResponse = produceSLSOutcome(msg, v2Message);
                slsDecision = slsResponse.getResult();
                slsDetail = slsResponse.getNotes();
                log.info("SLS PROCESSING COMPLETED");
                if ("REDACT".equals(action) && "RESTRICTED".equals(slsDecision)) {
                    msg = privacyProtect(msg, slsDetail);
                    log.info("Segments of this V2 message have been redacted based on Patient Privacy concerns - PPS");
                }
                else if ("REDACT".equals(action) && !"RESTRICTED".equals(slsDecision)) {
                    msg = msg;
                    log.info("No sensitive segments found allow release of message for further processing");
                }
                else if (("".equals(action) || action.isEmpty() || action == null) && "RESTRICTED".equals(slsDecision)) {
                    msg = privacyProtect(msg, slsDetail);
                    log.info("No patient constraints.  Organization policy does not allow the receipt of sensitive information.  Segnments of this message have been redacted.");
                }
                else if (("".equals(action) || action.isEmpty() || action == null) && !"RESTRICTED".equals(slsDecision)) {
                    msg = msg;
                    log.info("No Patient constraints and SLS have determined message segments are not restricted - Allowing forward.");
                }
                else {
                    log.info("Unable to determine any constraints on this document.  Allowing forward.");
                }
                log.info("Audit Consent Decision");
                fhirAudit.auditConsentDecision(authRequest, authResponse);
            }
            else {
                //authorization does not exist or is denied
                log.info("Authorization for this V2 messaging action is not permitted!");
                msg = "";
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();;
            log.error(ex.getMessage());
            return msg;
        }
        return msg;
    }
    
        
    private PatientConsentConsultHookResponse requestAuthorization(V2Message v2Message) {
        
        PatientId patient = new PatientId().setSystem(v2Message.getPatientIdCodeSystem()).setValue(v2Message.getPatientId());
        Actor actor = new Actor().setSystem(v2Message.getReceivingOrganizationSystem()).setValue(v2Message.getReceivingOrganizationOID());
        Context ctx = new Context().setPatientId(Arrays.asList(patient))
                                   .setPurposeOfUse(Context.PurposeOfUse.TREAT)
                                   .setScope(Context.Scope.PATIENT_PRIVACY)
                                   .setActor(Arrays.asList(actor));

        authRequest = new PatientConsentConsultHookRequest()
                                   .setContext(ctx)
                                   .setHook("patient-consent-consult")
                                   .setHookInstance(UUID.randomUUID().toString());


        
        client = new ConsentConsultCardClient(CDS_HOST);
        authResponse = new PatientConsentConsultHookResponse();

        try {
            log.info(new ObjectMapper().writeValueAsString(authRequest));
            authResponse = client.getConsentDecision(authRequest);
            log.info(new ObjectMapper().writeValueAsString(authResponse));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
   
        return authResponse;
    }
    
    private LabelingResult produceSLSOutcome(String msg, V2Message v2Message) {
        LabelingResult labelingResult = new LabelingResult();
        slsClient = new SLSRequestClient(SLS_HOST);
        String res = slsClient.requestLabelingSecured(UUID.randomUUID().toString(), v2Message.getSendingOrganizationOID(), "V2", "2.5", msg);
        try {
            labelingResult = new ObjectMapper().readValue(res, LabelingResult.class);
        }
        catch (Exception ex) {
            log.warn("Failed result return from SLS");
        }
        log.info("SLS Response: "+res);
        return labelingResult;
    }

    private String privacyProtect(String msg, String slsResults) {
        String ppsResults = "";
        List<String> matchedCodeList = new ArrayList();
        //create list of matched codes
        try {
            Pattern pattern = Pattern.compile("matched code [\\'](.+)[\\']");
            Matcher matcher = pattern.matcher(slsResults);
            while (!matcher.hitEnd()) {
                matcher.find();
                matchedCodeList.add(matcher.group(1));
            }
        }
        catch (Exception ex) {
            //reached end of pattern match
        }
        Iterator iter = matchedCodeList.iterator();
        String resultString = msg;
        while (iter.hasNext()) {
            String lineToRemove = (String)iter.next();
            log.info("PPS Removing: "+lineToRemove);
            resultString = Stream.of(resultString.split("\n"))
                    .filter(s -> !s.contains(lineToRemove))
                    .collect(Collectors.joining("\n"));
        }
        ppsResults = resultString;

        return ppsResults;
    }
}
