package sadilek.resources;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * A util class that enables screenshots to be taken. Intended to be used from
 * within the Listeners class when a test case fails.
 */
public class ScreenshotTaker {
    private WebDriver driver;
    private Logger log = LogManager.getLogger(ScreenshotTaker.class);

    /**
     * Constructor to set up and ready the screenshot class.
     * 
     * @param driver the WebDriver instance to operate on.
     */
    public ScreenshotTaker(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Takes a screenshot and returns a string its path
     * Stores screenshots in a the reports/screenshots folder.
     * 
     * @param testCase the name of the testcase to be used in the filename.
     */
    public String getScreenshot(String testCase) throws IOException {
        log.info("DEBUG: getScreenshot called for " + testCase);
        TakesScreenshot ts = (TakesScreenshot) driver;
        if (ts == null)
            return "";

        /*
         * the JENKINS build number. (Jenkins sets BUILD_NUMBER env var) Will use
         * 'local' if run without Jenkins
         */
        String buildNumber = System.getenv("BUILD_NUMBER") != null ? System.getenv("BUILD_NUMBER") : "local";
        long currentTimeMillis = System.currentTimeMillis();

        /* we need an absolute path for the file of the screenshot */
        String screenshotPathForFile = "reports/build_" + buildNumber + "/screenshots/"
                + testCase
                + "_"
                + currentTimeMillis
                + ".png";

        /* we need to return a relative file path for extent reports */
        String relativeScreenshotPath = "screenshots/"
                + testCase
                + "_"
                + currentTimeMillis
                + ".png";

        File source = ts.getScreenshotAs(OutputType.FILE);
        File file = new File(screenshotPathForFile);
        FileUtils.copyFile(source, file);
        return relativeScreenshotPath;
    }
}
