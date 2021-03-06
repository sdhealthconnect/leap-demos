package gov.hhs.onc.leap.ces.utils;

import gov.hhs.onc.leap.ces.common.clients.fhir.HapiFhirServer;
import gov.hhs.onc.leap.ces.common.clients.model.card.Context;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookRequest;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookResponse;
import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.List;

public class  FHIRAudit {
    private PatientConsentConsultHookRequest hookRequest;
    private PatientConsentConsultHookResponse hookResponse;
    private PatientConsentConsultXacmlResponse xacmlResponse;
    private PatientConsentConsultXacmlRequest xacmlRequest;
    private String decision;

    public void auditConsentDecision(PatientConsentConsultHookRequest hookRequest, PatientConsentConsultHookResponse hookResponse) {
        this.hookRequest = hookRequest;
        this.hookResponse = hookResponse;

        decision = hookResponse.getCards().get(0).getExtension().getDecision();

        AuditEvent auditEvent = new AuditEvent();

        Coding auditType = new Coding();
        auditType.setCode("110110");
        auditType.setSystem("http://hl7.org/fhir/ValueSet/audit-event-type");
        auditType.setDisplay("Patient Record");
        auditEvent.setType(auditType);

        Narrative narrative = new Narrative();
        narrative.setDivAsString(hookRequest.getHook());
        auditEvent.setText(narrative);

        Context hookContext = hookRequest.getContext();
        //add purpose of use
        CodeableConcept purpose = new CodeableConcept();
        Coding coding = purpose.addCoding();
        coding.setCode(hookContext.getPurposeOfUse().value());
        coding.setSystem("http://terminology.hl7.org/ValueSet/v3-PurposeOfUse");
        List<Coding> lCoding = new ArrayList<Coding>();
        lCoding.add(coding);
        purpose.setCoding(lCoding);

        AuditEvent.AuditEventOutcome eventOutcome = AuditEvent.AuditEventOutcome._0;
        auditEvent.setOutcome(eventOutcome);
        auditEvent.setOutcomeDesc(decision);

        auditEvent.setAction(AuditEvent.AuditEventAction.R);

        AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();
        Reference agentReference = new Reference();
        Identifier agentId = new Identifier();
        agentId.setId(hookRequest.getContext().getActor().get(0).getValue());
        agentId.setSystem(hookRequest.getContext().getActor().get(0).getSystem());

        auditEvent.addAgent(agent);

        Reference patientReference = new Reference();
        Identifier patientId = new Identifier();
        patientId.setSystem(hookRequest.getContext().getPatientId().get(0).getValue());
        patientId.setId(hookRequest.getContext().getPatientId().get(0).getSystem());
        patientReference.setIdentifier(patientId);

        AuditEvent.AuditEventEntityComponent entityComp = new AuditEvent.AuditEventEntityComponent();
        entityComp.setWhat(patientReference);

        //security label Action
        Coding securityActionCoding = new Coding();
        //security label
        Coding securityLabelCoding = new Coding();
        //obligation may be null so
        try {
            //action
            securityActionCoding.setCode(hookResponse.getCards().get(0).getExtension().getObligations().get(0).getObligationId().getCode());
            securityActionCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");
            //label
            securityLabelCoding.setCode(hookResponse.getCards().get(0).getExtension().getObligations().get(0).getParameters().getCodes().get(0).getCode());
            securityLabelCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-Confidentiality");

            entityComp.addSecurityLabel(securityLabelCoding);
            entityComp.addSecurityLabel(securityActionCoding);
        }
        catch (Exception ex) {
            //ignore
        }


        List<AuditEvent.AuditEventEntityComponent>  lEntities = new ArrayList<AuditEvent.AuditEventEntityComponent>();
        lEntities.add(entityComp);

        auditEvent.setEntity(lEntities);

        HapiFhirServer client = new HapiFhirServer();
        client.setUp();
        Bundle bundle = client.createAndExecuteBundle(auditEvent);

    }


}
