# LEAP Consent Enforcement Services (CES) for CCDA Processing 

This project implements RESTful services to orchestrated interactions during 'Generic' eHealth and Directed 
Exchange implementations where the health record is in Consolidated-CDA form.  These services are publicly available at;

https://sdhc-leap-ccda-axz2tb4tma-uc.a.run.app/

API Documentation can be found at; 

https://sdhc-leap-ccda-axz2tb4tma-uc.a.run.app/swagger-ui.html

There are 5 core RESTful operations with 4 providing a CDSHooks or XACML flavor. These are implementation examples 
to provide you with a starting point, your implementation vary significantly but have identical outcomes.

**Authorization Requests**
These methods provide auditing of authorization request and decision.

ccdaorchestration/requestAuthorizationWithCDSHooks - This is layered on top of the CDSHooks java client.

cddaorchestration/requestAuthorizationWithXacml - This is layered on top of the XACML java client.

 **Protecting Document by Enforcing Authorization Decision and Obligations**
 These methods provide full orchestration of Authorization, Security Labeling Service, and document manipulation base on 
 obligations.
 
 ccdaorchestration/processDocumentWithCDSHooks - This provides a CDSHooks implementation of enforcement.
 
 ccdaorchestration/processDocumentWithXacml - This provides a XACML implementation of enforcement.
 
 **Protecting Document by Enforcing Obligations**
 This provides a method where an Organization policy may override patient consent constraints.  Example, 
 and organization may require REDACT of all where confidentiality is "R" such as 42 CFR Part 2 restrictions.
 
 ccdaorchestration/processDocumentWithObligation


## Generic eHx Flows
These examples assume CONNECT-Solution, Aurion, or equivalent 5.x implementations.

**Document Query**

This call to LEAP CCDA CES Orchestration would typically be placed in the DocumentQuery Adapter implementation (AdapterDocQueryImpl).
![Document Query](./src/main/resources/images/GenericDocQuery.png?raw=true)

**Document Retrieve**

This call to LEAP CCDA CES Orchestration would typically be placed in the DocumentRetrieve Adapter implementation (AdapterDocRetrieveImpl).
![Document Retrieve](./src/main/resources/images/GenericDocRetrieve.png?raw=true)

**Document Submit**

This call to LEAP CCDA CES Orchestration would typically be placed in the DocumentSubmission Adapter implementation(AdapterDocSubmissionImp).
![Document Submit](./src/main/resources/images/GenericDocSubmit.png?raw=true)




## Generic Directed Exchange Flows
 

