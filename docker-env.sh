#!/bin/sh

export HAPI_DB_URL='jdbc:mysql://hapi-fhir-mysql:3306/hapi?serverTimezone=UTC&max_allowed_packet=16777216&createDatabaseIfNotExist=true'
export HAPI_DB_USER=hapiDbUser
export HAPI_DB_PASS=hapiDbPass

export LEAP_DB_URL='jdbc:mysql://leap-mysql:3306/leap?serverTimezone=UTC&max_allowed_packet=16777216&createDatabaseIfNotExist=true'
export LEAP_DB_USER=admin
export LEAP_DB_PASS=admin


export HAPI_FHIR_URL_PUBLIC='http://host.docker.internal:8080/hapi-fhir-jpaserver/'
export HAPI_FHIR_URL='http://hapi-fhir-jpaserver:8080/hapi-fhir-jpaserver/fhir/'

export CDS_HOST_URL='https://sdhc-leap.appspot.com'
export SLS_HOST_URL='http://leap-sls-service:9091'


export LEAP_LOG_LEVEL='WARN'
