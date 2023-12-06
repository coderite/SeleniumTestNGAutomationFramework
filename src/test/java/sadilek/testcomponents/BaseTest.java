package sadilek.testcomponents;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ThreadGuard;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * BaseTest class serving as the foundation for all test classes.
 * 
 * This class initializes and manages WebDriver instances in a ThreadLocal
 * storage for thread safety during parallel execution.
 */
public class BaseTest {
    protected ThreadLocal<WebDriver> driver = new ThreadLocal<>(); // make the driver instance thread safe.
    protected Properties properties;
    protected static Logger log = LogManager.getLogger(BaseTest.class);

    /**
     * Retrieves the WebDriver instance associated with the current thread.
     * 
     * Uses ThreadLocal storage to ensure that each thread
     * has its own isolated WebDriver instance.
     *
     * @return The WebDriver instance associated with the current thread.
     */
    public WebDriver getDriver() {
        return driver.get();
    }

    /**
     * A utility method that gets a property from the command line and otherwise
     * from the settings.properties file.
     * 
     * @param property the key to search the CLI or settings.properties file for
     * @return the string value of the property.
     * @throws Exception if things go wrong propogate the exception
     */
    public String getProperty(String property) throws Exception {
        /*
         * sometimes the Retry class gets called before the loadProperties has run. To
         * fix that we null check and manually call loadProperties
         */
        if (properties == null) {
            log.warn("properties were not loaded. Loading now");
            loadProperties();
            // throw new Exception("Properties is not not initialized");
        }

        /*
         * check if a property has been set on the command line invocation and otherwise
         * get it from the settings.properties file
         */
        return System.getProperty(property) != null ? System.getProperty(property)
                : properties.getProperty(property);
    }

    /**
     * Closes the browser and removes the driver instance from the ThreadLocal
     * storage.
     */
    private void closeBrowser() {
        getDriver().quit();
        driver.remove();
    }

    /**
     * Loads properties from a settings file located in the resources folder.
     * This method is annotated with @BeforeSuite, so it will be executed before
     * any test suites run.
     * 
     * @throws IOException if there is an issue reading the properties file.
     */
    @BeforeSuite
    public void loadProperties() throws IOException {
        /*
         * init a property file in the resources folder where we can store global
         * variables
         */
        try {
            FileInputStream fis = new FileInputStream(
                    System.getProperty("user.dir") + "/src/main/java/sadilek/resources/settings.properties");
            properties = new Properties();
            properties.load(fis);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * set the context attribute so we can access it easily in the Listeners class
     * we pass the driver so that we have a threadsafe instance during parallel
     * execution in the Listerer class for the purpose of taking screenshots during
     * Assert fails.
     */
    @BeforeMethod
    public void setContext(ITestContext context) {
        context.setAttribute("driver", getDriver());
    }

    @BeforeSuite
    public void setContextBeforeSuite(ITestContext context) {
        String browserName;
        try {
            browserName = getProperty("browser");
            context.setAttribute("browserName", browserName);
        } catch (Exception e) {
            log.error("setContext: could not get the browserName");
            e.printStackTrace();
        }
    }

    /**
     * This method will run before each @Test method is run in the test folder.
     * setup the WebDriver and return the main landing page for Douglas
     * 
     * @return the WebDriver driver after it has been set up.
     * @throws IOException
     */
    @BeforeMethod
    public void initDriver() throws IOException, Exception {
        log.info("Before Method Thread Number: " + Thread.currentThread().getId());

        /*
         * if the browser variable is being set using the MVN command, use that.
         * Otherwise, get the browser value from the settings.properties value
         */
        String browserName = getProperty("browser");

        /*
         * select the driver based on the browser variable in the properties file.
         */
        if (browserName.contains("chrome")) {
            WebDriverManager.chromedriver().setup();

            /*
             * enable headless browsing if invoke with the 'headless' value
             * Return the driver to avoid instability due to the driver manage maximize
             * method being called after setSize.
             */
            ChromeOptions options = new ChromeOptions();
            if (browserName.contains("headless")) {
                options.addArguments("headless");
                driver.set(ThreadGuard.protect(new ChromeDriver(options)));
                driver.get().manage().window().setSize(new Dimension(1800, 1000)); // resize the window
                                                                                   // for headless
            } else {
                driver.set(ThreadGuard.protect(new ChromeDriver()));
                /* maximize the window so that elements get maximum visibilty */
                driver.get().manage().window().maximize();
            }

        } else if (browserName.contains("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            if (browserName.contains("headless")) {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("-headless");
                driver.set(ThreadGuard.protect(new FirefoxDriver(options)));
                driver.get().manage().window().setSize(new Dimension(1900, 1200)); // resize the window
            } else {
                driver.set(ThreadGuard.protect(new FirefoxDriver()));
                /* maximize the window so that elements get maximum visibilty */
                driver.get().manage().window().maximize();
            }
        } else if (browserName.contains("edge")) {
            WebDriverManager.edgedriver().setup();
            if (browserName.contains("headless")) {
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--headless");
                driver.set(ThreadGuard.protect(new EdgeDriver(options)));
                driver.get().manage().window().setSize(new Dimension(1900, 1200));
            } else {
                driver.set(ThreadGuard.protect(new EdgeDriver()));
                /* maximize the window so that elements get maximum visibilty */
                driver.get().manage().window().maximize();
            }
        }
    }

    /*
     * Runs after each test ensuring that the Web Driver and the browser are closed.
     */
    @AfterMethod
    public void teardown() {
        log.info("After method thread ID: " + Thread.currentThread().getId());
        closeBrowser();
    }
}
