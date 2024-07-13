package Test;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.response.Response;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import resources.Constant;
import Helpers.TestHelper;
import java.io.IOException;

public class AddNewDeviceAPI {

    private static ExtentHtmlReporter htmlReporter;
    private static ExtentReports extent;
    private static ExtentTest test;

    @BeforeSuite
    public void setUp() {
        // Initialize ExtentReports and ExtentHtmlReporter
        htmlReporter = new ExtentHtmlReporter("target/TestReport.html");

        // Configuration of the report
        htmlReporter.config().setDocumentTitle("Automation Test Report");
        htmlReporter.config().setReportName("Rest API Test Report");
        htmlReporter.config().setEncoding("UTF-8");

        // Create instance of ExtentReports and attach reporter(s)
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    @Test
    public void testAddNewDeviceFromCSV() throws IOException {
        // Start the test
        test = extent.createTest("Add New Device Test", "Test to add new devices from CSV file");

        // Open CSV file and create CSVParser
        CSVParser csvParser = null;
        try {
            csvParser = TestHelper.readCSV(Constant.CSV_FILE_PATH);

            // Iterate over CSV records
            for (CSVRecord csvRecord : csvParser) {
                // Extract data from CSV
                String name = csvRecord.get("name");
                int year = Integer.parseInt(csvRecord.get("year"));
                float price = Float.parseFloat(csvRecord.get("price"));
                String cpuModel = csvRecord.get("CPU model");
                String hardDiskSize = csvRecord.get("Hard disk size");

                // Request Payload
                String requestBody = "{\n" +
                        "    \"name\": \"" + name + "\",\n" +
                        "    \"data\": {\n" +
                        "        \"year\": " + year + ",\n" +
                        "        \"price\": " + price + ",\n" +
                        "        \"CPU model\": \"" + cpuModel + "\",\n" +
                        "        \"Hard disk size\": \"" + hardDiskSize + "\"\n" +
                        "    }\n" +
                        "}";

                // Send POST request
                Response response = TestHelper.sendPostRequest(Constant.BASE_URL, Constant.ENDPOINT, requestBody);

                // Assertion and validation
                try {
                    TestHelper.validateResponse(response);

                    // Extract and validate response details
                    String id = response.jsonPath().getString("id");
                    String createdAt = response.jsonPath().getString("createdAt");

                    TestHelper.verifyDataNotNull(id);
                    TestHelper.verifyDataNotNull(createdAt);

                    // Validate the device details against the request payload
                    TestHelper.verifyStringsEqual(response.jsonPath().getString("name"), name);
                    TestHelper.verifyIntegersEqual(response.jsonPath().getInt("data.year"), year);
                    TestHelper.verifyFloatsEqual(response.jsonPath().getFloat("data.price"), price);
                    TestHelper.verifyStringsEqual(response.jsonPath().getString("data['CPU model']"), cpuModel);
                    TestHelper.verifyStringsEqual(response.jsonPath().getString("data['Hard disk size']"), hardDiskSize);

                    // Log test steps to Extent Report
                    test.pass("Test Result: Pass");
                    test.log(Status.INFO, MarkupHelper.createLabel("Request Payload:", ExtentColor.BLUE));
                    test.log(Status.INFO, requestBody);
                    test.log(Status.INFO, MarkupHelper.createLabel("Response Details:", ExtentColor.BLUE));
                    test.log(Status.INFO, "New Device Added:");
                    test.log(Status.INFO, "ID: " + id);
                    test.log(Status.INFO, "Name: " + response.jsonPath().getString("name"));
                    test.log(Status.INFO, "Created At: " + createdAt);
                    test.log(Status.INFO, "Year: " + response.jsonPath().getInt("data.year"));
                    test.log(Status.INFO, "Price: " + response.jsonPath().getFloat("data.price"));
                    test.log(Status.INFO, "CPU model: " + response.jsonPath().getString("data['CPU model']"));
                    test.log(Status.INFO, "Hard disk size: " + response.jsonPath().getString("data['Hard disk size']"));

                } catch (AssertionError e) {
                    // Handle assertion failure
                    test.log(Status.FAIL, "Test Result: Fail");
                    test.log(Status.FAIL, e); // Log the assertion error message
                    Assert.fail("Test failed due to assertion error: " + e.getMessage());
                }

            }
        } finally {
            // Close CSV parser and reader in finally block to ensure they are closed
            if (csvParser != null) {
                csvParser.close();
            }

            TestHelper.closeCSVReader();
        }
    }

    @AfterSuite
    public void tearDown() {
        // Closing the extent report
        extent.flush();
    }
}
