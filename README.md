
# Consent Enforcement Service (CES) Demonstration Environment
![Build Status](https://github.com/sdhealthconnect/leap-demos/workflows/Build%20Status/badge.svg)

This repository contains the artifacts for demonstrating integration with the [LEAP Consent Decision Service (CDS)](https://github.com/sdhealthconnect/leap-cds) to enforce patient consent in different exchange use-cases. Currently, the following demos are available:

- [HL7 v2 Exchange](#hl7-v2-orchestration-demo)

## Prerequisites
- [OpenJDK](https://openjdk.java.net/) 11.0.6_10 or newer
- [Maven](https://maven.apache.org/) 3.6.x or newer
- [Docker](https://www.docker.com/) 19.03.5 or newer 
- [Docker Compose](https://docs.docker.com/compose/) 1.25.2 or newer

## Enviroment setup
- Setup JAVA_HOME environment variable pointing to the location of the JDK’s installation directory.
- Setup M2_HOME environment variable pointing to the location of the Maven’s installation directory.  
- Make sure your Maven is configured to fetch dependencies from Github packages as discussed [below](#enable-github-packages-for-maven)  


## Build Instructions
-  Clone this repository and change to the repository directory:
```
> git clone https://github.com/sdhealthconnect/leap-demos.git
> cd leap-demos
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
- Remove all exited containers in `docker`
```
> docker ps -a -f status=exited
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
If you are running Docker locally you can validate that all have started using a bash terminal.  

Using the `stats` command:
```
> docker stats
```
or alternatively using the command `ps` that includes information as: `CONTAINER ID, IMAGE, COMMAND, CREATED, STATUS, PORTS` 
```
> docker ps
```
Find below an example of `docker ps` output 
```
CONTAINER ID        IMAGE                               COMMAND                  CREATED             STATUS              PORTS                               NAMES
493afbd3e5ba        ddecouteau/leap-ces-ccda-orchestration   "java -Dspring.profi…"   40 seconds ago      Up 38 seconds       0.0.0.0:9093->9093/tcp              leap-ces-ccda-orchestration
69701e2468e7        ddecouteau/leap-ces-v2-orchestration     "java -Dspring.profi…"   40 seconds ago      Up 37 seconds       0.0.0.0:9092->9092/tcp              leap-ces-v2-orchestration
3d12c66c1330        ddecouteau/leap-sls-service              "java -Dspring.profi…"   11 minutes ago      Up 38 seconds       0.0.0.0:9091->9091/tcp              leap-sls-service
af80218bfee4        mysql:latest                             "docker-entrypoint.s…"   11 minutes ago      Up 37 seconds       33060/tcp, 0.0.0.0:3307->3306/tcp   hapi-fhir-mysql
a84d62caad14        mysql:latest                             "docker-entrypoint.s…"   11 minutes ago      Up 37 seconds       33060/tcp, 0.0.0.0:3308->3306/tcp   leap-mysql
06e64682d872        ddecouteau/hapi-fhir-jpaserver           "catalina.sh run"        11 minutes ago      Up 38 seconds       0.0.0.0:8080->8080/tcp              hapi-fhir-jpaserver
```

## HL7 V2 Orchestration Demo
The API will receive a V2 message and will process the Message and check the Authorization Decision.
[For further information, please follow this link](leap-ces-v2-orchestration/README.md)


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
