package sadilek.tests;

import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import sadilek.pageobjects.LandingPage;
import sadilek.pageobjects.ParfumPage;
import sadilek.testcomponents.BaseTest;
import sadilek.testcomponents.ExcelDataProvider;
import sadilek.testcomponents.Retry;
import sadilek.exceptions.ElementInteractionException;

/**
 * Test class for validating the functionality of product filters.
 * 
 * This class extends BaseTest.java to inherit the basic test setup and
 * teardown.
 * It uses TestNG for test case management and assertion.
 */
public class ProductFilterTest extends BaseTest {
    static final String HIGHLIGHT = "highlight";
    static final String PRODUKTART = "produktart";
    static final String MARKE = "marke";
    static final String GESCHENKFUR = "geschenkFur";
    static final String FURWEN = "furWen";

    /**
     * Test the product filters return revelant search listings
     * 
     * This test uses the test case filters data from the HashMap supplied by the
     * TestNG data provider and subsequently applies each filter, checks the
     * displayed products, and asserts whether they pass or fail the expected
     * criteria.
     * 
     * @param HashMap containing the set of filters to be applied for each test
     *                case.
     * 
     *                Note: This method catches and handles IOException and
     *                InterruptedException by Assert failing the test.
     */
    @Test(dataProvider = "getData", retryAnalyzer = Retry.class)
    public void testProductFilters(HashMap<String, String> map) {
        try {
            /* local variables populated from our Excel data provider */
            String highlight = map.get(HIGHLIGHT);
            String produktart = map.get(PRODUKTART);
            String marke = map.get(MARKE);
            String geschenkFur = map.get(GESCHENKFUR);
            String furWen = map.get(FURWEN);

            /* set the timeouts to be used by the POM classes */
            int timeoutInSeconds = Integer.parseInt(getProperty("timeout"));
            int retries = Integer.parseInt(getProperty("retries"));

            /* init the Landing Page and handle the modal popup */
            LandingPage landingPage = new LandingPage(getDriver(), timeoutInSeconds, retries);
            landingPage.gotoPage(getProperty("url"));
            landingPage.handleModalPopup();

            ParfumPage parfumPage = landingPage.gotoParfumPage();

            /* handle setting the filter for each search criteria */
            parfumPage.setFilterProdukart(produktart);
            parfumPage.setFilterMarke(marke);
            parfumPage.setFilterHighlight(highlight);
            parfumPage.setFilterGeschenkFur(geschenkFur);
            parfumPage.setFilterFurWen(furWen);

            parfumPage.assertFacetsSetCorrectly(produktart, highlight, marke, geschenkFur, furWen);

            /* we refresh the page to ensure the right number of products are displayed */
            getDriver().navigate().refresh();
            // Thread.sleep(2000);

            /*
             * loop over the products on the current page so that we can assert the
             * each one matches the facet/filter criteria. To limit duration of each test,
             * only the products on the first result page are traversed. Pagintation support
             * for multiple result page looping was avoided as a strategy since most
             * combinations result in a single page of search results.
             */
            for (WebElement product : parfumPage.getProducts()) {
                /*
                 * get the product via jsoup instead of Selenium to speed up testing and since
                 * we do not need to test the product page UI
                 */
                String productPageUrl = parfumPage.getProductLink(product, retries);
                Document document = parfumPage.getDocument(productPageUrl, getProperty("useragent"));

                /*
                 * out of stock products do not display highlights. No specific test case
                 * requirement given, so skip product when encountered
                 */
                if (parfumPage.isOutOfStock(product)) {
                    log.info(productPageUrl + " product out of stock, skipping");
                    continue;
                }

                /*
                 * make sure each product is in view of the browser's viewport to avoid
                 * before moving to it to avoid out of bounds issues
                 */
                parfumPage.scrollIntoView(product);
                parfumPage.waitForVisibilityOf(product);
                parfumPage.actionMoveToElement(product);

                /*
                 * asserts to test the relevance of the products listed. These methods have been
                 * abstracted into the parfum page object class to keep the testProductFilters
                 * test clean.
                 */
                parfumPage.assertEqualsProduktart(product, produktart, productPageUrl);
                parfumPage.assertEqualsMarke(product, marke, productPageUrl);
                parfumPage.assertEqualsHighlight(product, highlight, document, productPageUrl);
                parfumPage.assertEqualsGeschenkFur(document, geschenkFur, productPageUrl);
                // parfumPage.assertEqualsFurWen(document, furWen, productPageUrl);
            }

            /*
             * catch any exceptions that might be thrown so that we can assert fail the test
             * case
             */
        } catch (ElementInteractionException e) {
            log.error("A custom ElementInteractionException was thrown", e);
            Assert.fail("Test interrupted due to a known Exception", e);
        } catch (Exception e) {
            log.error("An unknown exception was thrown!", e);
            Assert.fail("Test interrupted due to an unkown Exception", e);
        }
    }

    /**
     * Data provider for the testProductFilters method
     * 
     * Data provider retrieves test cases (data) from an Excel file defined during
     * the MVN cli initialization or from the settings.properties document.
     * 
     * @return Object[][] (2D object array) containing the test case data for each
     *         test
     * @throws Exception
     */
    @DataProvider(name = "getData", parallel = true)
    public Object[][] getData() throws Exception {
        /*
         * get the file path to the Excel sheet containing our test cases from the
         * properties file or from the mvn test cli variables
         */
        String filePath = getProperty("dataProvider");
        /*
         * ExcelDataProvider contains the Apache POI implementation to pull data from
         * our designated Excel file
         */
        ExcelDataProvider provider = new ExcelDataProvider(filePath);
        return provider.getData();
    }
}
