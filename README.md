
# Consent Enforcement Service (CES) Demonstration Environment
![Build Status](https://github.com/sdhealthconnect/leap-demos/workflows/Build%20Status/badge.svg)

This repository contains the artifacts for demonstrating integration with the [LEAP Consent Decision Service (CDS)](https://github.com/sdhealthconnect/leap-cds) to enforce patient consent in different exchange use-cases. Currently, the following demos are available:

- HL7 v2 Exchange

## Prerequisites
- OpenJDK 11.0.6_10
- Maven 3.6.x
- Make sure your Maven is configured to fetch dependencies from Github packages as discussed [below](#enable-github-packages-for-maven)
- Docker 19.03.5 and Docker Compose 1.25.2 


## Build Instructions
-  Clone this repository and change to the repository directory:
```
> git clone https://github.com/sdhealthconnect/leap-ces.git
> cd leap-ces
```
- Review the environment variable file `docker-env.sh` and modify if needed:
```
#!/bin/sh

export HAPI_DB_URL='jdbc:mysql://hapi-fhir-mysql:3306/hapi?serverTimezone=UTC&max_allowed_packet=16777216&createDatabaseIfNotExist=true'
export HAPI_DB_USER=admin
export HAPI_DB_PASS=admin

export LEAP_DB_URL='jdbc:mysql://leap-mysql:3306/leap?serverTimezone=UTC&max_allowed_packet=16777216&createDatabaseIfNotExist=true'
export LEAP_DB_USER=admin
export LEAP_DB_PASS=admin


export HAPI_FHIR_URL_PUBLIC='http://host.docker.internal:6060/hapi-fhir-jpaserver/'
export HAPI_FHIR_URL='http://hapi-fhir-jpaserver:6060/hapi-fhir-jpaserver/fhir/'

export CDS_HOST_URL='https://sdhc-leap.appspot.com'
export SLS_HOST_URL='http://leap-sls-service:9091'


export LEAP_LOG_LEVEL='WARN'
```

- Build the project using `maven`:
```
> mvn clean install -DskipTests
```
- Build the containers using `docker-compose`:
```
> docker-compose build
```

## Run Instructions

- Source enviroment you previously defined
```
> . docker-env.sh
```
- Launch Docker Containers
```
> docker-compose up
```
If you are running Docker locally you can visually validate that all have started by launching the Dashboard.  
![Docker Dashboard](docs/assets/dockerdashboard.png?raw=true)

You can also check this via the command line:
```
> docker stats
```

## The `HL7v2` Demo
- Launch the Swagger UI at `http://localhost:9092/swagger-ui.html` and click on the "Try It Out" button.
- Select "text/plain" in Request Body type drop-down list
- Copy the contents of `./test-script/message-artifacts/v2alcoholabuse.txt` in the Request Body.
- Click on Execute button.

The following shows a sample output:
![Swagger-UI](docs/assets/swaggerinterface.png?raw=true)

In your Docker-Compose terminal session you should see the following response
![Test Response](docs/assets/testoutput.png?raw=true)

Note:  If you wish to run this demo multiple times with this message, keep in mind that the message ID should be changed to a unique value every time. This is the _third_ last field in the HL7v2 message string. For example, change the following message 
```
MSH|^~\&|SendingApp^‹OID›^ISO|SendingFac^‹OID›^ISO|ReceivingApp^‹OID›^ISO|ReceivingFac^2.16.840.1.113883.20.5^ISO|2007509101832133||ADT^A08^ADT_A01|20075091019450028|D|2.5
```
to
```
MSH|^~\&|SendingApp^‹OID›^ISO|SendingFac^‹OID›^ISO|ReceivingApp^‹OID›^ISO|ReceivingFac^2.16.840.1.113883.20.5^ISO|2007509101832133||ADT^A08^ADT_A01|20075091019450029|D|2.5
```

## Enable Github Packages for Maven
In order to build this project, your Maven should be configured to fetch the [`leap-ces-java-clients`](https://github.com/sdhealthconnect/leap-ces-java-clients/packages) dependency from Github packages. Follow the steps below to configure your Maven to use Github packages. For further details refer to the corresponding [Github documentation](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages).

- Create a personal access token; go to _Settings_->_Developer Settings_->_Personal access tokens_ and click _Generate new token_. Make sure to choose the `read:packages` scope. Copy the generated token.

- Add the following to your Maven settings file located at `~/.m2/settings.xml` (you may need to create this file if it does not exist). Replace `USERNAME` with your Github ID and `TOKEN` with the token generated in the previous step. Note that the `id` for the `server` must be set to `github`.
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <servers>
    <server>
      <id>github</id>
      <username>USERNAME</username>
      <password>TOKEN</password>
    </server>
  </servers>
</settings>
```
