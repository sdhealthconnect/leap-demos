package gov.hhs.onc.leap.ces.ccda.orchestration.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.model.card.Actor;
import gov.hhs.onc.leap.ces.common.clients.model.card.Context;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookRequest;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientId;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.AccessSubject;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.Action;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.ConceptAttribute;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.Request;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.Resource;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.StringAttribute;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.SystemValue;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlRequest;
import gov.hhs.onc.leap.ces.orchestration.cds.PatientConsentConsultHookRequestWithData;
import gov.hhs.onc.leap.ces.orchestration.cds.PatientConsentConsultXacmlRequestWithData;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CCDAOrchestrationServiceTest {
    private static final Logger LOGGER = Logger.getLogger(CCDAOrchestrationServiceTest.class.getName());
    //orchestration endpoint
    private String host =  "http://34.94.253.50:9093";
    private String endpoint;
    private String baseDirectory = "/Users/duanedecouteau/leap-demos/test-scripts/message-artifacts/";

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

        ObjectMapper mapper = new ObjectMapper();
        String request = mapper.writeValueAsString(hookRequestWithData);
        //writeRequestToFile(request, "CCDACardTestWithDcoument.json");

        String result = makeRequest(request);

        //System.out.println(result);

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

        ObjectMapper mapper = new ObjectMapper();
        String requestString = mapper.writeValueAsString(xacmlRequestWithData);
        //writeRequestToFile(requestString, "CCDAXacmlTestWithDocument.json");

        String result = makeRequest(requestString);

        //System.out.println(result);
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


        ObjectMapper mapper = new ObjectMapper();
        String request = mapper.writeValueAsString(hookRequest);
        //writeRequestToFile(request, "CCDACardAuthTest.json");

        String result = makeRequest(request);

        //System.out.println(result);
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
        ObjectMapper mapper = new ObjectMapper();
        String xacmlRequestString = mapper.writeValueAsString(xacmlRequest);
        //writeRequestToFile(xacmlRequestString, "CCDAXacmlTest.json");

        String result = makeRequest(xacmlRequestString);

        //System.out.println(result);

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

    private void writeRequestToFile(String requestObject, String fileName) {
        try {
            fileName = baseDirectory+fileName;
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(requestObject);

            writer.close();
        }
        catch (IOException ix) {
            LOGGER.log(Level.WARNING, "Failed to write request. "+ix.getMessage());
        }
    }
}
