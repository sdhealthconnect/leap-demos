package gov.hhs.onc.leap.ces.v2.orchestration.controller;

import gov.hhs.onc.leap.ces.fhir.client.HapiFhirServer;
import gov.hhs.onc.leap.ces.v2.orchestration.service.V2OrchestrationService;
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
@RequestMapping(path = "/v2orchestration")
@Tag(name = "V2-Message-Controller", description = "CES Example API for Inbound V2 Message Exchange")
@Slf4j
public class V2OrchestrationController {
    public final HapiFhirServer hapiFhirServer;
    public final V2OrchestrationService orchestrationService;
    
    public V2OrchestrationController(HapiFhirServer hapiFhirServer, V2OrchestrationService orchestrationService) {
        this.hapiFhirServer = hapiFhirServer;
        this.orchestrationService = orchestrationService;
    }

    @Operation(summary = "Process V2 Message",
            description = "Perform Authorization Decision and Process Message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authorization of Permit Recieved - Processing of Message is proceeding"),
                    @ApiResponse(responseCode = "404", description = "Authorization of Deny Recieved - Processing of Message has ended")})
    @PostMapping(path = "/processMessage", 
            consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    
    public String processMessage(@RequestBody String content) {
        Boolean res = orchestrationService.processMessage(content);

        return res.toString();
    }
}
