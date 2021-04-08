package gov.hhs.onc.leap.ces.fhir.client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.model.card.Context;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookRequest;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookResponse;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.AccessSubject;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.StringAttribute;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlRequest;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlResponse;
import gov.hhs.onc.leap.ces.fhir.client.HapiFhirServer;

import org.hl7.fhir.r4.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class  FHIRAudit {
    private PatientConsentConsultHookRequest hookRequest;
    private PatientConsentConsultHookResponse hookResponse;
    private XacmlResponse xacmlResponse;
    private XacmlRequest xacmlRequest;

    private String decision;

    private HapiFhirServer client;

    public void auditConsentDecision(PatientConsentConsultHookRequest hookRequest, PatientConsentConsultHookResponse hookResponse) {
        this.hookRequest = hookRequest;
        this.hookResponse = hookResponse;

        decision = hookResponse.getCards().get(0).getExtension().getDecision();

        client = new HapiFhirServer();
        client.setUp();

        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setId(UUID.randomUUID().toString());
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
        agentReference.setIdentifier(agentId);
        agent.setWho(agentReference);
        auditEvent.addAgent(agent);

        Reference patientReference = new Reference();
        Identifier patientId = new Identifier();
        patientId.setId(hookRequest.getContext().getPatientId().get(0).getValue());
        patientId.setSystem(hookRequest.getContext().getPatientId().get(0).getSystem());

        Patient p = client.getPatient(patientId.getId());
        patientReference.setReference(p.getResourceType().name()+"/"+p.getIdElement().getIdPart());
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

        Bundle bundle = client.createAndExecuteBundle(auditEvent);

    }
    public void auditConsentDecision(XacmlRequest xacmlRequest, XacmlResponse xacmlResponse) {
        this.xacmlRequest = xacmlRequest;
        this.xacmlResponse = xacmlResponse;

        decision = xacmlResponse.getResponse().get(0).getDecision();

        AuditEvent auditEvent = new AuditEvent();

        Coding auditType = new Coding();
        auditEvent.setId(UUID.randomUUID().toString());
        auditType.setCode("110110");
        auditType.setSystem("http://hl7.org/fhir/ValueSet/audit-event-type");
        auditType.setDisplay("Patient Record");
        auditEvent.setType(auditType);

        Narrative narrative = new Narrative();
        ObjectMapper mapper = new ObjectMapper();
        try {
            narrative.setDivAsString(mapper.writeValueAsString(xacmlRequest));
            auditEvent.setText(narrative);
        } catch (Exception ex) {}

        String pou = "";
        String scope = "";
        List<StringAttribute> lAction = xacmlRequest.getRequest().getAction().get(0).getAttribute();
        Iterator iter = lAction.iterator();
        while (iter.hasNext()) {
            StringAttribute sAttr = (StringAttribute)iter.next();
            if (sAttr.getAttributeId().equals("scope")) scope = sAttr.getValue();
            if (sAttr.getAttributeId().equals("purposeOfUse")) pou = sAttr.getValue();
        }

        CodeableConcept purpose = new CodeableConcept();
        Coding coding = purpose.addCoding();
        coding.setCode(pou);
        coding.setSystem("http://terminology.hl7.org/ValueSet/v3-PurposeOfUse");
        List<Coding> lCoding = new ArrayList<Coding>();
        lCoding.add(coding);
        purpose.setCoding(lCoding);

        AuditEvent.AuditEventOutcome eventOutcome = AuditEvent.AuditEventOutcome._0;
        auditEvent.setOutcome(eventOutcome);
        auditEvent.setOutcomeDesc(decision);

        auditEvent.setAction(AuditEvent.AuditEventAction.R);

        String subjectSystem = "";
        String subjectValue = "";
        String patientSystem = "";
        String patientValue = "";

        List<AccessSubject> lAccessSubject = xacmlRequest.getRequest().getAccessSubject();
        subjectSystem = lAccessSubject.get(0).getAttribute().get(0).getAttributeId();
        subjectValue = lAccessSubject.get(0).getAttribute().get(0).getValue().get(0).getValue();

        patientSystem = xacmlRequest.getRequest().getResource().get(0).getAttribute().get(0).getAttributeId();
        patientValue = xacmlRequest.getRequest().getResource().get(0).getAttribute().get(0).getValue().get(0).getValue();

        AuditEvent.AuditEventAgentComponent agent = new AuditEvent.AuditEventAgentComponent();
        Reference agentReference = new Reference();
        Identifier agentId = new Identifier();
        agentId.setId(subjectValue);
        agentId.setSystem(subjectSystem);
        agentReference.setIdentifier(agentId);
        agent.setWho(agentReference);
        auditEvent.addAgent(agent);

        Reference patientReference = new Reference();
        Identifier patientId = new Identifier();
        patientId.setSystem(patientSystem);
        patientId.setId(patientValue);
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
            securityActionCoding.setCode(xacmlResponse.getResponse().get(0).getObligations().get(0).getObligationId().getCode());
            securityActionCoding.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode");
            //label
            securityLabelCoding.setCode(xacmlResponse.getResponse().get(0).getObligations().get(0).getAttributeAssignments().get(0).getSystemCodes().get(0).getCode());
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

        client = new HapiFhirServer();
        client.setUp();
        Bundle bundle = client.createAndExecuteBundle(auditEvent);

    }

}
