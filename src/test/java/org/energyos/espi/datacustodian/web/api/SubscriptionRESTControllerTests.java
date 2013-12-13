package org.energyos.espi.datacustodian.web.api;

import com.sun.syndication.io.FeedException;

import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.utils.ExportFilter;
import org.energyos.espi.datacustodian.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.energyos.espi.common.test.EspiFactory.newSubscription;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

public class SubscriptionRESTControllerTests extends BaseTest {

    @Mock
    private ExportService exportService;

    private MockHttpServletResponse response;
    private SubscriptionRESTController controller;
    private Subscription subscription;
    private Map<String, String> params = null;

    @Before
    public void before() {
        response = new MockHttpServletResponse();
        controller = new SubscriptionRESTController();

        controller.setExportService(exportService);

        subscription = newSubscription();
        subscription.setHashedId("hashedId");
    }

    @Test
    public void show_streamsEntries() throws IOException, FeedException, InterruptedException, JAXBException, XMLStreamException {
        controller.show(response, subscription.getHashedId(), params);

        verify(exportService).exportSubscription(eq(subscription.getHashedId()), eq(response.getOutputStream()), isA(ExportFilter.class));
    }

    @Test
    public void show_respondsWithATOM() throws InterruptedException, XMLStreamException, FeedException, JAXBException, IOException {
        controller.show(response, subscription.getHashedId(), params);

        assertThat(response.getContentType(), is(MediaType.APPLICATION_ATOM_XML_VALUE));
    }

    @Test
    public void show_responds200OK() throws InterruptedException, XMLStreamException, FeedException, JAXBException, IOException {
        controller.show(response, subscription.getHashedId(), params);

        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void show_usesPublishedFilters() throws InterruptedException, XMLStreamException, FeedException, JAXBException, IOException {
        params = new HashMap<>();
        params.put("published-min", "2012-10-24T00:00:00Z");
        params.put("published-max", "2012-10-25T00:00:00Z");

        controller.show(response, subscription.getHashedId(), params);

        verify(exportService).exportSubscription(subscription.getHashedId(), response.getOutputStream(), new ExportFilter(params));
    }

}
