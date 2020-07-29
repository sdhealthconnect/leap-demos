# Virtual HL7® FHIR® Connectathon Sept 2020 Consent Management and Enforcement Services Track
This project hosts the technical requirements and test artifacts for the Consent Management and Enforcement Services Track of the HL7® FHIR® Connectathon in September 2020.  

## Goal
This track will focus on using FHIR consent in a number of key use cases including but not limited the use of a Consent Decision Service for use case around privacy, consent for treatment, and participation is research.  In addition aspects of Consent Enforcement, Sharing, and discovery will be explored.

This track leverages the May 2020  Security Labeling Track which addressed FHIR® Data Segmentation for Privacy Implementation Guide(DS4P IG) requirements.

## Registration
Registration for the September Virtual HL7 FHIR® Connectathon is now open! Early-bird rates are currently $150 for members and $250 for non-members and will end on August 21, 2020. After August 21st the rates will increase to $200 for members and $300 for non-members.

Registration for this connectathon can be found at; https://www.hl7.org/events/fhir/connectathon/2020/09/

## Requirements
Participants are expected to provide one or many of the following, FHIR Consent Clients, Consent Decisioning Service, Consent Enforcement Service, or Security Labeling Services that meets the requirements of the track. 
Update the following Google Sheet with information about yourself and the implementation you will be demonstrating @ https://docs.google.com/spreadsheets/d/1q4HL1Jt3GXHTnoWRekKDsBdoicnMbBUom7J1B7WSTbI/edit?usp=sharing.

#### FHIR® Consent Client Requirements
- Interface to Register and Manage Patient Account and Patient resource
- Manages organization or practioner resources
- Creates, updates, or revokes FHIR® R4 Consent resources
- (Bonus) Allows for multiple Purpose of Uses, Policies, and Obligations

Update the track Google Sheet with information about your Consent Client and its capabilities
![Consent Client Info](./resources/images/ConsentClient.png?raw=true)

#### Consent Decision Service (CDS)
- Returns authorization decision and any obligations as a result of evaluating the patient Consent resource.

Update the track Google Sheet with information about your Consent Decision Service and its capabilities
![Consent Decision Server](./resources/images/CDS.png?raw=true)

#### Consent Enforcement Service (CES)
- Forms authorization request and communicates with CDS
- Consumes decision from CDS
- Acts on Obligations if any
- Orchestrates interactions with external services such as Security Lableling

Update the track Google Sheet with information about your Consent Enforcement Service and its capabilities
![Consent Enforcement Service](./resources/images/CES.png?raw=true)

#### Security Labeling Services (SLS)
As described in May 2020 Connectathon, SLS will enforce the 1..1 cardinality constraint for confidentiality labels by either:

-  Rejecting a labeled resource that does not bear a confidentiality label, or
Labeling the resource after accepting it and assigning a confidentiality label to it.
- Capable of adding high-watermark confidentiality label to a response bundle based on the contents of the bundle.

- (Bonus) Supporting high-watermark on the response bundle for other types security labels (other than confidentiality).

Update the track Google Sheet with information about your Security Labeling Service and it's capabilities
![Security Labeling Service](./resources/images/SLS.png?raw=true)

## Implementation Examples
Consent Decision Service - https://github.com/sdhealthconnect/leap-cds

Consent Decision Service Clients - https://github.com/sdhealthconnect/leap-ces-java-clients

Consent Enforcement V2 Messaging - https://github.com/sdhealthconnect/leap-demos/tree/master/leap-ces-v2-orchestration

Consent Enforcement eHealth and Directed Exchange (Generic) - https://github.com/sdhealthconnect/leap-demos/tree/master/leap-ces-ccda-orchestration

Consent Enforcement eHealth Exchange (Embedded) - https://github.com/sdhealthconnect/leap-demos/tree/master/leap-ces-ccda-orchestration-embedded

Consent Enforcement FHIR Based Exchange (Proxy) - https://github.com/sdhealthconnect/leap-fhir-ces

Consent Enforcement FHIR Based Exchange (Embedded) - https://github.com/sdhealthconnect/leap-hapi-fhir-ces-embedded

Additional information on the can be found at https://sdhealthconnect.github.io/leap/

## Available Test Servers

Consent Decision Services - https://sdhc-leap.appspot.com

HAPI-FHIR Consent Repository - http://34.94.253.50:8080/hapi-fhir-jpaserver/fhir

eHealth Exchange Server (Embedded) - http://104.196.250.115:8080

eHealth Exchange Services - https://sdhc-leap-ccda-axz2tb4tma-uc.a.run.app

Directed Exchange Services - https://sdhc-leap-ccda-axz2tb4tma-uc.a.run.app

V2 Exchange Services - https://sdhc-leap-v2-56zrsoidxa-uc.a.run.app

FHIR Exchange (Embedded) - http://35.235.74.117:8080/hapi-fhir-jpaserver/fhir

FHIR Exchange (Proxy) - https://leap-fhir-ces.appspot.com

## Additional Resources
Connectathon examples are available by cloning the LEAP Demos repository
```
> git clone https://github.com/sdhealthconnect/leap-demos.git
> cd leap-demos/hl7-fhir-connectathon-sept2020-consent-track/examples
```

### Use Case - Privacy Consent

### Use Case - Informed Consent

### Use Case - Research

### Use Case - Advanced Directive






