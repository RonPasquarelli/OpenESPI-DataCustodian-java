package org.energyos.espi.datacustodian.integration.service;

import org.energyos.espi.datacustodian.domain.*;
import org.energyos.espi.datacustodian.service.ImportService;
import org.energyos.espi.datacustodian.service.MeterReadingService;
import org.energyos.espi.datacustodian.service.ResourceService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.factories.EspiPersistenceFactory;
import org.energyos.espi.datacustodian.utils.factories.FixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ImportServiceTests {
    public static final String USAGE_POINT_UUID = "48C2A019-5598-4E16-B0F9-49E4FF27F5FB";
    @Autowired
    private ImportService importService;
    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private MeterReadingService meterReadingService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private EspiPersistenceFactory factory;

    @Test
    public void import_importsUsagePoint() throws IOException, ParserConfigurationException, SAXException {
        importService.importData(FixtureFactory.newFeedInputStream());

        UsagePoint usagePoint = usagePointService.findByUUID(UUID.fromString(USAGE_POINT_UUID));

        assertThat(usagePoint, is(notNullValue()));
    }

    @Test
    public void import_importsMeterReading() throws IOException, ParserConfigurationException, SAXException {
        importService.importData(FixtureFactory.newFeedInputStream());

        MeterReading meterReading = meterReadingService.findByUUID(UUID.fromString("F77FBF34-A09E-4EBC-9606-FF1A59A17CAE"));
        UsagePoint usagePoint = usagePointService.findByUUID(UUID.fromString("48C2A019-5598-4E16-B0F9-49E4FF27F5FB"));

        assertThat(meterReading, is(notNullValue()));
        assertThat(meterReading.getUsagePoint(), is(notNullValue()));

        assertThat(usagePoint.getMeterReadings().get(0), equalTo(meterReading));
    }

    @Test
    public void import_importsReadingType() throws IOException, ParserConfigurationException, SAXException {
        importService.importData(FixtureFactory.newFeedInputStream());

        ReadingType readingType = resourceService.findByUUID(UUID.fromString("3430B025-65D5-493A-BEC2-053603C91CD7"), ReadingType.class);
        MeterReading meterReading = resourceService.findByUUID(UUID.fromString("F77FBF34-A09E-4EBC-9606-FF1A59A17CAE"), MeterReading.class);

        assertThat(readingType, is(notNullValue()));
        assertThat(meterReading.getReadingType(), equalTo(readingType));
    }

    @Test
    public void import_importsIntervalBlock() throws IOException, ParserConfigurationException, SAXException {
        importService.importData(FixtureFactory.newFeedInputStream());

        IntervalBlock intervalBlock = resourceService.findByUUID(UUID.fromString("FE9A61BB-6913-42D4-88BE-9634A218EF53"), IntervalBlock.class);
        MeterReading meterReading = resourceService.findByUUID(UUID.fromString("F77FBF34-A09E-4EBC-9606-FF1A59A17CAE"), MeterReading.class);

        assertThat(intervalBlock, is(notNullValue()));
        assertThat(intervalBlock.getMeterReading(), equalTo(meterReading));
        assertThat(meterReading.getIntervalBlocks().get(0), equalTo(intervalBlock));
    }

    @Test
    public void import_importsElectricPowerQualitySummary() throws IOException, ParserConfigurationException, SAXException {
        importService.importData(FixtureFactory.newFeedInputStream());

        ElectricPowerQualitySummary electricPowerQualitySummary = resourceService.findByUUID(UUID.fromString("DEB0A337-C1B5-4658-99BA-4688E253A99B"), ElectricPowerQualitySummary.class);
        UsagePoint usagePoint = usagePointService.findByUUID(UUID.fromString("48C2A019-5598-4E16-B0F9-49E4FF27F5FB"));

        assertThat(electricPowerQualitySummary, is(notNullValue()));
        assertThat(electricPowerQualitySummary.getUsagePoint(), equalTo(usagePoint));
        assertThat(usagePoint.getElectricPowerQualitySummaries().get(0), equalTo(electricPowerQualitySummary));
    }

    @Test
    public void import_importsElectricPowerUsageSummary() throws IOException, ParserConfigurationException, SAXException {
        importService.importData(FixtureFactory.newFeedInputStream());

        ElectricPowerUsageSummary electricPowerUsageSummary = resourceService.findByUUID(UUID.fromString("621D4BDF-FE3D-418B-8C98-276D941D3D45"), ElectricPowerUsageSummary.class);
        UsagePoint usagePoint = usagePointService.findByUUID(UUID.fromString("48C2A019-5598-4E16-B0F9-49E4FF27F5FB"));

        assertThat(electricPowerUsageSummary, is(notNullValue()));
        assertThat(electricPowerUsageSummary.getUsagePoint(), equalTo(usagePoint));
        assertThat(usagePoint.getElectricPowerUsageSummaries().get(0), equalTo(electricPowerUsageSummary));
    }

    @Test
    public void import_importsLocalTimeParameters() throws IOException, ParserConfigurationException, SAXException {
        importService.importData(FixtureFactory.newFeedInputStream());

        TimeConfiguration localTimeParameters = resourceService.findByUUID(UUID.fromString("54C62EBE-2DB6-4D4F-B6BF-1973A079C841"), TimeConfiguration.class);
        UsagePoint usagePoint = usagePointService.findByUUID(UUID.fromString("48C2A019-5598-4E16-B0F9-49E4FF27F5FB"));

        assertThat(localTimeParameters, is(notNullValue()));
        assertThat(usagePoint.getLocalTimeParameters(), is(localTimeParameters));
    }

    @Test
    public void associateAndImport() throws Exception {
        importService.importData(FixtureFactory.newFeedInputStream());

        RetailCustomer retailCustomer = factory.createRetailCustomer();

        usagePointService.associateByUUID(retailCustomer, UUID.fromString(USAGE_POINT_UUID));

        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);

        assertThat(usagePoints.get(0), is(notNullValue()));
        assertThat(usagePoints.get(0).getElectricPowerUsageSummaries().get(0), is(notNullValue()));
    }

    @Test
    public void upload() throws Exception {
        UUID uuid = UUID.randomUUID();
        RetailCustomer retailCustomer = factory.createRetailCustomer();

        usagePointService.associateByUUID(retailCustomer, uuid);

        importService.importData(FixtureFactory.newFeedInputStream(uuid));

        List<UsagePoint> points = usagePointService.findAllByRetailCustomer(retailCustomer);
        UsagePoint usagePoint = points.get(0);
        assertThat(usagePoint.getElectricPowerUsageSummaries().get(0), is(notNullValue()));
    }
//
//    @Test
//    public void upload_existing() throws Exception {
//        UUID uuid = UUID.randomUUID();
//        UUID uuid1 = UUID.randomUUID();
//
//        RetailCustomer retailCustomer = factory.createRetailCustomer();
//
//        usagePointService.associateByUUID(retailCustomer, uuid);
//        usagePointService.associateByUUID(retailCustomer, uuid1);
//
//        importService.importData(FixtureFactory.newFeedInputStream(uuid));
//        importService.importData(FixtureFactory.newFeedInputStream(uuid1));
//
//        UsagePoint usagePoint = usagePointService.findByUUID(uuid);
//        assertThat(usagePoint.getDescription(), is(notNullValue()));
//        assertThat(usagePoint.getElectricPowerUsageSummaries().get(0), is(notNullValue()));
//        assertThat(usagePoint.getElectricPowerUsageSummaries().get(0).getBillingPeriod().getDuration(), is(notNullValue()));
//        UsagePoint usagePoint1 = usagePointService.findByUUID(uuid1);
//        assertThat(usagePoint1.getElectricPowerUsageSummaries().get(0), is(notNullValue()));
//    }
//
    @Test
    public void upload_existing2() throws Exception {
        UUID uuid = UUID.randomUUID();

        RetailCustomer retailCustomer = factory.createRetailCustomer();

        usagePointService.associateByUUID(retailCustomer, uuid);

        importService.importData(FixtureFactory.newFeedInputStream(uuid));

        UsagePoint usagePoint = usagePointService.findByUUID(uuid);
        assertThat(usagePoint.getDescription(), is(notNullValue()));
        assertThat(usagePoint.getElectricPowerUsageSummaries().get(0), is(notNullValue()));
        assertThat(usagePoint.getElectricPowerUsageSummaries().get(0).getBillingPeriod().getDuration(), is(notNullValue()));
        assertThat(usagePoint.getMeterReadings().size(), equalTo(1));
        assertThat(usagePoint.getMeterReadings().get(0).getIntervalBlocks().size(), equalTo(14));
    }

    @Test
    public void import_IntervalBlocks() throws Exception {
        UUID uuid = UUID.randomUUID();

        RetailCustomer retailCustomer = factory.createRetailCustomer();

        usagePointService.associateByUUID(retailCustomer, uuid);

        importService.importData(FixtureFactory.newFeedInputStream(uuid));

        UsagePoint usagePoint = usagePointService.findByUUID(uuid);
        MeterReading meterReading = meterReadingService.findByUUID(usagePoint.getMeterReadings().get(0).getUUID());
        assertThat(meterReading.getIntervalBlocks().size(), equalTo(14));
    }
//
//    @Test
//    public void exportUsagePoints() throws Exception {
//        UUID uuid = UUID.randomUUID();
//        UUID uuid1 = UUID.randomUUID();
//
//        RetailCustomer retailCustomer = factory.createRetailCustomer();
//
//        usagePointService.associateByUUID(retailCustomer, uuid);
//        usagePointService.associateByUUID(retailCustomer, uuid1);
//
//        importService.importData(FixtureFactory.newFeedInputStream(uuid));
//        importService.importData(FixtureFactory.newFeedInputStream(uuid1));
//
//        System.out.println(usagePointService.exportUsagePoints(retailCustomer));
//    }
//
//    @Test
//    public void testName() throws Exception {
//        importService.importData(FixtureFactory.newFeedInputStream(UUID.randomUUID()));
//    }
}
