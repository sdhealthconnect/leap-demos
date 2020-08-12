package gov.hhs.onc.leap.ces.ccda.orchestration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.model.card.Actor;
import gov.hhs.onc.leap.ces.common.clients.model.card.Context;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookRequest;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientId;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.*;
import gov.hhs.onc.leap.ces.orchestration.cds.PatientConsentConsultHookRequestWithData;
import gov.hhs.onc.leap.ces.orchestration.cds.PatientConsentConsultXacmlRequestWithData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creating an Application context configuration for this test
 */
@SpringBootApplication
class TestContextConfiguration {

}

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestContextConfiguration.class)
@ActiveProfiles("test")
public class CCDAOrchestrationServiceTest {
    private static final Logger LOGGER = Logger.getLogger(CCDAOrchestrationServiceTest.class.getName());

    //orchestration endpoint
    @Value("${ccda.host.url}")
    private String host;

    private String endpoint;

    @Test
    public void processCCDADocumentCardTest1() throws Exception {
        endpoint = "/ccdaorchestration/processDocumentWithCDSHooks";
        String sampleCCDA = new String(Files.readAllBytes(Paths.get(
                "src/test/java/gov/hhs/onc/leap/ces/ccda/orchestration/service/fixtures/HIVExampleSLS.xml")),
                "UTF-8");
        PatientId patient =
                new PatientId().setSystem("http://hl7.org/fhir/sid/us-ssn").setValue("111111111");
        Actor actor = new Actor().setSystem("urn:ietf:rfc:3986").setValue("2.16.840.1.113883.20.5");
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

        PatientConsentConsultHookRequestWithData hookRequestWithData = new PatientConsentConsultHookRequestWithData();
        hookRequestWithData.setHookRequest(hookRequest);
        hookRequestWithData.setPayload(sampleCCDA);
        String result = doRequest(hookRequestWithData);
    }

    @Test
    public void processCCDADocumentXacmlTest2() throws  IOException {
        endpoint = "/ccdaorchestration/processDocumentWithXACML";
        String sampleCCDA = new String(Files.readAllBytes(Paths.get(
                "src/test/java/gov/hhs/onc/leap/ces/ccda/orchestration/service/fixtures/HIVExampleSLS.xml")),
                "UTF-8");

        XacmlRequest xacmlRequest = new XacmlRequest();
        Request request = new Request();

        // set requestor info
        AccessSubject subject = new AccessSubject();
        ConceptAttribute subjAttr = new ConceptAttribute().setAttributeId("actor");
        SystemValue subjValue =
                new SystemValue().setSystem("urn:ietf:rfc:3986").setValue("2.16.840.1.113883.20.5");

        subjAttr.setValue(Arrays.asList(subjValue));
        subject.setAttribute(Arrays.asList(subjAttr));
        request.setAccessSubject(Arrays.asList(subject));

        // set resource
        Resource resource = new Resource();
        SystemValue resourceValue =
                new SystemValue().setSystem("http://hl7.org/fhir/sid/us-ssn").setValue("111111111");
        ConceptAttribute resourceAttr =
                new ConceptAttribute().setAttributeId("patientId").setValue(Arrays.asList(resourceValue));
        resource.setAttribute(Arrays.asList(resourceAttr));
        request.setResource(Arrays.asList(resource));

        // set action
        Action action = new Action();
        StringAttribute actionAttrScope =
                new StringAttribute().setAttributeId("scope").setValue("patient-privacy");
        StringAttribute actionAttrPOU =
                new StringAttribute().setAttributeId("purposeOfUse").setValue("TREAT");

        action.setAttribute(Arrays.asList(new StringAttribute[] {actionAttrScope, actionAttrPOU}));
        request.setAction(Arrays.asList(action));
        xacmlRequest.setRequest(request);

        PatientConsentConsultXacmlRequestWithData xacmlRequestWithData  = new PatientConsentConsultXacmlRequestWithData();
        xacmlRequestWithData.setPayload(sampleCCDA);
        xacmlRequestWithData.setXacmlRequest(xacmlRequest);
        String result = doRequest(xacmlRequestWithData);
    }


    @Test
    public void requestAuthorizationWithCDSHooksTest3() throws IOException {
        endpoint = "/ccdaorchestration/requestAuthorizationWithCDSHooks";
        PatientId patient =
                new PatientId().setSystem("http://hl7.org/fhir/sid/us-ssn").setValue("111111111");
        Actor actor = new Actor().setSystem("urn:ietf:rfc:3986").setValue("2.16.840.1.113883.20.5");
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
        String result = doRequest(hookRequest);
    }

    @Test
    public void requestAuthorizationWithXACMLTest4() throws IOException {
        endpoint = "/ccdaorchestration/requestAuthorizationWithXacml";
        XacmlRequest xacmlRequest = new XacmlRequest();
        Request request = new Request();

        // set requestor info
        AccessSubject subject = new AccessSubject();
        ConceptAttribute subjAttr = new ConceptAttribute().setAttributeId("actor");
        SystemValue subjValue =
                new SystemValue().setSystem("urn:ietf:rfc:3986").setValue("2.16.840.1.113883.20.5");

        subjAttr.setValue(Arrays.asList(subjValue));
        subject.setAttribute(Arrays.asList(subjAttr));
        request.setAccessSubject(Arrays.asList(subject));

        // set resource
        Resource resource = new Resource();
        SystemValue resourceValue =
                new SystemValue().setSystem("http://hl7.org/fhir/sid/us-ssn").setValue("111111111");
        ConceptAttribute resourceAttr =
                new ConceptAttribute().setAttributeId("patientId").setValue(Arrays.asList(resourceValue));
        resource.setAttribute(Arrays.asList(resourceAttr));
        request.setResource(Arrays.asList(resource));

        // set action
        Action action = new Action();
        StringAttribute actionAttrScope =
                new StringAttribute().setAttributeId("scope").setValue("patient-privacy");
        StringAttribute actionAttrPOU =
                new StringAttribute().setAttributeId("purposeOfUse").setValue("TREAT");

        action.setAttribute(Arrays.asList(new StringAttribute[] {actionAttrScope, actionAttrPOU}));
        request.setAction(Arrays.asList(action));
        xacmlRequest.setRequest(request);
        String result = doRequest(xacmlRequest);
    }

    private String makeRequest(String request) throws IOException {
        URL url = new URL(host + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        OutputStream os = conn.getOutputStream();
        os.write(request.getBytes());
        os.flush();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            LOGGER.log(
                    Level.WARNING,
                    "CCDA Orchestration Failed: HTTP error code : " + conn.getResponseCode());
            throw new RuntimeException(
                    "CCDA Orchestration Failed: HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuffer response = new StringBuffer();
        while (br.ready()) {
            response.append("\n" + br.readLine());
        }
        conn.disconnect();

        return response.toString();
    }

    private String doRequest(final Object reqData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String request = mapper.writeValueAsString(reqData);
        return makeRequest(request);
    }
}
