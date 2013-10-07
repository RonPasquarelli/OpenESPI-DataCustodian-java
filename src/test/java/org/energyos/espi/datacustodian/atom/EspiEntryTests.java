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

package org.energyos.espi.datacustodian.atom;


import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.ContentType;
import org.energyos.espi.datacustodian.models.atom.LinkType;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.*;

public class EspiEntryTests extends XMLTest {

    private UsagePointEntry entry;

    @Before
    public void before() throws FeedException {
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setId(1L);
        usagePoint.setMRID("E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0");
        usagePoint.setDescription("Electric Meter");
        RetailCustomer customer = new RetailCustomer();
        customer.setId(3L);
        usagePoint.setRetailCustomer(customer);
        entry = new UsagePointEntry(usagePoint);
    }

    @Test
    public void constructsEspiEntry() throws FeedException, SAXException, IOException, XpathException {

        assertNotNull("entry was null", entry);

        assertEquals("Electric Meter", entry.getTitle());
        assertEquals("Invalid entry id", "urn:uuid:E8E75691-7F9D-49F3-8BE2-3A74EBF6BFC0", entry.getId().getValue());
        assertNotNull("Published is null", entry.getPublished());
        assertNotNull("Updated is null", entry.getUpdated());
        assertTrue(entry.getLinks().contains(entry.getSelfLink()));
        assertTrue(entry.getLinks().contains(entry.getUpLink()));
        assertEquals("self", entry.getSelfLink().getRel());
        assertEquals("up", entry.getUpLink().getRel());

        ContentType content = (ContentType)entry.getContent();
        assertNotNull(content.getUsagePoint());
    }

    @Test
    public void addRelatedLink() {
        String relatedHref = "related href";
        entry.addRelatedLink(relatedHref);
        assertTrue(containsRelatedLink(relatedHref));
    }

    private boolean containsRelatedLink(String href) {
        for (LinkType link : entry.getRelatedLinks()) {
            if (link.getHref() == href) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void setUpLinkHref() {
        entry.setUpLinkHref("up href");
        assertEquals("up href", entry.getUpLink().getHref());
    }

    @Test
    public void setSelfLinkHref() {
        entry.setSelfLinkHref("self href");
        assertEquals("self href", entry.getSelfLink().getHref());
    }
}