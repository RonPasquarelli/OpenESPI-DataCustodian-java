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

import org.energyos.espi.datacustodian.domain.TimeConfiguration;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.xml.bind.JAXBException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class UsagePointBuilderTimeConfigurationTests {

    @Autowired
    private StreamMarshaller marshaller;
    private TimeConfiguration timeConfiguration;

    @Before
    public void before() throws IOException, JAXBException {
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/test_usage_data.xml");
        FeedType feed = marshaller.unmarshal(sourceFile.getInputStream(), FeedType.class);

        UsagePointBuilder builder = new UsagePointBuilder();

        UsagePoint usagePoint = builder.newUsagePoints(feed).get(0);
        timeConfiguration = usagePoint.getLocalTimeParameters();
    }

    @Test
    public void buildsTimeConfiguration() {
        assertEquals(TimeConfiguration.class, timeConfiguration.getClass());
    }

    @Test
    public void setsDescription() {
        assertNotNull(timeConfiguration.getDescription());
    }
}

