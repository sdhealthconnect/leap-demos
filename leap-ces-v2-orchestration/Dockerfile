FROM openjdk:11-jdk

ARG JAR_FILE=target/leap-ces-v2-orchestration-0.0.1.war

# Arguments declaration, needed to change ENV variables at build time
ARG ARG_CDS_HOST_URL
ARG ARG_SLS_HOST_URL
ARG ARG_HAPI_FHIR_URL

# Environment Variables declaration, needed to execute stand-alone Dockerfile with custom argumens if they are specified
ENV CDS_HOST_URL=${ARG_CDS_HOST_URL}
ENV SLS_HOST_URL=${ARG_SLS_HOST_URL}
ENV HAPI_FHIR_URL=${ARG_HAPI_FHIR_URL}

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app.jar"]
