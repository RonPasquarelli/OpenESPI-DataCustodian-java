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

import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.repositories.UsagePointRepository;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.ATOMMarshaller;
import org.energyos.espi.datacustodian.utils.StreamMarshaller;
import org.energyos.espi.datacustodian.utils.SubscriptionBuilder;
import org.energyos.espi.datacustodian.utils.UsagePointBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UsagePointServiceImpl implements UsagePointService {

    @Autowired
    private StreamMarshaller streamMarshaller;
    @Autowired
    private UsagePointRepository repository;
    @Autowired
    private ATOMMarshaller marshaller;
    @Autowired
    private UsagePointBuilder usagePointBuilder;
    @Autowired
    private SubscriptionBuilder subscriptionBuilder;

    public void setRepository(UsagePointRepository repository) {
        this.repository = repository;
    }

    public void setMarshaller(ATOMMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setStreamMarshaller(StreamMarshaller streamMarshaller) {
        this.streamMarshaller = streamMarshaller;
    }

    public void setUsagePointBuilder(UsagePointBuilder usagePointBuilder) {
        this.usagePointBuilder = usagePointBuilder;
    }

    public void setSubscriptionBuilder(SubscriptionBuilder subscriptionBuilder) {
        this.subscriptionBuilder = subscriptionBuilder;
    }

    public List<UsagePoint> findAllByRetailCustomer(RetailCustomer customer) {
        return repository.findAllByRetailCustomerId(customer.getId());
    }

    public UsagePoint findById(Long id) {
        return this.repository.findById(id);
    }

    public void persist(UsagePoint up) {
        this.repository.persist(up);
    }

    @Override
    public void importUsagePoints(InputStream stream) throws JAXBException {
        List<UsagePoint> usagePoints = usagePointBuilder.newUsagePoints(streamMarshaller.unmarshal(stream, FeedType.class));

        for (UsagePoint usagePoint : usagePoints) {
            createOrReplaceByUUID(usagePoint);
        }
    }

    public void createOrReplaceByUUID(UsagePoint usagePoint) {
        repository.createOrReplaceByUUID(usagePoint);
    }

    public String exportUsagePoints(RetailCustomer customer) throws FeedException {
        return marshaller.marshal(subscriptionBuilder.buildFeed(findAllByRetailCustomer(customer)));
    }

    public String exportUsagePointById(Long usagePointId) throws FeedException {
        List<UsagePoint> usagePointList = new ArrayList<UsagePoint>();
        usagePointList.add(findById(usagePointId));

        return marshaller.marshal(subscriptionBuilder.buildFeed(usagePointList));
    }

    @Override
    public void associateByUUID(RetailCustomer retailCustomer, UUID uuid) {
        repository.associateByUUID(retailCustomer, uuid);
    }

    @Override
    public UsagePoint findByUUID(UUID uuid) {
       return repository.findByUUID(uuid);
    }

    @Override
    public UsagePoint findByHashedId(String usagePointHashedId) {
        return findById(Long.valueOf(usagePointHashedId));
    }
}
