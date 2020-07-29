/*
 * Copyright 2020 esteban.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gov.hhs.onc.leap.ces.v2.orchestration.controller;

import gov.hhs.onc.leap.ces.v2.orchestration.config.Application;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * A test to validate that {@link HttpServletRequest} is secure or not.
 *
 * When we start Tomcat container with
 * some flags:
 *    - server.tomcat.remote-ip-header=x-forwarded-for
 *    - server.tomcat.protocol-header=x-forwarded-proto
 * such env variables indicates Spring Boot to add Tomcat’s own RemoteIpValve automatically.
 * Having that Valve enabled then we should be able to rely on the HttpServletRequest to report whether it is secure or
 * not (even downstream of a proxy server that handles the real SSL termination).
 * The standard behavior is determined by the presence or absence of certain request
 * headers (x-forwarded-for and x-forwarded-proto), whose names are conventional, so it should work with most
 * front-end proxies.
 * This situation can be found when the a non secure Web application in Spring boot application is behind a Load
 * Balancerer that is handling the SSL mechanism.
 *
 * For this test a test controller was created called {@link TestController} that is checking is the httpRequest on
 * controllers are secure or not.
 *
 * @author sebastiangroh@gmail.com
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HTTPRequestSecureTest {
    @LocalServerPort
    private int port;

    private CloseableHttpClient client;

    @Before
    public void doBefore() throws Exception {
         client = HttpClients.custom().build();
    }

    @Test
    public void requestNonSecureTest() throws Exception {
        String uri = "http://localhost:"+port+"/testcontroller/isSecure";
        HttpUriRequest request = RequestBuilder.get()
                .setUri(uri)
                .build();

        CloseableHttpResponse resp = client.execute(request);
        HttpEntity entity = resp.getEntity();
        BufferedReader br = new BufferedReader(new InputStreamReader((entity.getContent())));
        String response = br.readLine();
        assertEquals(200, resp.getStatusLine().getStatusCode());
        assertFalse(Boolean.valueOf(response));
        // Calling same URL with header "X-Forwarded-Proto" and value different to "https" will result in request not secure too
        request = RequestBuilder.get()
                .setUri(uri)
                .setHeader("X-Forwarded-Proto", "http")
                .build();
        resp = client.execute(request);
        entity = resp.getEntity();
        br = new BufferedReader(new InputStreamReader((entity.getContent())));
        response = br.readLine();
        assertEquals(200, resp.getStatusLine().getStatusCode());
        assertFalse(Boolean.valueOf(response));
    }

    @Test
    public void requestSecureTest() throws Exception {
        String uri = "http://localhost:"+port+"/testcontroller/isSecure";
        HttpUriRequest request = RequestBuilder.get()
                .setUri(uri)
                .setHeader("X-Forwarded-Proto", "https")
                .build();
        CloseableHttpResponse resp = client.execute(request);
        HttpEntity entity = resp.getEntity();
        BufferedReader br = new BufferedReader(new InputStreamReader((entity.getContent())));
        String response = br.readLine();
        assertEquals(200, resp.getStatusLine().getStatusCode());
        assertTrue(Boolean.valueOf(response));
    }
}
