FROM openjdk:11-jdk

ARG JAR_FILE=target/leap-ces-v2-orchestration-0.0.1.war

ENV CDS_HOST_URL=https://sdhc-leap.appspot.com
ENV SLS_HOST_URL=http://34.94.253.50:9091
ENV HAPI_FHIR_URL=http://34.94.253.50:8080/hapi-fhir-jpaserver/fhir/

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=docker","-jar","/app.jar"]
