package gov.hhs.onc.leap.ces.v2.orchestration.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

/**
 * A test controller to check if the {@link HttpServletRequest} is secure.
 *
 * To understand different invocation mechanisms please read {@link HTTPRequestSecureTest}
 *
 * @author sebastiangroh@gmail.com
 */
@RestController
@RequestMapping(path = "/testcontroller")
public class TestController {

    @GetMapping(path = "/isSecure",
                produces = {MediaType.TEXT_PLAIN})
    public String isSecure(HttpServletRequest httpServletRequest) {
        return Boolean.valueOf(httpServletRequest.isSecure()).toString();
    }
}
