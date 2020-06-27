### LEAP Consent Enforcement Service (CES) eHealth Exchange Demonstration - Embedded

What is eHealth Exchange?
Active in all 50 states, the eHealth Exchange is the largest query-based, health information network in the country. It is the principal network that connects federal agencies and non-federal organizations, allowing them to work together to improve patient care and public health.

Currently 61 regional and/or state Health Information Exchanges (HIE) participate in eHealth Exchange convering over 120 Million patients.

For additional information on specifications used within these demonstrations refer to https://ehealthexchange.org

#### **LEAP eHealth Exchange Demonstration Environment**

The LEAP team has implemented a CONNECT 5.4 Gateway and Adapters on the Google cloud to demostrate how LEAP CDS authorizations are implemented within DocumentQuery, DocumentRetrieve, and DocumentSubmission exchange flows.  These demonstrations are for informational purposes only, and do not attempt to dicate design decisions within your exhange enviroment.

![eHealth Exchange Test Enviroment](../docs/assets/eHealthExchangeDemoEnv.png?raw=true)

#### Prerequisites
http://104.196.250.115:8080/Gateway/DocumentSubmission/1_1/EntityService/EntityDocSubmissionUnsecured
- [OpenJDK](https://openjdk.java.net/) 11.0.6_10 or newer
- [Maven](https://maven.apache.org/) 3.6.x or newer
- CONNECT-Solution 5.4, Aurion 5.x, or proprietary systems, that implement the eHealth Exchange specifications.
- [Soap-UI](https://www.soapui.org/downloads/soapui/) 5.5 or newer

## Enviroment setup
- Setup JAVA_HOME environment variable pointing to the location of the JDK’s installation directory.
- Setup M2_HOME environment variable pointing to the location of the Maven’s installation directory.  
- Make sure your Maven is configured to fetch dependencies from Github packages as discussed [below](#enable-github-packages-for-maven)  


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

##### **Add missing libraries**
The project needs two libraries `FileUtils` and `MySQL-connector-java` that are included on this path `<repo directory>/test-scripts/ccda-orchestration-embedded/soap-ui-project/lib/`
To include them on SoapUI is necessary to close the tool and paste them on `<SoapUI directory>/lib` for `*NIX` environments or on `/Applications/SoapUI-x.x.x.app/Contents/java/app/lib` on Mac environments.
 
 

##### **Configure WSDL Locations**
Schema compliance tests require the absolute path to the WSDL.  
This seems to be a bug within the community edition of Soap-UI where a placeholder can not be used on `Schema Compliance`.

###### **Workarounds to configure WSDL locations**
There are two ways to resolve this issue
1) Enable a local SOAP Mock Server
2) Add manually the absolute path to WSDLs files. 

###### 1. Enable the local SOAP Mock Server
The Server uses by default the port 8282, if it is not being used in your local just right click over`MockSoapServer` and press on`Start Minimized`.

###### 2. Add absolute paths to WSDL files
If the server explained in step above did not work, please follow next instructions
Open each test by double click on it.  Depending on which test, double click on the step, Document Query, or Document Retrieve, or Doc Submission.  This will open the script window for that test.  Click on `Assertions` add double click on the `Schema Compliance` assertion.  Enter value based on list below this.

Document Query -> `<project directory>`/test-scripts/ccda-orchestration-embedded/soap-ui-project/wsdl/EntityXDR.wsdl

Document Retrieve -> `<project directory>`/test-scripts/ccda-orchestration-embedded/soap-ui-project/wsdl/EntityDocRetrieve.wsdl

Doc Submission -> `<project directory>`/test-scripts/ccda-orchestration-embedded/soap-ui-project/wsdl/EntityXDR.wsdl

![SchemaCompliance](../docs/assets/schemacompliance.png?raw=true)
#### **Execute Demonstration**

In Soap-UI under the test suite `LEAP CDS CES Integration Testing` double click on each test case and run by clicking on the `green` play button.  Run this in the listed order as there are dependencies.

Expected test run results are shown below.

![Soap-UI Results](../docs/assets/ccdatestingresults.png?raw=true)

#### **Implementation Guidance**
(Todo)

##### **Build Project**

##### DocumentQuery Example

##### DocumentRetrieve Example

##### DocumentSubmission Example


