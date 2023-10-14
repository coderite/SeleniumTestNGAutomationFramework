package sadilek.resources;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * A util class that enables screenshots to be taken. Intended to be used from
 * within the Listeners class when a test case fails.
 */
public class ScreenshotTaker {
    private WebDriver driver;

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
        TakesScreenshot ts = (TakesScreenshot) driver;
        if (ts == null)
            return "";

        long currentTimeMillis = System.currentTimeMillis();

        File source = ts.getScreenshotAs(OutputType.FILE);
        File file = new File(
                System.getProperty("user.dir") + "/reports/screenshots/" + testCase + "_" + currentTimeMillis
                        + ".png");
        FileUtils.copyFile(source, file);
        return System.getProperty("user.dir") + "/reports/screenshots/" + testCase + "_" + currentTimeMillis
                + ".png";
    }
}
