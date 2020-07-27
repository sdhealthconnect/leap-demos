# HL7® FHIR® Connectathon Sept 2020 Consent Management and Enforcement Services Track
This project hosts the technical requirements and test artifacts for the Consent Management and Enforcement Services Track of the HL7® FHIR® Connectathon in September 2020.  

## Goal
This track will focus on using FHIR consent in a number of key use cases including but not limited the use of a Consent Decision Service for use case around privacy, consent for treatment, and participation is research.  In addition aspects of Consent Enforcement, Sharing, and discovery will be explored.

This track leverages the May 2020  Security Labeling Track which addressed FHIR® Data Segmentation for Privacy Implementation Guide(DS4P IG) requirements.

##Requirements
The participants are expected to provide one or many of the following, FHIR Clients, Consent Decisioning Service, Consent Enforcement Service, or Security Labeling Services that meets the requirements of the track. The following are the minimum requirements:

### FHIR® Client Requirements
- Interface to Register and Manage Patient resource
- Manages organization or practioner resources
- Creates, updates, or revokes FHIR® R4 Consent resources
- (Bonus) Allows for multiple Purpose of Uses, Policies, and Obligations

### Consent Decision Service (CDS)
- Returns authorization decision and any obligations as a result of evaluating the patient Consent resource.

### Consent Enforcement Service (CES)
- Forms authorization request and communicates with CDS
- Consumes decision from CDS
- Acts on Obligations if any
- Orchestrates interactions with external services such as Security Lableling

### Security Labeling Services (SLS)
As described in May 2020 Connectathon, SLS will enforce the 1..1 cardinality constraint for confidentiality labels by either:

-  Rejecting a labeled resource that does not bear a confidentiality label, or
Labeling the resource after accepting it and assigning a confidentiality label to it.
- Capable of adding high-watermark confidentiality label to a response bundle based on the contents of the bundle.

- (Bonus) Supporting high-watermark on the response bundle for other types security labels (other than confidentiality).

## Implementation Examples
Consent Decision Service - https://github.com/sdhealthconnect/leap-cds

Consent Decision Service Clients - https://github.com/sdhealthconnect/leap-ces-java-clients

Consent Enforcement V2 Messaging - https://github.com/sdhealthconnect/leap-demos/tree/master/leap-ces-v2-orchestration

Consent Enforcement eHealth Exchange (Generic) - https://github.com/sdhealthconnect/leap-demos/tree/master/leap-ces-ccda-orchestration

Consent Enforcement eHealth Exchange (Embedded) - https://github.com/sdhealthconnect/leap-demos/tree/master/leap-ces-ccda-orchestration-embedded

Consent Enforcement FHIR Based Exchange (Proxy) - https://github.com/sdhealthconnect/leap-fhir-ces

Consent Enforcement FHIR Based Exchange (Embedded) - https://github.com/sdhealthconnect/leap-hapi-fhir-ces-embedded

Additional information can be found at https://sdhealthconnect.github.io/leap/

## Available Test Servers and Demonstrations

Consent Decision Services - https://sdhc-leap.appspot.com

HAPI-FHIR Consent Repository - http://34.94.253.50:8080/hapi-fhir-jpaserver/fhir

eHealth Exchange Server (Embedded) - http://104.196.250.115:8080

eHealth Exchange Services (Generic) - tbd

Directed Exchange Services - tdb

V2 Exchange Services - tbd









