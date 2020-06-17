### LEAP Consent Enforcement Service (CES) eHealth Exchange Demonstration - Embedded

What is eHealth Exchange?
Active in all 50 states, the eHealth Exchange is the largest query-based, health information network in the country. It is the principal network that connects federal agencies and non-federal organizations, allowing them to work together to improve patient care and public health.

Currently 61 regional and/or state Health Information Exchanges (HIE) participate in eHealth Exchange convering over 120 Million patients.

For additional information on specifications used within these demonstrations refer to https://ehealthexchange.org

#### **LEAP eHealth Exchange Demonstration Environment**

The LEAP team has implemented a CONNECT 5.4 Gateway and Adapters on the Google cloud to demostrate how LEAP CDS authorizations are implemented within DocumentQuery, DocumentRetrieve, and DocumentSubmission exchange flows.  These demonstrations are for informational purposes only, and do not attempt to dicate design decisions within your exhange enviroment.

![eHealth Exchange Test Enviroment](../docs/assets/eHealthExchangeDemoEnv.png?raw=true)

#### Prerequisites

- OpenJDK 11.0.6_10

- Maven 3.6.x -
Make sure your Maven is configured to fetch dependencies from Github packages as discussed below

- CONNECT-Solution 5.4, Aurion 5.x, or proprietary systems, that implement the eHealth Exchange specifications.

- Soap-UI 5.5 or greater

#### **Demonstration Setup**

-  Clone this repository:
```
> git clone https://github.com/sdhealthconnect/leap-demos.git
> cd leap-demo
```

- Import Project Into Soap-UI

The project is located in the following directory;

```
<repo directory>/test-scripts/ccda-orchestration-embedded/soap-ui-project
```
Filename: leap-ccda-demo-soapui-project.xml

You will need to change the `GatewayPropDir` for the LEAPCCDADemonstration project, those properties files is located
in your `<repo directory>/test-scripts/ccda-orchestration-embedded/nhin-properties` directory.

![Soap-UI Setup](../docs/assets/soapuiconfig.png?raw=true)


#### **Execute Demonstration**

In Soap-UI under the test suite `LEAP CDS CES Integration Testing` double click on each test case and run by clicking on the `green` play button.  Run this in the listed order as there are dependencies.

Expected test run results are show below.

![Soap-UI Results](../docs/assets/ccdatestingresults.png?raw=true)

#### **Implementation Examples**

##### DocumentQuery Example

##### DocumentRetrieve Example

##### DocumentSubmission Example


