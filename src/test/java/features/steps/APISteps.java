package features.steps;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.Routes;
import org.openqa.selenium.WebDriver;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import java.io.IOException;

import static features.steps.StepUtils.assertContains;
import static features.steps.StepUtils.clickLinkByText;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.datacustodian.test.TestUtils.getXPathValue;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class APISteps {

    private WebDriver driver;

    @Before
    public void setup() {
        driver = WebDriverSingleton.getInstance();
    }

    @When("^I GET \\/espi\\/1_1\\/resource\\/RetailCustomer\\/\\{RetailCustomerID\\}\\/UsagePoint$")
    public void I_GET_espi_1_1_resource_RetailCustomer_RetailCustomerID_UsagePoint() throws Throwable {
        driver.get(StepUtils.BASE_URL + "/espi/1_1/resource/RetailCustomer/" + CucumberSession.getUserHashedId() + "/UsagePoint");
    }

    @When("^I GET \\/espi\\/1_1\\/resource\\/RetailCustomer\\/\\{RetailCustomerID\\}\\/UsagePoint\\/\\{UsagePointID\\}$")
    public void I_GET_espi_1_1_resource_RetailCustomer_RetailCustomerID_UsagePoint_UsagePointID() throws Throwable {
        CucumberSession.setUsagePointHashedId(StepUtils.getFirstUsagePointHashedId());
        driver.get(StepUtils.BASE_URL + Routes.buildDataCustodianRESTUsagePointMember(CucumberSession.getUserHashedId(), CucumberSession.getUsagePointHashedId()));
    }

    @Then("^I should receive the list of Usage Points$")
    public void I_should_receive_the_list_of_Usage_Points() throws Throwable {
        assertXpathExists("/:feed/:entry[1]/:content/espi:UsagePoint", driver.getPageSource());
        assertXpathExists("/:feed/:entry[2]/:content/espi:UsagePoint", driver.getPageSource());
    }

    @Then("^I should receive the Usage Point$")
    public void I_should_receive_the_Usage_Point() throws SAXException, IOException, XpathException {
        assertXpathExists("/:entry/:content/espi:UsagePoint", driver.getPageSource());
    }

    @When("^I POST \\/espi\\/1_1\\/resource\\/RetailCustomer\\/\\{RetailCustomerID\\}\\/UsagePoint$")
    public void I_POST_espi_1_1_resource_RetailCustomer_RetailCustomerID_UsagePoint() throws Throwable {
        RestTemplate rest = new RestTemplate();
        String response = rest.postForObject(StepUtils.BASE_URL + "/espi/1_1/resource/RetailCustomer/1/UsagePoint",
                "<entry xmlns=\"http://www.w3.org/2005/Atom\">>" +
                        "  <id>urn:uuid:97EAEBAD-1214-4A58-A3D4-A16A6DE718E1</id>" +
                        "  <published>2012-10-24T00:00:00Z</published>" +
                        "  <updated>2012-10-24T00:00:00Z</updated>" +
                        "  <link rel=\"self\"" +
                        "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01\"/>" +
                        "  <link rel=\"up\"" +
                        "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint\"/>" +
                        "  <link rel=\"related\"" +
                        "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01/MeterReading\"/>" +
                        "  <link rel=\"related\"" +
                        "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary\"/>" +
                        "  <link rel=\"related\"" +
                        "        href=\"/espi/1_1/resource/UsagePoint/01/LocalTimeParameters/01\"/>" +
                        "  <title>Created</title>" +
                        "  <content>" +
                        "    <UsagePoint xmlns=\"http://naesb.org/espi\">" +
                        "      <ServiceCategory>" +
                        "        <kind>0</kind>" +
                        "      </ServiceCategory>" +
                        "    </UsagePoint>" +
                        "  </content>" +
                        "</entry>"
                , String.class);

        assertThat(response, is(nullValue()));
    }

    @And("^I PUT \\/espi\\/1_1\\/resource\\/RetailCustomer\\/\\{RetailCustomerID\\}\\/UsagePoint\\/\\{UsagePointID\\}$")
    public void I_PUT_espi__resource_RetailCustomer_RetailCustomerID_UsagePoint_UsagePointID() throws Throwable {
        driver.get(StepUtils.BASE_URL + "/espi/1_1/resource/RetailCustomer/1/UsagePoint");
        String xml = driver.getPageSource();

        String id = getXPathValue("/:feed/:entry/:title[contains(text(),'Created')]/../:id", xml);
        String selfHref = getXPathValue("/:feed/:entry/:title[contains(text(),'Created')]/../:link[@rel='self']/@href", xml);

        String requestBody = "<entry xmlns=\"http://www.w3.org/2005/Atom\">>" +
                "  <id>" + id + "</id>" +
                "  <published>2012-10-24T00:00:00Z</published>" +
                "  <updated>2012-10-24T00:00:00Z</updated>" +
                "  <link rel=\"self\"" +
                "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01\"/>" +
                "  <link rel=\"up\"" +
                "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint\"/>" +
                "  <link rel=\"related\"" +
                "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01/MeterReading\"/>" +
                "  <link rel=\"related\"" +
                "        href=\"/espi/1_1/resource/RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary\"/>" +
                "  <link rel=\"related\"" +
                "        href=\"/espi/1_1/resource/UsagePoint/01/LocalTimeParameters/01\"/>" +
                "  <title>Updated</title>" +
                "  <content>" +
                "    <UsagePoint xmlns=\"http://naesb.org/espi\">" +
                "      <ServiceCategory>" +
                "        <kind>0</kind>" +
                "      </ServiceCategory>" +
                "    </UsagePoint>" +
                "  </content>" +
                "</entry>";
        HttpEntity<String> request = new HttpEntity<>(requestBody);
        RestTemplate rest = new RestTemplate();
        rest.put(StepUtils.BASE_URL + "/espi/1_1/resource/" + selfHref, request);
    }

    @Then("^I should see a new Usage Point$")
    public void I_should_see_a_new_Usage_Point() throws Throwable {
        clickLinkByText("Usage Points");
        assertContains("Created", driver.getPageSource());
    }

    @Then("^I should see an updated Usage Point$")
    public void I_should_see_an_updated_Usage_Point() throws Throwable {
        clickLinkByText("Usage Points");
        assertContains("Updated", driver.getPageSource());
    }

    @And("^I DELETE \\/espi\\/1_1\\/resource\\/RetailCustomer\\/\\{RetailCustomerID\\}\\/UsagePoint\\/\\{UsagePointID\\}$")
    public void I_DELETE_espi__resource_RetailCustomer_RetailCustomerID_UsagePoint_UsagePointID() throws Throwable {
        RestTemplate rest = new RestTemplate();
        rest.delete(StepUtils.BASE_URL + "/espi/1_1/resource/RetailCustomer/" + CucumberSession.getUserHashedId() + "/UsagePoint/" + CucumberSession.getUsagePointHashedId());
    }

    @Then("^the Usage Point should be deleted$")
    public void the_Usage_Point_should_be_deleted() throws Throwable {
        driver.get(StepUtils.BASE_URL + "/espi/1_1/resource/RetailCustomer/1/UsagePoint");
        String xml = driver.getPageSource();

        assertThat(getXPathValue("/:feed/:entry/:link[@rel='self']/@href", xml), is(not("RetailCustomer/" + CucumberSession.getUserHashedId() + "/UsagePoint/" + CucumberSession.getUsagePointHashedId())));
    }
}
