package sadilek.pageobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import sadilek.abstractcomponents.AbstractComponent;
import sadilek.exceptions.ElementInteractionException;

/**
 * ParfumPage represents the parfum page of Douglas.de using Page Object
 * notation.
 * 
 * It inherits common functions from the AbstractComponent class that are useful
 * for the other pages as well.
 * 
 * Most methods handle parfum page interacions.
 * A few methods handle JSoup HTTP requests to get data from the product page.
 * There is no dedicated product page POM class because we don't need to
 * interact with the UI elements. So this speeds up operations.
 * 
 */
public class ParfumPage extends AbstractComponent {
    private WebDriver driver;
    private Logger log = LogManager.getLogger(ParfumPage.class);
    private int retries;

    /**
     * ParfumPage constructor
     * 
     * @param driver           the WebDriver instance we are targetting
     * @param timeoutInSeconds sets the timemout in seconds for any WebDriverWait
     *                         scenarios
     */
    public ParfumPage(WebDriver driver, int timeoutInSeconds, int retries) {
        super(driver, timeoutInSeconds);
        this.driver = driver;
        this.retries = retries;

        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".facet-option__checkbox")
    private WebElement facetCheckbox;

    @FindBy(css = "input[name='facet-search']")
    private WebElement searchField;

    @FindBy(css = ".facet")
    private List<WebElement> facets;

    @FindBy(css = ".facet__close-button")
    private WebElement closeButton;

    @FindBy(css = ".product-overview h1")
    private WebElement productH1;

    @FindBy(css = ".out-of-stock .label-text")
    private WebElement outOfStock;

    /*
     * REFACTOR: The setFilter methods should refactored into a single
     * method. @Lucas
     */

    /**
     * Sets the filter for the specified facet (filter) while abstracting some of
     * the details away from the main test method.
     * 
     * @param facet the entry to set in the filter
     * @throws ElementInteractionException the exception to be propagated
     */
    public void setFilterProdukart(String facet) throws ElementInteractionException {
        log.info("produktart setting filter to: " + facet);
        setFilter("produktart", facet, driver, true, retries);
    }

    /**
     * Sets the filter for the specified facet (filter) while abstracting some of
     * the details away from the main test method.
     * 
     * @param facet the entry to set in the filter
     * @throws ElementInteractionException the exception to be propagated
     */
    public void setFilterMarke(String payload) throws ElementInteractionException {
        log.info("marke setting filter " + payload);
        setFilter("marke", payload, driver, true, retries);
    }

    /**
     * Sets the filter for the specified facet (filter) while abstracting some of
     * the details away from the main test method.
     * 
     * @param facet the entry to set in the filter
     * @throws ElementInteractionException the exception to be propagated
     */
    public void setFilterGeschenkFur(String payload) throws ElementInteractionException {
        log.info("geschenk fur setting filter " + payload);
        setFilter("Geschenk für", payload, driver, true, retries);
    }

    /**
     * Sets the filter for the specified facet (filter) while abstracting some of
     * the details away from the main test method.
     * 
     * @param facet the entry to set in the filter
     * @throws ElementInteractionException the exception to be propagated
     */
    public void setFilterHighlight(String payload) throws ElementInteractionException {
        log.info("highlight setting filter " + payload);
        setFilter("Highlights", payload, driver, false, retries);
    }

    /**
     * Sets the filter for the specified facet (filter) while abstracting some of
     * the details away from the main test method.
     * 
     * @param facet the entry to set in the filter
     * @throws ElementInteractionException the exception to be propagated
     */
    public void setFilterFurWen(String payload) throws ElementInteractionException {
        log.info("fur wen setting filter " + payload);
        setFilter("Für Wen", payload, driver, false, retries);
    }

    /**
     * Asserts that the 'Produktart' attribute of a given product WebElement matches
     * the expected facet.
     *
     * @param product        The WebElement representing the product whose
     *                       'Produktart' attribute is to be checked.
     * @param expected       The expected 'Produktart' value for the product.
     * @param productPageUrl The URL of the product page, included in the assertion
     *                       message for debugging purposes.
     */
    public void assertEqualsProduktart(WebElement product, String expected, String productPageUrl) {
        if (!isEmptyString(expected)) {

            String normalizedActual = getProduktart(product).toLowerCase();
            String normalizedExpected = expected.toLowerCase();

            Assert.assertEquals(normalizedActual, normalizedExpected, "produktart mismatch- " + productPageUrl);
        }
    }

    /**
     * Asserts that the 'Marke' attribute of a given product WebElement matches the
     * expected facet.
     * 
     * @param product        The WebElement representing the product whose
     *                       'Produktart' attribute is to be checked.
     * @param facet          The expected 'Produktart' value for the product.
     * @param productPageUrl The URL of the product page, included in the assertion
     *                       message for debugging purposes.
     */
    public void assertEqualsMarke(WebElement product, String facet, String productPageUrl) {
        String normalizedActual = getMarke(product).toLowerCase();
        String normalizedExpected = facet.toLowerCase();

        if (!isEmptyString(facet)) {
            Assert.assertEquals(normalizedActual, normalizedExpected,
                    "marke mismatch - " + productPageUrl);
        }
    }

    /**
     * Asserts that the 'Geschenk Für' attribute of a product on the given webpage
     * matches the expected value.
     *
     * @param document       The Jsoup Document object containing the parsed HTML of
     *                       the product page.
     * @param expected       The expected 'Geschenk Für' value to be compared with.
     * @param productPageUrl The URL of the product page, included in the assertion
     *                       message for debugging.
     */
    public void assertEqualsGeschenkFur(Document document, String expected, String productPageUrl) {
        if (!isEmptyString(expected)) {

            String normalizedActual = getGeschenkFur(document).toLowerCase();
            String normalizedExpected = expected.toLowerCase();

            if (!normalizedActual.contains(normalizedExpected)) {
                Assert.assertEquals(normalizedActual, normalizedExpected, "geschenk fur mismatch - " + productPageUrl);
            }
        }
    }

    /**
     * Asserts that the "highlight" attribute of a product matches the expected
     * value. We handle the NEU, SALE, and LIMITIERT highlight values.
     *
     * @param product        The WebElement representing the product whose
     *                       "highlight" attribute is to be checked.
     * @param highlight      The expected highlight value to be compared with the
     *                       actual value.
     * @param document       The Jsoup Document object containing the parsed HTML of
     *                       the product page.
     *                       Used specifically when the highlight is "Limitiert".
     * @param productPageUrl The URL of the product page; this will be included in
     *                       the assertion
     *                       message to provide context in case of test failure.
     */
    public void assertEqualsHighlight(WebElement product, String highlight, Document document, String productPageUrl) {
        if (!isEmptyString(highlight)) {

            String normalizedExpected = highlight.toLowerCase();

            /* handle the LIMITIERT highlight */
            if (highlight.equalsIgnoreCase("Limitiert")) {
                String normalizedActual = document.select(".eyecatcher span").text().toLowerCase();

                /* assert the right highlight is being displayed */
                if (!normalizedActual.contains(normalizedExpected)) {
                    Assert.assertEquals(normalizedActual, normalizedExpected,
                            "highlights mismatch - " + productPageUrl);
                }
                /* handle the NEU and SALE highlight (which are found in the JSOUP document) */
            } else {
                /*
                 * Get the list of available highlights as a string from our JSoup DOM document
                 * so that we can check if it contains our expected highlight value.
                 */
                String normalizedHighlights = getHighlights(product).toLowerCase();

                /*
                 * SALE can also be replaced by text like
                 * "-19% ZUM UVP." We are just going to check that the distinct class name is
                 * available in the DOM.
                 * 
                 * if the highlights string does not contain the expected highlight then assert
                 * equals to report it.
                 */
                if (normalizedExpected.equals("sale")) {
                    if (!isSaleHighlightDisplayed(product)) {
                        Assert.assertEquals(normalizedHighlights, normalizedExpected,
                                "highlights mismatch - " + productPageUrl);
                    }
                }

                /*
                 * check that the NEU highlight is displayed.
                 * Uses a classname to verify it is displayed
                 * 
                 * if the highlights string does not contain the expected highlight then assert
                 * equals to report it.
                 */
                if (normalizedExpected.equals("neu")) {
                    if (!isNeuHighlightDisplayed(product)) {
                        Assert.assertEquals(normalizedHighlights, normalizedExpected,
                                "highlights mismatch - " + productPageUrl);
                    }
                }

                /*
                 * if the highlights string does not contain the expected highlight then assert
                 * equals to report it.
                 */
                /*
                 * if (!normalizedHighlights.contains(normalizedExpected)) {
                 * Assert.assertEquals(normalizedHighlights, normalizedExpected,
                 * "highlights mismatch - " + productPageUrl);
                 * }
                 */
            }
        }
    }

    /**
     * REFACTOR: add JavaDoc info for this method.
     * 
     * @param produktart
     * @param highlight
     * @param marke
     * @param geschenkFur
     * @param furWen
     */
    public void assertFacetsSetCorrectly(String produktart, String highlight, String marke, String geschenkFur,
            String furWen) {
        String[] expected = { produktart.toLowerCase(), highlight.toLowerCase(), marke.toLowerCase(),
                geschenkFur.toLowerCase(), furWen.toLowerCase() };
        String actual = getSelectedFacets().toLowerCase();

        for (String facet : expected) {
            if (!actual.contains(facet)) {
                log.info("Filters not correctly set");
                log.info("filters: ACTUAL: " + actual);
                log.info("filters: EXPECTED: " + expected.toString());

                /*
                 * we found a difference in the expected and actual filters that have been
                 * displayed. Fail the test.
                 */
                Assert.assertEquals(actual, expected, "The facets were not properly set");
            }
        }
        log.info("All filters correctly set: VERIFIED");
    }

    /**
     * Sets the desired filter on the Parfum page.
     * 
     * This method applies a facet filter based on the provided filter name and
     * payload.
     * If the withKeys flag is set to true, it searches for the facet using the
     * provided payload.
     * After setting the filter, it waits for the filter to be activated and then
     * closes the filter dropdown.
     *
     * @param filterName The name of the filter facet to be activated.
     * @param payload    The name of the facet option within the dropdown to be
     *                   selected.
     *                   If the withKeys flag is true, this payload is also used for
     *                   searching the facet.
     * @param driver     The WebDriver instance to interact with the web page.
     * @param withKeys   A flag to determine if the facet should be searched using
     *                   the payload.
     */
    private void setFilter(String filterName, String facet, WebDriver driver, Boolean withKeys, int localRetries)
            throws ElementInteractionException {
        /* check if the payload contains something otherwise return */
        if (isEmptyString(facet)) {
            log.info("\tempty facet, skipping filter");
            return;
        }

        // make this recurive for when the dropdown does not open. //
        try {
            /*
             * make sure the section is loaded by checking for the h1 header above the
             * filters
             */
            // waitForVisibilityOf(facets.get(0));

            /* click to open the dropdown filter in the UI */
            openFilterDropdown(filterName, retries);

            /*
             * if the withKeys boolean parameter is set to true, enter the facet name into
             * the search field
             */
            if (withKeys) {
                inputFilterSearch(facet);
            }

            /* select the filter in the dropdown list */
            selectFilterOption(facet);

            /*
             * check the list of tags displayed underneath the filter area to confirm our
             * selection was successful.
             */
            waitForFilterToBeEnabled(facet);

            closeFilter();

            /*
             * most of the flaky behavior on the Douglas site is caused by flaky dropdown
             * interactions. Here we catch any exception, refresh the page, and try to set
             * the filter again the specified amount of times.
             */
        } catch (TimeoutException | ElementInteractionException | NoSuchElementException
                | StaleElementReferenceException | ElementNotInteractableException e) {

            /*
             * REFACTOR: should this be moved to the top of the method block? Why execute
             * the block if we run out of retries?
             * retry to set filter at least N number of times to avoid flaky set filter
             * issues
             */
            if (localRetries <= 0)
                throw new ElementInteractionException("SET FILTER ERROR!" + e.getMessage(), e);

            log.warn("SET FILTER: exception caught, retrying to set filter " + facet + " (recursively) Retries left: #"
                    + localRetries);
            // refresh page as well
            driver.navigate().refresh();
            // return the method again until we are out of retries
            setFilter(filterName, facet, driver, withKeys, localRetries - 1);

        }
    }

    /**
     * Attempts to open a filter dropdown identified by the provided filter name.
     * 
     * This part of the Douglas UI is one of the most flaky elements. A retry
     * mechanism is include to recursively call the method to retry during
     * execeptions until the max retries limit is reached.
     *
     * @param filterName The name of the filter whose dropdown is to be opened.
     * @param retries    The number of retry attempts remaining before failing and
     *                   throwing an exception.
     * 
     * @throws ElementInteractionException if max retries are reached or any known
     *                                     exceptions are encountered.
     */
    private void openFilterDropdown(String filterName, int localRetries) throws ElementInteractionException {
        try {
            if (localRetries <= 0) {
                log.error("MAX RETRIES to open the dropdown reached, throwing InterruptedException");
                throw new ElementInteractionException(
                        "max retries for the openFilterDropDown method have been reached.", null);
            }
            // add a wait for here to wait for the facet section to be useable
            waitForNumberOfFiltersToNotBeZero();

            /*
             * loop over all facets and check the text to match our facetName
             * then click it so that the filter drop down appears
             */
            facets.stream()
                    .filter(facet -> facet.findElement(By.cssSelector(".facet__title")).getText()
                            .equalsIgnoreCase(filterName))
                    .findFirst()
                    .ifPresent(facet -> {
                        log.info("clicking: " + facet.getText());
                        facet.click();
                    });

            /*
             * we have to wait until the options appear on screen, otherwise an execption
             * will be thrown.
             */
            try {
                waitForElementToBeClickable(facetCheckbox);
            } catch (Exception ex) {
                log.warn("OPEN FILTER DROPDOWN exception waiting for searched facet (" + filterName
                        + ") to be clickable.");
                log.info("calling set open filter dropdown method recursively to try again. RETRIES LEFT #"
                        + localRetries);
                openFilterDropdown(filterName, localRetries - 1);
            }
        } catch (ElementInteractionException e) {
            throw new ElementInteractionException("OPEN FILTER DROPDOWN: a known exception was thrown" + e.getMessage(),
                    e);
        }
    }

    /**
     * Search for the inputted query (facet) in the dropdown search field.
     * 
     * @param query the facet to query (search for) in the dropdown
     */
    private void inputFilterSearch(String query) {
        waitForElementToBeClickable(searchField);

        /* find the input field and send keys */
        searchField.sendKeys(query);
        log.info("search field contains: " + searchField.getAttribute("value"));

        /*
         * we have to waita again until the options reappear on screen, otherwise an
         * execption
         * will be thrown.
         */
        waitForElementToBeClickable(facetCheckbox);
    }

    /**
     * Wrapper for the selectFilterOption that allows us to call it recursively in
     * case of stale element exception using the class level max retry variable.
     * 
     * @param facet the facet to select in the dropdown
     */
    private void selectFilterOption(String facet) {
        selectFilterOption(facet, this.retries);
    }

    /**
     * Select the inputted facet in the list of checkboxes listed in the dropdown.
     * 
     * @param facet        the facet to look for and click.
     * @param localRetries the max number of retries to facilitate
     */
    private void selectFilterOption(String facet, int localRetries) {
        /*
         * loop over dropdown options and click the entry that matches the option name
         */
        try {
            driver.findElements(By.cssSelector("a[class*='facet-option']"))
                    .stream()
                    .filter(option -> option
                            .findElement(By.cssSelector(
                                    ".facet-option__checkbox--rating-stars"))
                            .getText()
                            .split(" \\(")[0]
                            .trim().equalsIgnoreCase(facet))
                    .findFirst()
                    .ifPresent(option -> option
                            .findElement(By.cssSelector(".facet-option__checkbox"))
                            .click());
            /*
             * sometimes the elements in this class go state. In that case catch it and
             * recursively try again
             */
        } catch (StaleElementReferenceException e) {
            if (localRetries <= 0) {
                throw new ElementInteractionException(
                        "SELECT FILTER OPTION: stale element detected after 3 retries" + e.getMessage(), e);
            }

            log.warn("selectFilterOption: stale element exception caught. Recursively Retries left #" + localRetries);
            selectFilterOption(facet, localRetries - 1);
        }
    }

    /**
     * Close the the filter by moving the close button into view, then clicking the
     * close button.
     */
    private void closeFilter() {
        try {
            /*
             * using JS because Firefox does not scroll the close button into view (even
             * with Actions)
             */
            scrollIntoView(closeButton);

            /*
             * wait for, scroll element into view, and then close the filter
             */
            waitForVisibilityOf(closeButton);
            waitForTextToBe(closeButton, "SCHLIESSEN");

            /* wait for the element to be clickable */
            waitForElementToBeClickable(closeButton);
            /* click the close button */
            closeButton.click();
        } catch (Exception ex) {
            /*
             * sometimes the close button is not visible so we print the ex and continue.
             * The close button does not really need to be clicked so we can just print the
             * exception and continue. The exception won't be rethrown.
             */
            log.warn("CLOSE BUTTON: there was an issue with the close button, ignoring and continuing: ");
        }
    }

    /**
     * Fetches all the product elements on the current web page identified by the
     * class name "product-tile".
     *
     * @return List<WebElement> - a list of WebElements representing the products.
     */
    public List<WebElement> getProducts() {
        waitForElementLocated(By.className("product-tile"));
        return driver.findElements(By.className("product-tile"));
    }

    /**
     * Fetches and parses the HTML document of a given product page URL.
     * Opens a new tab in the browser and then retrieves the page source (HTML
     * content).
     * The HTML is then parsed into a Jsoup Document object which is returned.
     *
     * @param productPageUrl The URL of the product page to fetch and parse.
     * @return A Jsoup Document object containing the parsed HTML
     */
    public Document getDocument(WebElement product, String productPageUrl) {
        Document document = null;

        try {
            /*
             * get the Document object of the product page by opening a new tab in the
             * browser instance, switching to it, and then closing it
             */

            // Store the original tab's handle
            String originalTab = driver.getWindowHandle();

            // Open a new tab
            ((JavascriptExecutor) driver).executeScript("window.open()");

            // Switch to the new tab
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            driver.switchTo().window(tabs.get(1)); // assumes only one new tab is opened

            // Navigate to the URL in the new tab
            driver.get(productPageUrl);

            // Get the page source from the new tab
            String pageSource = driver.getPageSource();

            // Parse the page source with Jsoup to create a Document object
            document = Jsoup.parse(pageSource);

            // Close the new tab and switch back to the original tab
            driver.close();
            driver.switchTo().window(originalTab);
        } catch (Exception e) {
            log.error("JSOUP: there was an error fetching the document: " + e.getMessage(), e);
            if (productPageUrl != null) {
                Assert.fail("Failed to retrieve product page via HTTP request for " + getBrand(product) + " "
                        + productPageUrl);
            }
            Assert.fail("Item" + " does not have a product URL" + getBrand(product));
        }

        /* Assert fail in case the document is null */
        if (document == null) {
            Assert.fail("Document is null for the product: " + getBrand(product) + " " + productPageUrl);
        }

        return document;
    }

    /**
     * Retrieves the highlights associated with a given product WebElement from the
     * search listings page. This will grab the NEU and SALE elements.
     * 
     * Highlights are fetched as a list of text from elements matched by a specific
     * CSS selector and then joined into a single string.
     *
     * @param product The WebElement representing the product whose highlights are
     *                to be retrieved.
     * @return A string containing the concatenated highlights, separated by spaces.
     */
    public String getHighlights(WebElement product) {
        // SALE is always .eyecatcher--discount
        // NEU is .eyecatcher--new
        List<String> items = product.findElements(By.cssSelector(".eyecatcher span")).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        return String.join(" ", items);
    }

    public boolean isSaleHighlightDisplayed(WebElement product) {
        waitForVisibilityOf(product);
        return product.findElement(By.cssSelector(".eyecatcher--discount")).isDisplayed();
    }

    public boolean isNeuHighlightDisplayed(WebElement product) {
        waitForVisibilityOf(product);
        return product.findElement(By.cssSelector(".eyecatcher--new")).isDisplayed();
    }

    /**
     * Retrieves the Produktart associated with a given product
     * WebElement.
     *
     * @param product The WebElement representing the product whose produktart is to
     *                be retrieved.
     * @return A string containing the product type, or an empty string if the
     *         element is not found.
     */
    public String getProduktart(WebElement product) {
        try {
            return product.findElement(By.className("category")).getText();
        } catch (NoSuchElementException ex) {
            log.warn("getProkuktart: can a produktart entry from this product element");
            return "";
        }
    }

    /**
     * Retrieves the marke associated with a given product WebElement.
     *
     * @param product The WebElement representing the product whose produktart is to
     *                be retrieved.
     * @return A string containing the produktart, or an empty string if the
     *         element is not found.
     */
    public String getMarke(WebElement product) {
        try {
            return product.findElement(By.className("top-brand")).getText();
        } catch (NoSuchElementException ex) {
            log.warn("getMarke: can a marke entry from this product element");
            return "";
        }
    }

    /**
     * Retrieves the value of Geschenk Für from a parsed HTML document.
     *
     * @param document The Jsoup Document object containing the parsed HTML.
     * @return A string containing the Geschenk Für value, "null" if the span is
     *         empty, or "Not Found" if no relevant elements are present.
     */
    public String getGeschenkFur(Document document) {
        Elements elements = document.select(".classification");
        return elements.stream()
                .filter(element -> {
                    Element firstSpan = element.selectFirst("span:nth-child(1)");
                    return firstSpan != null && firstSpan.text().equalsIgnoreCase("Geschenk Für");
                })
                .map(element -> {
                    Element secondSpan = element.selectFirst("span:nth-child(2)");
                    return secondSpan != null ? secondSpan.text() : "null";
                })
                .findFirst()
                .orElse("Not Found");
    }

    /**
     * Get the href attribute from the WebElement product
     * 
     * @param product the WebElement from which to get the href attribute
     */
    public String getProductLink(WebElement product, int retries) {
        if (retries <= 0) {
            return null;
        }

        try {
            return product.findElement(By.cssSelector(".product-tile .link")).getAttribute("href");
        } catch (StaleElementReferenceException e) {
            log.info("getProductLink: caught stale element exception, retrying using generics #" + retries);
            return getProductLink(product, retries - 1);
        }

    }

    /**
     * Check if a product contains the out of stock element
     * 
     * @param product the product WebElement to check
     * @return a boolean whether it is in stock or not
     */
    public boolean isOutOfStock(WebElement product) {
        try {
            return product.findElement(By.cssSelector(".out-of-stock")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /* internal util method to grab the product name */
    private String getBrand(WebElement product) {
        return product.findElement(By.cssSelector(".name")).getText();
    }
}
