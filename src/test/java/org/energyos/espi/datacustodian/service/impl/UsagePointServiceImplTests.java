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

package org.energyos.espi.datacustodian.service.impl;


import com.sun.syndication.feed.atom.Feed;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.repositories.UsagePointRepository;
import org.energyos.espi.datacustodian.utils.ATOMMarshaller;
import org.energyos.espi.datacustodian.utils.StreamMarshaller;
import org.energyos.espi.datacustodian.utils.SubscriptionBuilder;
import org.energyos.espi.datacustodian.utils.UsagePointBuilder;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class UsagePointServiceImplTests {

    private UsagePointServiceImpl service;
    private UsagePointRepository repository;
    private StreamMarshaller streamMarshaller;
    private UsagePointBuilder usagePointBuilder;
    private ATOMMarshaller marshaller;

    public UsagePoint usagePoint;

    @Before
    public void setup() {
        repository = mock(UsagePointRepository.class);
        marshaller = mock(ATOMMarshaller.class);
        streamMarshaller = mock(StreamMarshaller.class);
        usagePointBuilder = mock(UsagePointBuilder.class);

        service = new UsagePointServiceImpl();
        service.setRepository(repository);
        service.setUsagePointBuilder(usagePointBuilder);
        service.setMarshaller(marshaller);
        service.setStreamMarshaller(streamMarshaller);

        usagePoint = EspiFactory.newUsagePoint();
        usagePoint.setId(989879L);
    }

    @Test
    public void findAllByRetailCustomer() {
        RetailCustomer customer = new RetailCustomer();

        service.findAllByRetailCustomer(customer);

        verify(repository, times(1)).findAllByRetailCustomerId(customer.getId());
    }


    @Test
    public void findById() {
        service.findById(usagePoint.getId());

        verify(repository).findById(usagePoint.getId());
    }

    @Test
    public void findByHashedId() {
        service.findByHashedId(usagePoint.getHashedId());
        verify(repository).findById(usagePoint.getId());
    }

    @Test
    public void persist() {
        UsagePoint up = new UsagePoint();

        service.persist(up);

        verify(repository).persist(up);
    }

    @Test
    public void importUsagePoints() throws JAXBException, FileNotFoundException {
        UsagePoint usagePoint = new UsagePoint();
        List<UsagePoint> usagePoints = new ArrayList<UsagePoint>();
        usagePoints.add(usagePoint);

        when(usagePointBuilder.newUsagePoints(any(FeedType.class))).thenReturn(usagePoints);

        service.importUsagePoints(mock(InputStream.class));

        verify(repository).createOrReplaceByUUID(usagePoint);
    }

    @Test
    public void importUsagePoint_returnsMarshalledUsagePoint() {
        UsagePoint usagePoint = newUsagePoint();
        InputStream inputStream = mock(InputStream.class);
        EntryType entryType = new EntryType();

        when(streamMarshaller.unmarshal(inputStream, EntryType.class)).thenReturn(entryType);
        when(usagePointBuilder.newUsagePoint(entryType)).thenReturn(usagePoint);

        UsagePoint returnedUsagePoint = service.importUsagePoint(inputStream);

        verify(repository).createOrReplaceByUUID(usagePoint);
        assertThat(returnedUsagePoint, is(usagePoint));
    }

    @Test
    public void exportUsagePoints() throws Exception {

        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);

        SubscriptionBuilder subscriptionBuilder = mock(SubscriptionBuilder.class);

        service.setSubscriptionBuilder(subscriptionBuilder);

        Feed atomFeed = mock(Feed.class);

        List<UsagePoint> usagePointList = new ArrayList<UsagePoint>();
        String atomFeedResult = "<?xml version=\"1.0\"?><feed></feed>";


        when(subscriptionBuilder.buildFeed(usagePointList)).thenReturn(atomFeed);
        when(marshaller.marshal(atomFeed)).thenReturn(atomFeedResult);

        assertEquals(atomFeedResult, service.exportUsagePoints(customer));
        verify(subscriptionBuilder).buildFeed(usagePointList);
        verify(marshaller).marshal(atomFeed);
    }

    @Test
    public void exportUsagePointById() throws Exception {
        Long usagePointId = 1L;
        SubscriptionBuilder subscriptionBuilder = mock(SubscriptionBuilder.class);

        service.setSubscriptionBuilder(subscriptionBuilder);

        Feed atomFeed = mock(Feed.class);

        String atomFeedResult = "<?xml version=\"1.0\"?><feed></feed>";

        when(subscriptionBuilder.buildFeed(anyListOf(UsagePoint.class))).thenReturn(atomFeed);
        when(marshaller.marshal(atomFeed)).thenReturn(atomFeedResult);

        assertEquals(atomFeedResult, service.exportUsagePointById(usagePointId));
        verify(subscriptionBuilder).buildFeed(anyListOf(UsagePoint.class));
        verify(marshaller).marshal(atomFeed);
    }

    @Test
    public void createOrReplaceByUUID() {
        UsagePoint usagePoint = new UsagePoint();

        service.createOrReplaceByUUID(usagePoint);

        verify(repository).createOrReplaceByUUID(usagePoint);
    }
}
