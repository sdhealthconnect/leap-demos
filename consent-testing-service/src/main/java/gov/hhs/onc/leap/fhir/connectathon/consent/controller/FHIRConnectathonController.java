package gov.hhs.onc.leap.fhir.connectathon.consent.controller;

import gov.hhs.onc.leap.fhir.connectathon.consent.service.FHIRConnectathonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/fhirconsenttesting")
@Tag(name = "FHIR-Connectathon-Controller", description = "FHIR Connectathon Interface for Testing of Authorization Request and Response")
@Slf4j
public class FHIRConnectathonController {
    public final FHIRConnectathonService fhirConnectathonService;

    public FHIRConnectathonController(FHIRConnectathonService fhirConnectathonService) {
        this.fhirConnectathonService = fhirConnectathonService;
    }

    @Operation(summary = "Authorization Request",
            description = "Request Authorization Decision from LEAP CDS",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authorization of Permit Recieved - Processing of Message is proceeding"),
                    @ApiResponse(responseCode = "404", description = "Authorization of Deny Recieved - Processing of Message has ended")})
    @PostMapping(path = "/authRequest",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_PLAIN_VALUE},
            produces = {MediaType.TEXT_PLAIN_VALUE})

    public String authRequest(@RequestBody String content) {
        String res = fhirConnectathonService.authRequest(content);

        return res;
    }

}
