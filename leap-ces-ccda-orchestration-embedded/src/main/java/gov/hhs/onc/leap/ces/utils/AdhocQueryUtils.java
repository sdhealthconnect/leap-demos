package gov.hhs.onc.leap.ces.utils;

import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import oasis.names.tc.ebxml_regrep.xsd.query._3.AdhocQueryRequest;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.SlotType1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdhocQueryUtils {
    private static final Logger LOG = LoggerFactory.getLogger(AdhocQueryUtils.class);

    public static final String XDS_DOCUMENT_ENTRY_PATIENT_ID = "$XDSDocumentEntryPatientId";

    public static final short PARTICIPANT_PATIENT_OBJ_TYPE_CODE_SYSTEM = 1;
    public static final short PARTICIPANT_PATIENT_OBJ_TYPE_CODE_ROLE = 1;
    public static final String PARTICIPANT_PATIENT_OBJ_ID_TYPE_CODE = "2";
    public static final String PARTICIPANT_PATIENT_OBJ_ID_TYPE_CODE_SYSTEM = "RFC-3881";
    public static final String PARTICIPANT_PATIENT_OBJ_ID_TYPE_DISPLAY_NAME = "Patient Number";
    public static final short PARTICIPANT_QUERY_OBJ_TYPE_CODE_SYSTEM = 2;
    public static final short PARTICIPANT_QUERY_OBJ_TYPE_CODE_ROLE = 24;
    public static final String PARTICIPANT_QUERY_OBJ_ID_TYPE_CODE = "ITI-38";
    public static final String PARTICIPANT_QUERY_OBJ_ID_TYPE_CODE_SYSTEM = "IHE Transactions";
    public static final String PARTICIPANT_QUERY_OBJ_ID_TYPE_DISPLAY_NAME = "Cross Gateway Query";
    public static final String EVENT_ID_CODE = "110112";
    public static final String EVENT_CODE_SYSTEM = "DCM";
    public static final String EVENT_CODE_DISPLAY_REQUESTOR = "Query";
    public static final String EVENT_CODE_DISPLAY_RESPONDER = "Query";
    public static final String EVENT_TYPE_CODE = "ITI-38";
    public static final String EVENT_TYPE_CODE_SYSTEM = "IHE Transactions";
    public static final String EVENT_TYPE_CODE_DISPLAY_NAME = "Cross Gateway Query";
    public static final String EVENT_ACTION_CODE_REQUESTOR = "E";
    public static final String EVENT_ACTION_CODE_RESPONDER = "E";
    public static final String QUERY_ENCODING_TYPE = "QueryEncoding";
    public static final String UTF_8 = "UTF-8";
    public static final String HOME_COMMUNITY_ID = "urn:ihe:iti:xca:2010:homeCommunityId";

    private AdhocQueryRequest request;

    public AdhocQueryUtils() {

    }

    public AdhocQueryUtils(AdhocQueryRequest request) {
        this.request = request;
    }

    public String getPatientIdFromRequest(AdhocQueryRequest request) {
        if (request != null
                && request.getAdhocQuery() != null
                && request.getAdhocQuery().getSlot() != null
                && !request.getAdhocQuery().getSlot().isEmpty()) {
            for (SlotType1 slot : request.getAdhocQuery().getSlot()) {
                if (slot != null && slot.getName().equals(XDS_DOCUMENT_ENTRY_PATIENT_ID)
                        && slot.getValueList() != null && NullChecker.isNotNullish(slot.getValueList().getValue())
                        && NullChecker.isNotNullish(slot.getValueList().getValue().get(0))) {
                    return stripQuotesFromPatientId(slot.getValueList().getValue().get(0));
                }
            }
        } else {
            LOG.error("PatientId doesn't exist in the received AdhocQueryRequest message");
        }
        return null;
    }

    public String getPatientHomeCommunityId(String encodedPatientId) {
        LOG.debug("Parsing community id: " + encodedPatientId);
        String communityId = null;
        if (encodedPatientId != null && encodedPatientId.length() > 0) {
            String workingCommunityId = encodedPatientId;
            workingCommunityId = stripQuotesFromPatientId(workingCommunityId);

            // First remove the first components
            int componentIndex = workingCommunityId.lastIndexOf("^");
            LOG.debug("Index: " + componentIndex);
            if (componentIndex != -1 && workingCommunityId.length() > componentIndex + 1) {
                workingCommunityId = workingCommunityId.substring(componentIndex + 1);
                LOG.debug("Working community id after first components removed: " + workingCommunityId);

                if (workingCommunityId.startsWith("&")) {
                    workingCommunityId = workingCommunityId.substring(1);
                }
                int subComponentIndex = workingCommunityId.indexOf("&");
                if (subComponentIndex != -1) {
                    workingCommunityId = workingCommunityId.substring(0, subComponentIndex);
                }
                communityId = workingCommunityId;
            }
        }
        return communityId;
    }

    public String parsePatientId(String receivedPatientId) {
        LOG.debug("Parsing patient id: " + receivedPatientId);
        String patientId = receivedPatientId;
        if (patientId != null && patientId.length() > 0) {
            patientId = stripQuotesFromPatientId(patientId);
            int componentIndex = patientId.indexOf("^");
            LOG.debug("Index: " + componentIndex);
            if (componentIndex != -1) {
                patientId = patientId.substring(0, componentIndex);
                LOG.debug("Parsed patient id: " + patientId);
            }
        }
        return patientId;
    }

    public String stripQuotesFromPatientId(String patientId) {
        LOG.debug("stripQuotesFromPatientId - Parsing patient id: {}", patientId);

        String sbPatientId = patientId;

        if (patientId != null && patientId.length() > 0) {
            if (patientId.startsWith("'") && patientId.length() > 1) {
                if (patientId.endsWith("'")) {
                    // strip off the ending quote
                    sbPatientId = sbPatientId.substring(0, sbPatientId.length() - 1);
                }

                // strip off the first char quote
                sbPatientId = sbPatientId.substring(1);
            }
        }

        LOG.debug("stripQuotesFromPatientId - Parsed patient id: {}", sbPatientId);
        return sbPatientId;
    }
}
