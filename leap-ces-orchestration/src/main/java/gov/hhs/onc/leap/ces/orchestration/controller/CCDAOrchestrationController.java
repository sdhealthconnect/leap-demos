package gov.hhs.onc.leap.ces.orchestration.controller;

import gov.hhs.onc.leap.ces.orchestration.service.CCDAOrchestrationService;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookRequest;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookResponse;
import gov.hhs.onc.leap.ces.common.clients.model.ces.PatientConsentConsultHookRequestWithData;
import gov.hhs.onc.leap.ces.common.clients.model.ces.PatientConsentConsultXacmlRequestWithData;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlRequest;
import gov.hhs.onc.leap.ces.common.clients.model.xacml.XacmlResponse;
import gov.hhs.onc.leap.ces.fhir.client.HapiFhirServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;


/**
 *
 * @author duanedecouteau
 */
@RestController
@RequestMapping(path = "/ccdaorchestration")
@Tag(name = "CCDA-Message-Controller", description = "CES Example API for Inbound / Outbound CCDA Message Exchange")
@Slf4j
public class CCDAOrchestrationController {
    public final HapiFhirServer hapiFhirServer;
    public final CCDAOrchestrationService orchestrationService;
    
    public CCDAOrchestrationController(HapiFhirServer hapiFhirServer, CCDAOrchestrationService orchestrationService) {
        this.hapiFhirServer = hapiFhirServer;
        this.orchestrationService = orchestrationService;
    }

    @Operation(summary = "Process CCDA Message With CDS Hooks",
            description = "Perform Authorization Decision and Process Message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authorization of Permit Recieved - Processing of Message is proceeding"),
                    @ApiResponse(responseCode = "404", description = "Authorization of Deny Recieved - Processing of Message has ended")})
    @PostMapping(path = "/processDocumentWithCDSHooks",
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String processDocumentCDSHooks(@RequestBody PatientConsentConsultHookRequestWithData hookRequestAndData) {
        String res = orchestrationService.processDocumentCDSHooks(hookRequestAndData);

        return res;
    }

    @Operation(summary = "Process CCDA Message With XACML",
            description = "Perform Authorization Decision and Process Message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authorization of Permit Recieved - Processing of Message is proceeding"),
                    @ApiResponse(responseCode = "404", description = "Authorization of Deny Recieved - Processing of Message has ended")})
    @PostMapping(path = "/processDocumentWithXACML",
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public String processDocumentXacml(@RequestBody PatientConsentConsultXacmlRequestWithData xacmlRequestWithData) {
        String res = orchestrationService.processDocumentXacml(xacmlRequestWithData);

        return res;
    }

    @Operation(summary = "Request Authorization With CDS HOOKs",
            description = "Perform Authorization Decision",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authorization of Permit Recieved - Processing of Message is proceeding"),
                    @ApiResponse(responseCode = "404", description = "Authorization of Deny Recieved - Processing of Message has ended")})
    @PostMapping(path = "/requestAuthorizationWithCDSHooks",
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public PatientConsentConsultHookResponse requestAuthorizationCDSHooks(@RequestBody PatientConsentConsultHookRequest hookRequest) {
        PatientConsentConsultHookResponse res = orchestrationService.requestAuthorizationCDSHooks(hookRequest);

        return res;
    }

    @Operation(summary = "Request Authorization With Xacml",
            description = "Perform Authorization Decision",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authorization of Permit Recieved - Processing of Message is proceeding"),
                    @ApiResponse(responseCode = "404", description = "Authorization of Deny Recieved - Processing of Message has ended")})
    @PostMapping(path = "/requestAuthorizationWithXacml",
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public XacmlResponse requestAuthorizationXacml(@RequestBody XacmlRequest xacmlRequest) {
        XacmlResponse res = orchestrationService.requestAuthorizationXacml(xacmlRequest);

        return res;
    }

}
