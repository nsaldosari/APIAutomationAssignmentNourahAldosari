package Helpers;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TestHelper {
    // Create a static Reader variable to handle reading CSV files.
    private static Reader csvReader;

    // Method to send a POST request to the API endpoint
    public static Response sendPostRequest(String URL, String endpoint, String requestBody) {
        return RestAssured.given()
                .baseUri(URL)
                .basePath(endpoint)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .post();
    }

    // Method to read data from a CSV file and return as CSVParser
    public static CSVParser readCSV(String csvFilePath) throws IOException {
        // Initialize the FileReader with the provided CSV file path
        csvReader = new FileReader(csvFilePath);
        // Parse the CSV content using CSVFormat and return CSVParser
        return CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
    }

    // Method to close the CSV reader
    public static void closeCSVReader() throws IOException {
        // Check if the csvReader is initialized
        if (csvReader != null) {
            // Close the CSV reader to release resources
            csvReader.close();
        }
    }

    // Method to validate that response status code is 200 (OK)
    public static void validateResponse(Response response) {
        response.then().statusCode(200);
    }

    // Method to check that data is not null
    public static void verifyDataNotNull(String data) {
        assertNotNull(data, "This data should not be null");
    }

    // Method to check that two strings are equal
    public static void verifyStringsEqual(String actualData, String expectedData) {
        // Assert that the actualData text is equal to the expectedData text value
        assertEquals(actualData, expectedData);
    }

    //Method to check that two integers are equal
    public static void verifyIntegersEqual(int actualData, int expectedData) {
        // Assert that the actualData integer value is equal to the expectedData integer value
        assertEquals(actualData, expectedData);
    }

    // Method to check that two floats are equal
    public static void verifyFloatsEqual(float actualData, float expectedData) {
        // Assert that the actualData float value is equal to the expectedData float value
        assertEquals(actualData, expectedData);
    }
}