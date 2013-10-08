package org.energyos.espi.datacustodian.utils;

import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.domain.*;
import org.energyos.espi.datacustodian.models.atom.ContentType;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.utils.factories.FeedFactory.newFeed;
import static org.energyos.espi.datacustodian.utils.factories.FeedFactory.newFeedType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class ATOMMarshallerTests extends XMLTest {

    String FEED_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String FEED_POSTFIX = "</feed>";
    @Autowired
    private ATOMMarshaller marshaller;

    private String buildContentXml(String espiXml) {
        String XML_PREFIX =
                FEED_PREFIX +
                "  <entry>" +
                "    <id>urn:uuid:E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0</id>" +
                "    <title/>" +
                "    <content>";

        String XML_POSTFIX =
                "    </content>" +
                "    <updated>2012-10-24T00:00:00Z</updated>" +
                "  </entry>" +
                FEED_POSTFIX;

        return XML_PREFIX + espiXml + XML_POSTFIX;
    }

    @Test
    public void unmarshal_givenMultipleIntervalBlocks_returnsIntervalBlocks() throws JAXBException {
        String espiXml = "<IntervalBlock xmlns=\"http://naesb.org/espi\"/>\n" +
                "<IntervalBlock xmlns=\"http://naesb.org/espi\"/>\n";

        List<IntervalBlock> intervalBlocks = unmarshalToContentType(espiXml).getIntervalBlocks();

        assertEquals(intervalBlocks.size(), 2);
    }

    @Test
    public void unmarshall_UsagePoint() throws JAXBException {
        String espiXml = "<UsagePoint xmlns=\"http://naesb.org/espi\"/>";

        UsagePoint usagePoint = unmarshalToContentType(espiXml).getUsagePoint();

        assertNotNull(usagePoint);
    }

    @Test
    public void unmarshall_MeterReading() throws JAXBException {
        String espiXml = "<MeterReading xmlns=\"http://naesb.org/espi\"/>";

        MeterReading meterReading = unmarshalToContentType(espiXml).getMeterReading();

        assertNotNull(meterReading);
    }

    @Test
    public void unmarshall_ReadingType() throws JAXBException {
        String espiXml = "<ReadingType xmlns=\"http://naesb.org/espi\"/>";

        ReadingType readingType = unmarshalToContentType(espiXml).getReadingType();

        assertNotNull(readingType);
    }

    @Test
    public void unmarshall_ElectricPowerUsageSummary() throws JAXBException {
        String espiXml = "<ElectricPowerUsageSummary xmlns=\"http://naesb.org/espi\"/>";

        ElectricPowerUsageSummary electricPowerUsageSummary = unmarshalToContentType(espiXml).getElectricPowerUsageSummary();

        assertNotNull(electricPowerUsageSummary);
    }

    @Test
    public void unmarshall_ElectricPowerQualitySummary() throws JAXBException {
        String espiXml = "<ElectricPowerQualitySummary xmlns=\"http://naesb.org/espi\"/>";

        ElectricPowerQualitySummary electricPowerQualitySummary = unmarshalToContentType(espiXml).getElectricPowerQualitySummary();

        assertNotNull(electricPowerQualitySummary);
    }

    @Test
    public void marshal_domainContent() throws FeedException, SAXException, IOException, XpathException {
        String xmlResult = marshaller.marshal(newFeedType());
        System.out.println(xmlResult);

        assertXpathExists("/feed/entry[1]/content/UsagePoint", xmlResult);
        assertXpathExists("/feed/entry[2]/content/MeterReading", xmlResult);
        assertXpathExists("/feed/entry[3]/content/ReadingType", xmlResult);
        assertXpathExists("/feed/entry[4]/content/ElectricPowerUsageSummary", xmlResult);
        assertXpathExists("/feed/entry[5]/content/ElectricPowerQualitySummary", xmlResult);
        assertXpathExists("/feed/entry[6]/content/IntervalBlock", xmlResult);
    }

    private ContentType unmarshalToContentType(String espiXml) throws JAXBException {
        InputStream xmlStream = new ByteArrayInputStream(buildContentXml(espiXml).getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        return feed.getEntries().get(0).getContent();
    }
}
