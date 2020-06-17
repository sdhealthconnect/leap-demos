package gov.hhs.onc.leap.ces.utils;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;

import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.extern.slf4j.Slf4j;
import oasis.names.tc.ebxml_regrep.xsd.rim._3.*;


import javax.xml.bind.JAXBElement;

public class AssertionTypeUtilities {
    private static final Logger log = java.util.logging.Logger.getLogger(AssertionTypeUtilities.class.getName());

    public static final String DS_RESPONSE_BASIC_ERROR_MESSAGE = "Unable to update server with DocumentReference";

    public static final String XDS_SUBMISSIONSET_PATIENT_ID = "XDSSubmissionSet.patientId";
    public static final String XDS_DOCUMENT_SOURCE_PID = "sourcePatientId";
    public static final String XDS_DOCUMENT_SOURCE_PATIENT_INFO = "sourcePatientInfo";
    public static final String XDS_DOCUMENT_PATIENT_ID = "XDSDocumentEntry.patientId";
    public static final String XDS_PATIENT_INFO_NAME_PREFIX = "PID-5|";
    public static final String XDS_PATIENT_INFO_DOB_PREFIX = "PID-7|";
    public static final String XDS_PATIENT_INFO_GENDER_PREFIX = "PID-8|";
    public static final String XDS_PATIENT_INFO_ADDRESS_PREFIX = "PID-11|";

    public static final String PARAMS_FAMILY_PART_TYPE = "FAM";
    public static final String PARAMS_GIVEN_PART_TYPE = "GIV";
    public static final String SUBJECT_NAME_SEMANTICS = "LivingSubject.name";
    public static final String SUBJECT_GENDER_SEMANTICS = "LivingSubject.administrativeGender";
    public static final String SUBJECT_DOB_SEMANTICS = "LivingSubject.birthTime";

    public static final String DS_RESPONSE_STATUS_SUCCESS = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success";
    public static final String DS_RESPONSE_STATUS_FAILURE = "urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Failure";
    public static final String DS_RESPONSE_ERROR_SEVERITY = "urn:oasis:names:tc:ebxml-regrep:ErrorSeverityType:Error";

    public static final String XDR_EC_XDSMissingDocument = "XDSMissingDocument";
    public static final String XDR_EC_XDSMissingDocumentMetadata = "XDSMissingDocumentMetadata";
    public static final String XDR_EC_XDSNonIdenticalHash = "XDSNonIdenticalHash";
    public static final String XDR_EC_XDSRegistryDuplicateUniqueIdInMessage = "XDSRegistryDuplicateUniqueIdInMessage";
    public static final String XDR_EC_XDSRegistryBusy = "XDSRegistryBusy";
    public static final String XDR_EC_XDSRegistryMetadataError = "XDSRegistryMetadataError";
    public static final String XDR_EC_XDSUnknownPatientId = "XDSUnknownPatientId";
    public static final String XDR_EC_XDSPatientIdDoesNotMatch = "XDSPatientIdDoesNotMatch";
    public static final String XDR_EC_XDSRegistryError = "XDSRegistryError";

    public static final String XDS_DOCUMENT_CLASSIFICATION_SCHEME_TYPE_CODE = "urn:uuid:f0306f51-975f-434e-a61c-c59651d33983";
    public static final String XDS_DOCUMENT_CLASSIFICATION_SCHEME_CLASS_CODE = "urn:uuid:41a5887f-8865-4c09-adf7-e362475b143a";
    public static String XDS_EXTERNAL_DOCUMENT_IDENTIFICATION_SCHEME = "urn:uuid:2e82c1f6-a085-4c72-9da3-8640a32e42ab";
    public static final String XDS_DOCUMENT_CLASSIFICATION_SCHEME_AUTHOR = "urn:uuid:93606bcf-9494-43ec-9b4e-a7748d1a838d";
    public static final String CACCD_LOINC_TYPE_CODE = "";
    public static final String XDS_DOCUMENT_CREATION_TIME = "creationTime";
    public static final String XDS_DOCUMENT_AUTHOR_PERSON = "authorPerson";

    public static final String SEPARATOR = " ";

    public static final String XDS_CLASSIFICATION_FACILITY_TYPE = "urn:uuid:f33fb8ac-18af-42cc-ae0e-ed0b0bdb91e1";
    public static final String XDS_CLASSIFICATION_EVENT_CODE = "urn:uuid:2c6b8cb7-8b2a-4051-b291-b1ae6a575ef4";
    public static final String XDS_DOCUMENT_SERVICE_START_TIME = "serviceStartTime";
    public static final String XDS_DOCUMENT_SERVICE_STOP_TIME = "serviceStopTime";
    public static final String XDS_DOCUMENT_SIZE = "size";
    public static final String XDS_DOCUMENT_LANGUAGE = "languageCode";

    private String leapResourceId = "";
    private String leapOrganizationId = "";
    private String leapSubjectId = "";
    private String leapHomeCommunityId = "";
    private String leapOrganizationName = "";

    public AssertionTypeUtilities() {

    }

    public String getResourceIdFromAssertion(AssertionType assertion) {
        String res = "UNKNOWN-RESOURCE-ID";
        try {
            String id = assertion.getSamlAuthzDecisionStatement().getResource();
            if (id.indexOf("null") > -1) {
                res = leapResourceId;
            }
            else {
                res = getUniquePatientIdentifier(id);
            }
        }
        catch (Exception ex) {
            log.log(Level.WARNING,"UniquePatientId is Null Checking leap Resource", ex);
        }
        return res;
    }

    public String getOrganizationIdFromAssertion(AssertionType assertion) {
        String res = "UNKNOWN-ORGANIZATION-ID";
        try {
            String name = assertion.getUserInfo().getOrg().getName();
            if (name.indexOf("null") > -1) {
                res = leapOrganizationId;
            }
            else {
                res = name;
            }
        }
        catch (Exception ex) {
            log.log(Level.WARNING,"UNKNOWN-ORGANIZATION-ID", ex);
        }
        return res;
    }

    public String getSubjectIdFromAssertion(AssertionType assertion) {
        String res = "UNKNOWN-SUBJECT-ID";
        try {
            String id = assertion.getUserInfo().getUserName();
            if (id.indexOf("CN=") > -1) {
                res = leapSubjectId;
            }
            else {
                res = id;
            }
        }
        catch (Exception ex) {
            log.log(Level.WARNING,"UNKNOWN-SUBJECT-ID", ex);
        }
        return res;
    }

    public String extractPurpose(AssertionType assertion) {
        String purpose = null;
        if (assertion != null && assertion.getPurposeOfDisclosureCoded() != null
                && assertion.getPurposeOfDisclosureCoded().getCode() != null) {
            purpose = assertion.getPurposeOfDisclosureCoded().getCode();
        }

        return purpose;
    }


    public String getHomeCommunityIdFromAssertion(AssertionType assertion) {
        String res = "UNKNOWN-HCID";
        try {
            String hc = assertion.getHomeCommunity().getHomeCommunityId();
            if (hc.indexOf("null") > -1) {
                res = leapHomeCommunityId;
            }
            else {
                res = fixSubjectLocality(hc);
            }
        }
        catch (Exception ex) {
            log.log(Level.WARNING,"Error in obtaining HomeCommunityId from assertion", ex);
        }
        return res;
    }

    public String getHomeCommunityNameFromAssertion(AssertionType assertion) {
        String res = "UNKNOWN-HCNAME";
        try {
            String hc = assertion.getUserInfo().getOrg().getName();
            if (hc.indexOf("null") > -1) {
                res = leapOrganizationName;
            }
            else {
                res = hc;
            }
        }
        catch (Exception ex) {
            log.log(Level.WARNING,"Org Name is Null Checking leap Message", ex);
            //this may always through an exception on outbound request..
            res = leapOrganizationName;
        }
        return res;
    }

    private String fixSubjectLocality(String subjectlocality) {
        if (subjectlocality.indexOf("urn:oid:") < 0) {
            subjectlocality = "urn:oid:"+subjectlocality;
        }
        return subjectlocality;
    }

    public String getUniquePatientIdentifier(String nhinpatient) {
        String res = "";
        try {
            StringTokenizer st = new StringTokenizer(nhinpatient, "^");
            res = st.nextToken();
        }
        catch (Exception ex) {
            log.log(Level.WARNING,"Error in Processing uniguePatientId",ex);
        }
        return res;
    }

    public String extractPatientId(RegistryObjectListType registryList) throws Exception {
        String patientId = null;
        RegistryPackageType regPackage = extractRegistryPackage(registryList);

        if (regPackage != null && regPackage.getSlot() != null && regPackage.getSlot().size() > 0) {
            patientId = getPatientIdFromExternalIdentifiers(regPackage.getExternalIdentifier(),
                    XDS_SUBMISSIONSET_PATIENT_ID);
        }

        return patientId;
    }
    public RegistryPackageType extractRegistryPackage(RegistryObjectListType registryList)
            throws Exception {
        RegistryPackageType regPackage = null;
        if (registryList != null && registryList.getIdentifiable() != null && registryList.getIdentifiable().size() > 0) {
            List<JAXBElement<? extends IdentifiableType>> identifiers = registryList.getIdentifiable();
            for (JAXBElement<? extends IdentifiableType> object : identifiers) {
                if (object.getValue() != null && object.getValue() instanceof RegistryPackageType) {
                    regPackage = (RegistryPackageType) object.getValue();
                    break;
                }
            }
        } else {
            log.log(Level.WARNING, "Unable to read Registry Package in request.");
            throw new Exception("Unable to read Registry Package in request.");
        }

        return regPackage;
    }

    String getPatientIdFromExternalIdentifiers(List<ExternalIdentifierType> externalIdentifiers, String name) {
        String patientId = null;
        for (ExternalIdentifierType identifier : externalIdentifiers) {
            if (identifier.getName() != null
                    && identifier.getName().getLocalizedString() != null
                    && identifier.getName().getLocalizedString().size() > 0
                    && identifier.getName().getLocalizedString().get(0) != null
                    && identifier.getName().getLocalizedString().get(0).getValue()
                    .equals(XDS_DOCUMENT_SOURCE_PID)) {
                patientId = identifier.getValue();
                break;
            }

        }
        return patientId;
    }

    public String extractSourcePatientId(RegistryObjectListType registryList) {
        String patientId = null;

        ExtrinsicObjectType extrinsicObj = extractFirstExtrinsicObject(registryList);

        if (extrinsicObj != null) {
            ValueListType valueList = getValueListFromSlot(extrinsicObj.getSlot(),
                    XDS_DOCUMENT_SOURCE_PID);
            if (valueList != null && valueList.getValue() != null && valueList.getValue().size() > 0) {
                patientId = valueList.getValue().get(0);
            }
        }
        return patientId;
    }

    private ValueListType getValueListFromSlot(List<SlotType1> slots, String name) {
        ValueListType valueList = null;
        for (SlotType1 slot : slots) {
            if (slot.getName() != null && slot.getName().equals(name)) {
                if (slot.getValueList() != null && slot.getValueList().getValue() != null
                        && slot.getValueList().getValue().size() > 0) {
                    valueList = slot.getValueList();
                    break;
                }
            }
        }
        return valueList;
    }

    private ExtrinsicObjectType extractFirstExtrinsicObject(RegistryObjectListType registryList) {
        ExtrinsicObjectType extrinsicObj = null;
        if (registryList != null && registryList.getIdentifiable() != null && registryList.getIdentifiable().size() > 0) {
            List<JAXBElement<? extends IdentifiableType>> identifiers = registryList.getIdentifiable();
            for (JAXBElement<? extends IdentifiableType> object : identifiers) {
                if (object.getValue() != null && object.getValue() instanceof ExtrinsicObjectType) {
                    extrinsicObj = (ExtrinsicObjectType) object.getValue();
                }
            }
        }
        return extrinsicObj;
    }

}
