package sadilek.pageobjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import sadilek.abstractcomponents.AbstractComponent;
import sadilek.exceptions.ElementInteractionException;

/**
 * LandingPage represents the landing page of Douglas.de using Page Object
 * notation.
 * 
 * It inherits common functions from the AbstractComponent class that are useful
 * for the other pages as well.
 * 
 * This class handles the cookie modal popup and navigates to the
 * Parfum Page.
 * 
 */
public class LandingPage extends AbstractComponent {
    private WebDriver driver;
    private Logger log = LogManager.getLogger(LandingPage.class);
    private int timeoutInSeconds;
    private int retries;

    /**
     * LandingPage constructor
     * 
     * @param driver           the WebDriver instance we are targetting
     * @param timeoutInSeconds sets the timemout in seconds for any WebDriverWait
     *                         scenarios
     */
    public LandingPage(WebDriver driver, int timeoutInSeconds, int retries) {
        super(driver, timeoutInSeconds);
        this.driver = driver;
        this.timeoutInSeconds = timeoutInSeconds;
        this.retries = retries;

        /* init the Page Factory WebElements */
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".modal-overlay__display")
    private WebElement modal;

    @FindBy(css = ".button.button__primary.uc-list-button__accept-all")
    private WebElement closeButton;

    @FindBy(css = ".navigation-main-entry a[href='/de/c/parfum/01']")
    private WebElement parfumLink;

    @FindBy(css = ".header-component__container")
    private WebElement douglasHeader;

    /**
     * Handles the modal popup on the landing page.
     * 
     * To abstract the retry count this method wraps the an overloaded version of
     * the handlemodalpopup.
     * If the modal is not found or not interactable, retries recursively
     * up to the maximum number of retries set in the constructor.
     * 
     * @throws ElementInteractionException If the maximum number of retries is
     *                                     reached
     *                                     and the modal is still not found.
     */
    public void handleModalPopup() {
        handleModalPopup(this.retries);
    }

    /**
     * 
     * wait for, then click to close the Douglas modal that appears on a new browser
     * session. Retry recursively after refreshing page in case of exceptions.
     * Needed due to Douglas sometime denying access probably as a result of too
     * much high volumn parallel execution.
     * 
     * @param retries max number of times to recursively retry method
     */
    public void handleModalPopup(int localRetries) {
        try {
            /* handle the cookie modal that first appears */
            waitForVisibilityOf(modal);
            closeButton.click();
        } catch (TimeoutException | ElementInteractionException | NoSuchElementException
                | StaleElementReferenceException | ElementNotInteractableException e) {

            /* only throw an exception if max retries has diminshed to 0 */
            if (localRetries <= 0) {
                log.error("Modal not found: " + e.getMessage(), e);
                throw new ElementInteractionException(
                        "MODAL: not found! (probably ACCESS DENIED by Douglas)" + e.getMessage(), e);
            }

            /* handle the recursion */
            log.warn(
                    "HANDLEMODALPOPUP: known exception thrown (likely ACCESS DENIED by Douglas) retrying method recursively. Retries left: #"
                            + retries);
            refreshPage();
            handleModalPopup(localRetries - 1);
        }

        /*
         * must refresh the page after closing the modal otherwise modal will block
         * Parfum link click on Firefox
         */
        refreshPage();
    }

    /**
     * Overloaded method to ensure we can do recursive calls while abstracting away
     * the entries variable for recursion.
     * 
     * @return ParfumPage instance
     */
    public ParfumPage gotoParfumPage() {
        return gotoParfumPage(this.retries);
    }

    /**
     * Handle the parfum link to navigate to the product listings page.
     * Recursive method that retries (N number of times) method with a page reload
     * in case of exceptions.
     * 
     * @return a ParfumPage Page Object Notation instance
     * @throws ElementInteractionException proprogates exceptions to the upper
     *                                     classes.
     */
    public ParfumPage gotoParfumPage(int localRetries) throws ElementInteractionException {
        try {
            /* wait for the parfum link to be clickable */
            waitForElementToBeClickable(parfumLink);

            /*
             * simulate a mouse moving to the link to click the button. Otherwise we might
             * see an overlay blocking the test
             */
            actionMoveToElementAndClick(parfumLink);

            /*
             * move to another element to prevent the main nav Parfum dropdown remaining
             * visible and blocking interaction with the facets underneath
             */
            actionMoveToElement(douglasHeader);
        } catch (TimeoutException | ElementInteractionException | NoSuchElementException
                | StaleElementReferenceException | ElementNotInteractableException e) {
            log.warn("GOTO PARFUMAGE: known exception caught, retrying method recursively. Retries left: #" + retries);
            refreshPage();
            gotoParfumPage(localRetries - 1);

            if (localRetries <= 0) {
                log.error("GOTO PARFUMPAGE: known exception interacting with element!" + e.getMessage(), e);
                throw new ElementInteractionException("GOTO PARFUMPAGE: element not found!", e);
            }
        }

        return new ParfumPage(driver, timeoutInSeconds, retries);
    }

    /**
     * Navigate to the URL of the landing page
     * 
     * @param url the URL of the landing page
     */
    public void gotoPage(String url) {
        driver.get(url);
    }
}
