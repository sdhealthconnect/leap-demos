package gov.hhs.onc.leap.ces.fhir.client.tests;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import gov.hhs.onc.leap.ces.fhir.client.HapiFhirServer;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ValueSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HapiFhirServerTest {

    @Mock
    IGenericClient hapiClient;
    @Mock
    ITransactionTyped<Bundle> bundleITransactionTyped;
    @Mock
    IUntypedQuery iUntypedQuery;
    @Mock
    IQuery iQuery;
    @Mock
    private FhirContext ctx;
    @Mock
    private ITransaction iTransaction;
    @InjectMocks
    private HapiFhirServer hapiFhirServer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hapiFhirServer, "baseURL", "http://acme.com");
    }

    @Test
    void getCtx() {
        assertEquals(ctx, hapiFhirServer.getCtx());
    }

    @Test
    void getHapiClient() {
        assertEquals(hapiClient, hapiFhirServer.getHapiClient());
    }

    @Test
    void createBundle() {
        Bundle bundleToReturn = new Bundle();

        when(hapiClient.transaction()).thenReturn(iTransaction);
        when(iTransaction.withBundle(any(Bundle.class))).thenReturn(bundleITransactionTyped);
        when(bundleITransactionTyped.execute()).thenReturn(bundleToReturn);

        Bundle bundleReturned = hapiFhirServer.createAndExecuteBundle(new Patient());
        assertEquals(bundleToReturn, bundleReturned);

        verify(hapiClient).transaction();
        verify(iTransaction).withBundle(any(Bundle.class));
        verify(bundleITransactionTyped).execute();
    }

    @Test
    void buildBundle() {
        ValueSet resource = new ValueSet();
        Bundle bundle = hapiFhirServer.buildBundle(resource);
        assertEquals(1, bundle.getEntry().size());

        assertEquals(Bundle.BundleType.TRANSACTION, bundle.getType());
    }

    @Test
    void delete() {
        Patient resource = new Patient();
        IDelete iDelete = mock(IDelete.class);
        IDeleteTyped iDeleteTyped = mock(IDeleteTyped.class);

        IBaseOperationOutcome iBaseOperationOutcomeToReturn = mock(IBaseOperationOutcome.class);

        when(hapiClient.delete()).thenReturn(iDelete);
        when(iDelete.resource(resource)).thenReturn(iDeleteTyped);
        when(iDeleteTyped.prettyPrint()).thenReturn(iDeleteTyped);
        when(iDeleteTyped.encodedJson()).thenReturn(iDeleteTyped);
        when(iDeleteTyped.execute()).thenReturn(iBaseOperationOutcomeToReturn);

        IBaseOperationOutcome iBaseOperationOutcomeReturned = hapiFhirServer.delete(resource);
        assertEquals(iBaseOperationOutcomeToReturn, iBaseOperationOutcomeReturned);

        verify(hapiClient).delete();
        verify(iDelete).resource(resource);
        verify(iDeleteTyped).prettyPrint();
        verify(iDeleteTyped).encodedJson();
        verify(iDeleteTyped).execute();
    }

    @Test
    void getNextPage() {
        Bundle param = new Bundle();
        Bundle toReturn = new Bundle();

        IGetPage iGetPage = mock(IGetPage.class);
        IGetPageTyped iBaseBundle = mock(IGetPageTyped.class);

        when(hapiClient.loadPage()).thenReturn(iGetPage);
        when(iGetPage.next(param)).thenReturn(iBaseBundle);
        when(iBaseBundle.execute()).thenReturn(toReturn);

        Bundle returned = hapiFhirServer.getNextPage(param);

        assertEquals(toReturn, returned);

        verify(hapiClient).loadPage();
        verify(iGetPage).next(param);
        verify(iBaseBundle).execute();
    }
}
