/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.hhs.onc.leap.ces.common.clients.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.hhs.onc.leap.ces.common.clients.model.card.PatientConsentConsultHookResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

/** @author duanedecouteau */
public class ConsentConsultCardClient {
  private static final Logger LOGGER = Logger.getLogger(ConsentConsultCardClient.class.getName());
  private final String host;
  private final String endpoint = "/cds-services/patient-consent-consult";
  
  private static final Header CDS_CLIENT_HEADER_CONTENT = new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json");
  private static final Header CDS_CLIENT_HEADER_ACCEPTS = new BasicHeader(HttpHeaders.ACCEPT, "application/json");


  public ConsentConsultCardClient(String host) {
    this.host = host;
  }

  public PatientConsentConsultHookResponse getConsentDecision(String request) throws IOException {

//    URL url = new URL(host + endpoint);
//    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//    conn.setDoOutput(true);
//    conn.setRequestMethod("POST");
//    conn.setRequestProperty("Content-Type", "application/json");
//    conn.setRequestProperty("Accept", "application/json");

    String result = "";
    PatientConsentConsultHookResponse decision = new PatientConsentConsultHookResponse();
    CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier())
                    .setDefaultHeaders(getDefaultHeaders()).build();
    try {

        System.out.println("******* CDS DECISION REQUEST ******* "+host);
        System.out.println(request);
        
        URIBuilder uriBuilder = new URIBuilder(host + endpoint);

        HttpPost postRequest = new HttpPost(uriBuilder.build());

        StringEntity ent = new StringEntity(request, "UTF-8");

        postRequest.setEntity(ent);
        
        HttpResponse response = httpClient.execute(postRequest);
        
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

        String output;
        StringBuffer sb = new StringBuffer();

        while ((output = br.readLine()) != null) {
                sb.append(output);
        }
        
        LOGGER.log(Level.INFO, String.format("CDS Response: ", sb.toString()));

        result = sb.toString();        
        
        System.out.println("******* CDS DECISION RESULT *********");
        System.out.println(result);

        decision = new ObjectMapper().readValue(result, PatientConsentConsultHookResponse.class);

//        OutputStream os = conn.getOutputStream();
//        os.write(request.getBytes());
//        os.flush();
//
//        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//          LOGGER.log(
//              Level.WARNING,
//              "Consent Consult Hook Failed: HTTP error code : " + conn.getResponseCode());
//          throw new RuntimeException(
//              "Consent Consult Hook Failed: HTTP error code : " + conn.getResponseCode());
//        }
//
//        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
//        StringBuffer response = new StringBuffer();
//        while (br.ready()) {
//          response.append("\n" + br.readLine());
//        }
//        conn.disconnect();
//
//        return new ObjectMapper()
//            .readValue(response.toString(), PatientConsentConsultHookResponse.class);
    }
    catch (Exception ex) {
        ex.printStackTrace();
    }
    finally {
        httpClient.close();
    }
    return decision;
  }

    private List<Header> getDefaultHeaders() {
        List<Header> defaultHeaders = new ArrayList<Header>();
        defaultHeaders.add(CDS_CLIENT_HEADER_CONTENT);
        defaultHeaders.add(CDS_CLIENT_HEADER_ACCEPTS);
        return defaultHeaders;
    }  
}
