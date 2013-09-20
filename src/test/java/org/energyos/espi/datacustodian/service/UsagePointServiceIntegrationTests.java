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

package org.energyos.espi.datacustodian.service;


import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointServiceIntegrationTests {

    @Autowired
    private UsagePointService service;

    @Test
    public void givenXML_returnsFeed() throws JAXBException, IOException {
        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);

        int count = service.findAllByRetailCustomer(customer).size();

        TestUtils.importUsagePoint(service, customer, UUID.randomUUID());

        assertEquals(count + 1, service.findAllByRetailCustomer(customer).size());
    }
}
