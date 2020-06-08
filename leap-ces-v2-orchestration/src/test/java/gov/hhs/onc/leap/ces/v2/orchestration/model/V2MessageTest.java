package gov.hhs.onc.leap.ces.v2.orchestration.model;

import gov.hhs.onc.leap.ces.orchestration.model.V2Message;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class V2MessageTest {
    @Test
    public void processMessageTest() throws Exception {
        String sampleV2Message = new String(Files.readAllBytes(Paths.get(
                "src/test/java/gov/hhs/onc/leap/ces/v2/orchestration/model/fixtures/sample-v2-message.txt")),
                "UTF-8");

        V2Message v2Message = new V2Message(sampleV2Message);

        assert("111111111".equals(v2Message.getPatientId()));
        assert("2.16.840.1.113883.20.5".equals(v2Message.getReceivingOrganizationOID()));
        assert("2.16.840.1.113883.2".equals(v2Message.getSendingOrganizationOID()));
        assert("20075091019450028".equals(v2Message.getMessageId()));
    }
}
