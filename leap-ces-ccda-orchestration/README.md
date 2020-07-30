# LEAP Consent Enforcement Services (CES) for CCDA Processing 

This project implements RESTful services to orchestrated interactions during 'Generic' eHealth and Directed 
Exchange implementations where the health record is in Consolidated-CDA form.  These services are publicly available at;

https://sdhc-leap-ccda-axz2tb4tma-uc.a.run.app/

API Documentation can be found at; 

https://sdhc-leap-ccda-axz2tb4tma-uc.a.run.app/swagger-ui.html


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
 

