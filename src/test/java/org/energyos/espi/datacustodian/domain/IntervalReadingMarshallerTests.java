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

package org.energyos.espi.datacustodian.domain;

import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.atom.XMLTest;
import org.energyos.espi.datacustodian.utils.XMLMarshaller;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;

import static org.energyos.espi.datacustodian.support.Asserts.assertXpathValue;
import static org.energyos.espi.datacustodian.support.TestUtils.assertAnnotationPresent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
public class IntervalReadingMarshallerTests extends XMLTest {

    private String xml;
    @Autowired
    XMLMarshaller xmlMarshaller;

    @Before
    public void before() throws Exception {
        xml = xmlMarshaller.marshal(EspiFactory.newIntervalReading());
    }

    @Test
    public void intervalBlock_hasTransientAnnotation() {
        assertAnnotationPresent(IntervalReading.class, "intervalBlock", XmlTransient.class);
    }

    @Test
    public void marshal_setsCost() throws SAXException, IOException, XpathException {
        assertXpathValue("100", "espi:IntervalReading/espi:cost", xml);
    }

    @Test
    public void marshal_setsReadingQualities() throws SAXException, IOException, XpathException {
        assertXpathValue("quality1", "espi:IntervalReading/espi:ReadingQuality[1]/espi:quality", xml);
        assertXpathValue("quality2", "espi:IntervalReading/espi:ReadingQuality[2]/espi:quality", xml);
    }

    @Test
    public void marshal_setsTimePeriod() throws SAXException, IOException, XpathException {
        assertXpathValue("86401", "espi:IntervalReading/espi:timePeriod/espi:duration", xml);
        assertXpathValue("1330578001", "espi:IntervalReading/espi:timePeriod/espi:start", xml);
    }

    @Test
    public void marshal_setsValue() throws SAXException, IOException, XpathException {
        assertXpathValue("6", "espi:IntervalReading/espi:value", xml);
    }
}