package gov.hhs.onc.leap.ces.fhir.client.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.model.card.*;
import gov.hhs.onc.leap.ces.fhir.client.utils.FHIRAudit;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Test;

import java.util.Arrays;

public class FHIRAuditTest {

    private String hookRequestString = "{" +
            "  \"hook\": \"patient-consent-consult\"," +
            "  \"hookInstance\": \"1234\"," +
            "  \"context\": {" +
            "    \"patientId\": [" +
            "      {" +
            "        \"system\": \"http://hl7.org/fhir/sid/us-medicare\"," +
            "        \"value\": \"0000-000-0000\"" +
            "      }" +
            "    ]," +
            "    \"scope\": \"patient-privacy\"," +
            "    \"actor\": [" +
            "      {" +
            "        \"system\": \"test-system\"," +
            "        \"value\": \"test-value\"" +
            "      }" +
            "    ]" +
            "  }" +
            "}";

    private String hookResponseString = "{" +
            " \"cards\": [" +
            "    {" +
            "      \"summary\": \"CONSENT_PERMIT\"," +
            "      \"detail\": \"There is a patient consent permitting this action.\"," +
            "      \"indicator\": \"info\"," +
            "      \"source\": {" +
            "        \"label\": \"Sample\"," +
            "        \"url\": \"https://sample-cdms.org\"" +
            "      }," +
            "      \"extension\": {" +
            "        \"decision\": \"CONSENT_PERMIT\"," +
            "        \"obligations\": [" +
            "          {" +
            "            \"id\": {" +
            "              \"system\": \"http://terminology.hl7.org/CodeSystem/v3-ActCode\"," +
            "              \"code\": \"REDACT\"" +
            "            }," +
            "            \"parameters\": {" +
            "              \"codes\": [" +
            "                {" +
            "                  \"system\":" +
            "                    \"http://terminology.hl7.org/CodeSystem/v3-Confidentiality\"," +
            "                  \"code\": \"R\"" +
            "                }" +
            "              ]" +
            "            }" +
            "          }" +
            "        ]" +
            "      }" +
            "    }" +
            "  ]" +
            "}";

    private FHIRAudit fhirAudit = new FHIRAudit();

    @Test
    public void testFhirAudit1() {
        PatientId patient =
                new PatientId()
                        .setSystem("http://hl7.org/fhir/sid/us-ssn")
                        .setValue("111111111");

        Actor actor =
                new Actor()
                        .setSystem("urn:ietf:rfc:3986")
                        .setValue("2.16.840.1.113883.20.5");

        Context context =
                new Context()
                        .setPatientId(Arrays.asList(patient))
                        .setPurposeOfUse(Context.PurposeOfUse.TREAT)
                        .setScope(Context.Scope.PATIENT_PRIVACY)
                        .setActor(Arrays.asList(actor));

        PatientConsentConsultHookRequest hookRequest =
                new PatientConsentConsultHookRequest()
                        .setContext(context)
                        .setHook("patient-consent-consult")
                        .setHookInstance("123456");

        PatientConsentConsultHookResponse hookResponse = new PatientConsentConsultHookResponse();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            hookResponse = objectMapper.readValue(hookResponseString, PatientConsentConsultHookResponse.class);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        fhirAudit.auditConsentDecision(hookRequest, hookResponse);

    }



}
