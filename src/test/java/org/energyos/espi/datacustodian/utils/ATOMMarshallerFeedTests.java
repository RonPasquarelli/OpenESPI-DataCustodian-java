/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.utils;

import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.models.atom.IdType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.support.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.support.Asserts.assertXpathValueStartsWith;
import static org.energyos.espi.datacustodian.utils.factories.FeedFactory.newFeed;
import static org.energyos.espi.datacustodian.utils.factories.FeedFactory.newFeedType;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class ATOMMarshallerFeedTests extends XMLTest {

    String FEED_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String FEED_POSTFIX = "</feed>";

    @Autowired
    private ATOMMarshaller marshaller;

    @Test(expected = UnmarshallingFailureException.class)
    public void unmarshal_givenInvalidInput_throwsAnException() throws JAXBException {
        marshaller.unmarshal(new ByteArrayInputStream(new byte[0]));
    }

    @Test
    public void unmarshal_handlesAnEmptyFeed() throws JAXBException {
        String xml = FEED_PREFIX + FEED_POSTFIX;

        marshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
    }

    @Test
    public void unmarshal_setsFeedId() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <id>urn:uuid:D7B58EA6-D94D-45D1-A0CA-F8A843AB1080</id>" +
                FEED_POSTFIX;
        FeedType feed = unmarshalToFeed(xml);

        assertEquals(IdType.class, feed.getId().getClass());
    }

    @Test
    public void unmarshal_setsEntryId() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <id>urn:uuid:D7B58EA6-D94D-45D1-A0CA-F8A843AB1080</id>" +
                "   </entry>" +
                FEED_POSTFIX;
        FeedType feed = unmarshalToFeed(xml);

        assertEquals(IdType.class, feed.getEntries().get(0).getId().getClass());
    }

    @Test
    public void unmarshal_setsEntryTitle() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <title>Entry Title</title>" +
                "   </entry>" +
                FEED_POSTFIX;
        FeedType feed = unmarshalToFeed(xml);

        assertEquals("Entry Title", feed.getEntries().get(0).getTitle());
    }

    @Test
    public void unmarshal_setsEntryPublished() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <published>2000-02-29T00:00:00Z</published>" +
                "   </entry>" +
                FEED_POSTFIX;
        FeedType feed = unmarshalToFeed(xml);

        assertEquals(new DateTime(2000, 2, 29, 0, 0, 0, 0, DateTimeZone.UTC), feed.getEntries().get(0).getPublished());
    }

    @Test
    public void unmarshal_setsEntryUpdated() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <updated>2000-02-29T00:00:00Z</updated>" +
                "   </entry>" +
                FEED_POSTFIX;
        FeedType feed = unmarshalToFeed(xml);

        assertEquals(new DateTime(2000, 2, 29, 0, 0, 0, 0, DateTimeZone.UTC), feed.getEntries().get(0).getUpdated());
    }

    @Test
    public void marshal_setsFeedId() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("urn:uuid:0071C5A7-91CF-434E-8BCE-C38AC8AF215D", "/feed/id", newFeedXML());
    }

    @Test
    public void marshal_setsFeedTitle() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("Feed title", "/feed/title", newFeedXML());
    }

    @Test
    public void marshal_setsFeedUpdated() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValueStartsWith("2013-12-28T", "/feed/updated", newFeedXML());
    }

    @Test
    public void marshal_setsFeedSelfLink() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("/ThirdParty/83e269c1/Batch", "/feed/link[@rel='self']/@href", newFeedXML());
    }

    @Test
    public void marshal_setsFeedEntries() throws FeedException, SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]", newFeedXML());
        assertXpathExists("/feed/entry[2]", newFeedXML());
    }

    @Test
    public void marshal_setsEntryId() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("urn:uuid:7BC41774-7190-4864-841C-861AC76D46C2", "/feed/entry[1]/id", newFeedXML());
    }

    @Test
    public void marshal_setsEntrySelfLink() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("RetailCustomer/9b6c7063/UsagePoint/01", "/feed/entry[1]/link[@rel='self']/@href", newFeedXML());
    }

    @Test
    public void marshal_setsEntryUpLink() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("RetailCustomer/9b6c7063/UsagePoint", "/feed/entry[1]/link[@rel='up']/@href", newFeedXML());
    }

    @Test
    public void marshal_setsEntryRelatedLink() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValue("RetailCustomer/9b6c7063/UsagePoint/01/MeterReading", "/feed/entry[1]/link[@rel='related']/@href", newFeedXML());
    }

    @Test
    public void marshal_setsEntryPublished() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValueStartsWith("2012-11-15T", "/feed/entry[1]/published", newFeedXML());
    }

    @Test
    public void marshal_setsEntryUpdated() throws FeedException, SAXException, IOException, XpathException {
        assertXpathValueStartsWith("2012-11-17T", "/feed/entry[1]/updated", newFeedXML());
    }

    @Test
    public void marshal_setsEntryTitle() throws SAXException, IOException, XpathException, FeedException {
        assertXpathEvaluatesTo("Front Electric Meter", "/feed/entry[1]/title", newFeedXML());
    }

    @Test
    public void marshal_setsEntryContent() throws SAXException, IOException, XpathException, FeedException {
        assertXpathExists("/feed/entry[1]/content", newFeedXML());
    }

    private String newFeedXML() throws FeedException {
        return marshaller.marshal(newFeedType());
    }

    private FeedType unmarshalToFeed(String xml) throws JAXBException {
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        return marshaller.unmarshal(xmlStream);
    }
}
