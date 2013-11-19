package org.energyos.espi.datacustodian.integration.utils;

import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.domain.ElectricPowerUsageSummary;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.LinkType;
import org.energyos.espi.datacustodian.service.ResourceService;
import org.energyos.espi.datacustodian.utils.ResourceLinker;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.energyos.espi.datacustodian.utils.factories.EspiPersistenceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ResourceLinkerTests extends BaseTest {
    @Autowired
    private ResourceLinker linker;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private EspiPersistenceFactory factory;

    @Test
    public void link_linkUp() {
        UsagePoint usagePoint = factory.createUsagePoint();
        String relatedLink = UUID.randomUUID().toString();
        usagePoint.getRelatedLinks().add(new LinkType(LinkType.RELATED, relatedLink));
        resourceService.persist(usagePoint);
        MeterReading meterReading  = EspiFactory.newMeterReading();
        meterReading.setUpLink(new LinkType(LinkType.UP, relatedLink));
        resourceService.persist(meterReading);

        assertThat(meterReading.getUsagePoint(), is(nullValue()));

        linker.link(meterReading);

        assertThat(meterReading.getUsagePoint(), equalTo(usagePoint));
    }


    @Test
    public void link_linkUp_givenUsagePointAndElectricPowerUsageSummary() {
        UsagePoint usagePoint = factory.createUsagePoint();
        String relatedLink = UUID.randomUUID().toString();
        usagePoint.getRelatedLinks().add(new LinkType(LinkType.RELATED, relatedLink));
        resourceService.persist(usagePoint);

        ElectricPowerUsageSummary electricPowerUsageSummary  = EspiFactory.newElectricPowerUsageSummary();
        electricPowerUsageSummary.setUpLink(new LinkType(LinkType.UP, relatedLink));
        resourceService.persist(electricPowerUsageSummary);

        assertThat(electricPowerUsageSummary.getUsagePoint(), is(nullValue()));

        linker.link(electricPowerUsageSummary);

        usagePoint = resourceService.findByUUID(usagePoint.getUUID(), UsagePoint.class);

        assertThat(usagePoint.getElectricPowerUsageSummaries(), hasItem(electricPowerUsageSummary));
    }

    @Test
    public void link_linkRelated() {
        String relatedLink = UUID.randomUUID().toString();
        MeterReading meterReading  = EspiFactory.newMeterReading();
        meterReading.setUpLink(new LinkType(LinkType.UP, relatedLink));
        resourceService.persist(meterReading);
        UsagePoint usagePoint = factory.createUsagePoint();
        usagePoint.getRelatedLinks().add(new LinkType(LinkType.RELATED, relatedLink));
        resourceService.persist(usagePoint);

        assertThat(meterReading.getUsagePoint(), is(nullValue()));

        linker.link(usagePoint);

        assertThat(meterReading.getUsagePoint(), equalTo(usagePoint));
    }
}
