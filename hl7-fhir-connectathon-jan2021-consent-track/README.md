# Virtual HL7® FHIR® Connectathon January 2021 Consent Management and Enforcement Services Track
This project hosts the technical requirements and test artifacts for the Consent Management and Enforcement Services Track of the HL7® FHIR® Connectathon in January.  

## Goal
This track is a follow-on to previous September 2020 connectathon and again will focus on using FHIR consent in a number of key use cases including but not limited the use of a Consent Decision Service for use case around privacy, consent for treatment, and participation is research.  In addition aspects of Consent Enforcement, Sharing, and discovery will be explored.

## Registration
Registration for the January Virtual HL7 FHIR® Connectathon is now open! Early-bird rates are currently $150 for members and $250 for non-members and will end on December 30th, 2020. After December 30th the rates will increase to $250 for members and $375 for non-members.

Registration for this connectathon can be found at; https://www.hl7.org/events/fhir-connectathon/index.cfm?ref=nav

For additional details about the 26th HL7 FHIR Connectathon, visit  https://confluence.hl7.org/display/FHIR/2021-01+Connectathon+26

## Track Planning Meetings
Those who are interested in participating in the Consent Management and Enforcement Services Track are welcome to attend 3 planning prior to the January Connectathon.

Dates/Times
- December 2, 2020 4:00pm-5:00pm EST
- December 9, 2020 4:00pm-5:00pm EST
- December 16, 2020 4:00pm-5:00pm EST

Meeting Link Info:
- TBD

Planning Agenda Items:
- Review Objectives, Refine Uses Cases, Reviews Test Messages and Scripts, Review Implementation Examples
- Timing, determine track connectathon hours based on where participants are globally.
- other topics


## Agenda
Please login to your Whova account for additional information on this track.  

Day 1 January 13th
- TBD

Day 2 - January 14th
- TBD

Day 3 - January 15th
- TBD
 
## Requirements
Participants are expected to provide one or many of the following, FHIR Consent Clients, Consent Decisioning Service, Consent Enforcement Service, or Security Labeling Services that meets the requirements of the track. 
Update the following Google Sheet with information about yourself and the implementation you will be demonstrating @ https://docs.google.com/spreadsheets/d/1Vw3BIwd25ZRzU74ELboNyVvnJYO1pm-bsE2_4WDNEAk/edit?usp=sharing.

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
> cd leap-demos/hl7-fhir-connectathon-jan2020-consent-track/examples
```

### Consent Decision Testing
You can test your patients FHIR consent against the LEAP CDS at http://34.94.253.50:9095/swagger-ui.html.  This assumes
that you have loaded the Patient, Consent, Organization, and/or Pracitioner resource are loaded in the consent repository 
at http://34.94.253.50:8080/hapi-fhir-jpaserver/fhir for LEAP CDS to act on them.

![Consent Testing Service](./resources/images/fhirconsenttesting.png?raw=true)


### Privacy Consent Scenarios
Assuming a **Purpose of Use** of **"TREAT"**;

a) Patient "A" wishes to opt-in to SDHealthConnect.org HIE with No restrictions

b) Patient "B" wishes to opt-in to SDHealthConnect.org HIE but wishes to restrict access to their sensitive information.

c) Patient "C" wishes to opt-out of SDHealthConnect.org HIE.

d) Patient "D" wishes to grant access to "Dr. Bob" at Kaiser Permanente with no restrictions.

e) Patient "E" wishes to grant access to "Dr. Bob" at Kaiser Permanente by with to restrict access to their sensitive information.

f) Patient "F" wishes to grant access to "Dr. Bob" at Kaiser Permanente with no restrictions for only one day.

g) Patient "G" wishes to deny access to "Dr. Bob" at Kaiser Permanente.

**Objectives:**

Consent Client:  Demonstrate the ability to create, update, retrieve, and revoke the FHIR consent.

Consent Decision Service: Demonstrate the ability to return authorization and (if any) obligations from above FHIR consents.

Consent Enforcement Service: Demonstrate the ability to enforce the authorization decision and any obligations, such as redaction
of sensitive PHI. 

**Bonus** - Demonstrate enforcement when exchange of patients healthcare information is based on V2, eHx, or Direct

**Bonus** - Demonstrate how Organization Policy may sometimes override constraints levied with the patient's consent.

**Bonus** - Demonstrate Emergency Access when Purpose of Use is **ETREAT**

**Bonus** - Demonstrate Consent Revocation

### Informed Consent Scenario
Assumes a **Purpose of Use** of **"TREAT"**

a) Patient "H" is having minor surgery on his left shoulder.  "Dr. Bob" will be the attending orthopedic surgeon. "Dr. Bob" reviews
with Patient "H" the planned procedure, any risks, and other expectations such as recovery.

**Objective:**

Consent Client:  Demostrate the creation and retrieval of FHIR resource for Informed Consent capturing both 
Patient "H" and "Dr. Bob's" signature.

Consent Decision Service:  Demonstrate CDS interactions (authoirzation decision) as part of pre-surgical checklist flow.

### Research Consent Scenario
Assumes a **Purpose of Use** of **"HRESCH"**

a) Patient "I" as has recently recovered from severe case of COVID-19 requiring hospitalization.  "Dr. Dave" at Kaiser Permanente
 suggests that she participate in RESEARCH project at University of Arizona to determine long-term effects of this virus.  She agrees.
 
**Objectives:**

Consent Client: Demonstrate ability to create, update, retrieve, and revoke when information exchange is for research purposes.

Consent Decision Service: Demonstrate the ability to return authorization and (if any) obligations from above FHIR consents.

Consent Enforcement Service: Demonstrate enforcement of RESEARCH Consent authorization decision and any obligations returned from CDS.

**Bonus** Demonstration how Consent Enforcement Service can facilitate De-Identification of patient healthcare record.

### Advanced Directive Scenario
TBD






