package gov.hhs.onc.leap.ces.fhir.client;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import gov.hhs.onc.leap.ces.fhir.client.exceptions.HapiFhirCreateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Resource;

@Component
@Slf4j
public class HapiFhirServer {
    @Getter
    FhirContext ctx;

    @Getter
    IGenericClient hapiClient;

    @Getter
    @Value("${fhir.r4.baseurl}")
    private String baseURL;

    @PostConstruct
    public void setUp() {
        ctx = FhirContext.forR4();

        hapiClient = ctx.newRestfulGenericClient(baseURL);
        hapiClient.registerInterceptor(createLoggingInterceptor());

        log.info("Created hapi client for server: {} ", baseURL);
    }


    public <T extends Resource> Optional<T> findResourceInBundle(Bundle bundle, Class<T> clazz) {
        if (bundle.hasEntry()) {
            if (bundle.getEntry().size() > 1) {
                log.error("Hapi-Fhir Resource for {} returned more than one resource count: {}",
                        clazz.getSimpleName(), bundle.getEntry().size());
                return Optional.empty();
            } else {
                return findResourceFromBundle(bundle, clazz);
            }
        } else {
            log.debug("Hapi-Fhir Resource for {} NOT found in DB.",
                    clazz.getSimpleName());
            return Optional.empty();
        }
    }

    public <T extends Resource> Optional<T> findResourceFromBundle(Bundle bundle, Class<T> clazz) {
        Resource resource = bundle.getEntry().get(0).getResource();

        if (clazz.isInstance(resource)) {
            log.debug("Hapi-Fhir Resource for {} found in DB.",
                    clazz.getSimpleName());
            return Optional.of((T) resource);
        } else {
            log.error("Hapi-Fhir Resource is of wrong type expected: {} found in bundle: {}",
                    clazz.getSimpleName(),
                    resource.getClass().getSimpleName());
            return Optional.empty();
        }
    }


    public Optional<String> processBundleLink(Bundle bundle) {
        if (bundle.hasEntry()) {
            if (bundle.getEntry().isEmpty()) {
                return Optional.of(bundle.getLink().get(0).getUrl());
            } else {
                return Optional.of(bundle.getEntry().get(0).getFullUrl());
            }
        } else {
            return Optional.empty();
        }
    }

    public String persist(Resource resource) {
        log.debug("Persisting resource {} with id {}",
                resource.getResourceType() != null ? resource.getResourceType().name() : "null",
                resource.getId());
        Bundle bundle = createAndExecuteBundle(resource);

        validatePersistedBundle(resource, bundle);

        //todo this is wrong without it we cannot find object after we create
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            log.debug("InterruptedException", e);
        }

        return buildResourceUrl(resource);
    }

    public String buildResourceUrl(Resource resource) {
        return buildHapiFhirUrl(resource.fhirType(), resource.getId());
    }

    public String buildHapiFhirUrl(String type, String id) {
        return new StringBuilder(hapiClient.getServerBase())
                .append(type)
                .append('/')
                .append(id)
                .toString();
    }

    private void validatePersistedBundle(Resource resource, Bundle bundle) {
        if (CollectionUtils.isEmpty(bundle.getEntry()) || bundle.getEntry().size() > 1) {
            log.error("Bundle size is invalid: {}", bundle.getEntry() != null ? bundle.getEntry().size() : null);
            throw new HapiFhirCreateException(resource.getIdElement().getValue());
        }

        Bundle.BundleEntryComponent bundleEntryComponent = bundle.getEntry().get(0);

        if (!bundleEntryComponent.hasResponse()) {
            log.error("Bundle does not contain a response");
            throw new HapiFhirCreateException(resource.getIdElement().getValue());
        }

        if (bundleEntryComponent.getResponse().getStatus() != null &&
                bundleEntryComponent.getResponse().getStatus().startsWith("20")) {
            log.debug("Successfully (OK) Persisted resource {} with id {}",
                    resource.getResourceType() != null ? resource.getResourceType().name() : "null",
                    resource.getId());
        } else {
            log.error("FAILED Persisted resource: {} with id: {} status:{}",
                    resource.getResourceType().name(), resource.getId(),
                    bundleEntryComponent.getResponse().getStatus());
            throw new HapiFhirCreateException(resource.getIdElement().getValue());
        }
    }

    public Bundle createAndExecuteBundle(Resource resource) {
        Bundle bundle = buildBundle(resource);

        return hapiClient.transaction()
                .withBundle(bundle)
                .execute();
    }

    public Bundle buildBundle(Resource resource) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.TRANSACTION);
        bundle.addEntry().setResource(resource)
                .getRequest()
                .setUrl(baseURL + resource.getResourceType().name() + "/" + resource.getId())
                .setMethod(Bundle.HTTPVerb.PUT);
        return bundle;
    }

    private LoggingInterceptor createLoggingInterceptor() {
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLogger(log);

        // Optionally you may configure the interceptor (by default only summary info is logged)
        loggingInterceptor.setLogRequestBody(false);
        loggingInterceptor.setLogRequestHeaders(false);
        loggingInterceptor.setLogRequestSummary(true);

        loggingInterceptor.setLogResponseBody(false);
        loggingInterceptor.setLogResponseHeaders(false);
        loggingInterceptor.setLogResponseSummary(true);

        return loggingInterceptor;
    }

    public IBaseOperationOutcome delete(IBaseResource resource) {
        return hapiClient.delete()
                .resource(resource)
                .prettyPrint()
                .encodedJson()
                .execute();
    }

    public int count(Class<? extends Resource> resourceClass) {
        return hapiClient.search()
                .forResource(resourceClass)
                .totalMode(SearchTotalModeEnum.ACCURATE)
                .returnBundle(Bundle.class)
                .execute()
                .getTotal();
    }

    public Bundle getAll(Class<? extends Resource> resourceClass) {
        return hapiClient.search()
                .forResource(resourceClass)
                .returnBundle(Bundle.class)
                .execute();
    }

    public Bundle getNextPage(Bundle bundle) {
        return hapiClient.loadPage()
                .next(bundle)
                .execute();
    }

    public String toJson(Resource resource) {
        return getCtx().newJsonParser()
                .setPrettyPrint(true)
                .encodeResourceToString(resource);
    }

    public <T extends Resource> T parseResource(Class<T> resourceClass, String resourceJson) {
        return getCtx()
                .newJsonParser()
                .parseResource(resourceClass, resourceJson);
    }
}
