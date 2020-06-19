package gov.hhs.onc.leap.ces.v2.orchestration.pps;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class V2PrivacyProtection {
    @Test
    public void protectMessage() throws Exception {
        List<String> matchedCodeList = new ArrayList();
        String sampleV2Message = new String(Files.readAllBytes(Paths.get(
                "src/test/java/gov/hhs/onc/leap/ces/v2/orchestration/model/fixtures/sample-v2-message.txt")),
                "UTF-8");
        String sampleV2MessageResult = new String(Files.readAllBytes(Paths.get(
                "src/test/java/gov/hhs/onc/leap/ces/v2/orchestration/model/fixtures/sample-v2-message-results.txt")),
                "UTF-8");
        String slsDetailResponse = new String(Files.readAllBytes(Paths.get(
                "src/test/java/gov/hhs/onc/leap/ces/v2/orchestration/model/fixtures/SLSDetailResults.txt")),
                "UTF-8");

        try {
            Pattern pattern = Pattern.compile("matched code [\\'](.+)[\\']");
            Matcher matcher = pattern.matcher(slsDetailResponse);
            while (!matcher.hitEnd()) {
                matcher.find();
                matchedCodeList.add(matcher.group(1));
            }
        }
        catch (Exception ex) {
            //reached end of pattern match
        }

        Iterator iter = matchedCodeList.iterator();
        String resultString = sampleV2Message;
        while (iter.hasNext()) {
            String lineToRemove = (String)iter.next();
            System.out.println("Line To Remove "+lineToRemove);
            resultString = Stream.of(resultString.split("\n"))
                    .filter(s -> !s.contains(lineToRemove))
                    .collect(Collectors.joining("\n"));
        }

        System.out.println(resultString);

        assert(resultString.equals(sampleV2MessageResult));

    }
}
