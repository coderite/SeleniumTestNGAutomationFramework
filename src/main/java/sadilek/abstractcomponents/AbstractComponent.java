package sadilek.abstractcomponents;

import java.time.Duration;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Contains all common components that can be reused in page objects.
 */
public class AbstractComponent {
    private WebDriver driver;
    private int timeoutInSeconds = 10;
    private Logger log = LogManager.getLogger("AbstractComponent.class");

    /**
     * Constructor that instantiates the AbstractComponent.
     * 
     * @param driver           the WebDriver instance
     * @param timeoutInSeconds the amount of time Webdriver Wait waits in
     *                         seconds.
     */
    public AbstractComponent(WebDriver driver, int timeoutInSeconds) {
        this.driver = driver;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    /**
     * Wrapper that waits for presenceOfElementLocated.
     * 
     * @param locator the By locator used to identify an element on a page.
     */
    protected void waitForElementLocated(By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            log.error("TIMEOUT waitForElementLocatedBy: " + locator.toString() + e);
        }
    }

    protected String getSelectedFacets() {
        return driver.findElements(By.cssSelector(".selected-facets a"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(" "));
    }

    /**
     * Waits until a facet filter becomes enabled, based on the provided payload.
     * 
     * @param facet the facet to check and wait for
     */
    protected void waitForFilterToBeEnabled(String facet) {
        /*
         * add logic to loop .selected-facets to check wait before continuing until
         * selected facet is enabled
         */
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        ExpectedCondition<Boolean> elementTextContainsFacet = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return driver.findElements(By.cssSelector(".selected-facets a"))
                        .stream()
                        .anyMatch(filter -> filter.getText().equalsIgnoreCase(facet));
            }
        };
        wait.until(elementTextContainsFacet);
    }

    public void scrollIntoView(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("arguments[0].scrollIntoView(false);", element);
        } catch (StaleElementReferenceException e) {
            /* log it but don't rethrow the error to continue execution */
            log.error("scroll into view issue");
        }
    }

    /**
     * Wrapper that wait for the visibility of a WebElement.
     * 
     * @param element the WebElement to wait for.
     */
    public void waitForVisibilityOf(WebElement element) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException ex) {
            log.error("TIMEOUT waitForVisibilityOf element: " + element.getText() + ex);
            throw ex;
        }
    }

    /**
     * Wrapper that waits for a WebElement to be clickable.
     * 
     * @param element the WebElement to wait for.
     */
    protected void waitForElementToBeClickable(WebElement element) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (TimeoutException ex) {
            log.error("TIMEOUT waitForElementToBeClickable: " + element.getText() + ex);
            throw ex;
        } catch (Exception e) {
            log.error("WAIT FOR ELEMENT TO BE CLICKABLE ERROR!");
        }
    }

    /**
     * Wait for the number of filters to be more than zero. Mostly for Firefox to
     * prevent the test continuing with 0 filters selected.
     */
    protected void waitForNumberOfFiltersToNotBeZero() {
        By locator = By.cssSelector(".facet");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, 0));
        } catch (TimeoutException ex) {
            log.error("TIMEMOUT Number of elements is 0");
            throw ex;
        }
    }

    protected void waitForTextToBe(WebElement element, String expectedText) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.textToBePresentInElement(element, expectedText));
        } catch (TimeoutException ex) {
            log.error("TIMEOUT waitForTextToBe: " + element.getText() + ex);
            throw ex;
        }

    }

    /**
     * Refreshes the current page.
     */
    protected void refreshPage() {
        driver.navigate().refresh();
    }

    /**
     * Navigates to a specified URL.
     * 
     * @param url the URL to open in the browser.
     */
    protected void gotoPage(String url) {
        if (isEmptyString(url)) {
            log.info("URL is empty or null");
            return;
        }

        driver.get(url);
    }

    /**
     * ACTIONS
     * Moves to a WebElement and clicks it.
     * 
     * @param element the WebEelment to move the action to.
     */
    protected void actionMoveToElementAndClick(WebElement element) {
        if (element != null) {
            Actions action = new Actions(driver);
            action.moveToElement(element).click().build().perform();
        } else {
            log.info("Action not move to and click an element that is null.");
        }

    }

    /**
     * ACTIONS
     * Moves to a WebElement without clicking it.
     * 
     * @param element the WebEelment to move the action to.
     */
    public void actionMoveToElement(WebElement element) {
        if (element != null) {
            Actions action = new Actions(driver);
            action.moveToElement(element).build().perform();
        } else {
            log.info("Action can not move to null element.");
        }
    }

    /**
     * HELPER FUNCTIONS
     * 
     * @param input the string to check whether it's empty or not
     * @return Boolean whether the input string is empty or not.
     */
    protected boolean isEmptyString(String input) {
        return input == null || input.isEmpty();
    }
}
