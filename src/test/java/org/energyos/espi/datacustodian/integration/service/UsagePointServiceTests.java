package org.energyos.espi.datacustodian.integration.service;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointServiceTests {
    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private RetailCustomerService retailCustomerService;

    @Test
    public void associateByUUID_nonExistentUsagePoint() {
        UUID uuid = UUID.randomUUID();
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);

        usagePointService.associateByUUID(retailCustomer, uuid);

        assertNotNull(usagePointService.findByUUID(uuid));
        assertEquals(retailCustomer.getId(), usagePointService.findByUUID(uuid).getRetailCustomer().getId());
        assertTrue(usagePointService.findAllByRetailCustomer(retailCustomer).size() > 0);
    }

    @Test
    public void associateByUUID_existingUsagePoint() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);

        UsagePoint usagePoint = EspiFactory.newUsagePoint(retailCustomer);

        usagePoint.setRetailCustomer(null);
        usagePointService.persist(usagePoint);

        usagePointService.associateByUUID(retailCustomer, usagePoint.getUUID());

        UsagePoint existingUsagePoint = usagePointService.findByUUID(usagePoint.getUUID());

        assertNotNull(existingUsagePoint.getRetailCustomer());
        assertEquals(retailCustomer.getId(), existingUsagePoint.getRetailCustomer().getId());
    }

    @Test
    public void createOrReplacebyUUID_existingUsagePoint_persistsLocalTimeParameters() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);

        UsagePoint usagePoint = EspiFactory.newUsagePoint(null);
        usagePointService.persist(usagePoint);

        UsagePoint newUsagePoint = EspiFactory.newUsagePoint(retailCustomer);
        newUsagePoint.setUUID(usagePoint.getUUID());
        newUsagePoint.setLocalTimeParameters(null);

        usagePointService.createOrReplaceByUUID(newUsagePoint);

        UsagePoint existingUsagePoint = usagePointService.findByUUID(newUsagePoint.getUUID());

        assertNotNull(existingUsagePoint.getLocalTimeParameters());
    }

    @Test
    public void createOrReplacebyUUID_existingUsagePoint_associatesRetailCustomer() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);

        UsagePoint usagePoint = EspiFactory.newUsagePoint(null);
        usagePointService.persist(usagePoint);

        UsagePoint newUsagePoint = EspiFactory.newUsagePoint(retailCustomer);
        newUsagePoint.setUUID(usagePoint.getUUID());

        usagePointService.createOrReplaceByUUID(newUsagePoint);

        UsagePoint existingUsagePoint = usagePointService.findByUUID(newUsagePoint.getUUID());

        assertEquals(retailCustomer.getId(), existingUsagePoint.getRetailCustomer().getId());
    }
}
