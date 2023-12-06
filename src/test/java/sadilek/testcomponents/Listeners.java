package sadilek.testcomponents;

import java.util.HashMap;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import sadilek.resources.ExtentReporterNG;
import sadilek.resources.ScreenshotTaker;

/**
 * This class is responsible for listening to test events and logging them using
 * Extent Reports.
 */
public class Listeners implements ITestListener {
    private Logger log = LogManager.getLogger(Listeners.class);

    ExtentTest test;
    ExtentReports extent;
    ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        String browserName = (String) context.getAttribute("browserName");
        extent = ExtentReporterNG.getReportObject(browserName);
    }

    /**
     * When a test fails log the the results to Extent Reports
     * 
     * @params ITestResult contains the information from the failed test class.
     */
    @Override
    public void onTestFailure(ITestResult result) {
        ITestContext context = result.getTestContext();
        WebDriver threadSafeDriver = ((BaseTest) result.getInstance()).getDriver();

        /* Capture the failure reason so we can process the response */
        Throwable failureReason = result.getThrowable();

        log.info("failure message: " + failureReason.getMessage());
        log.info("throwable: " + failureReason);

        /*
         * Take a screenshot using our utility class from the utils folder.
         */
        ScreenshotTaker screenshotTaker = new ScreenshotTaker(threadSafeDriver);
        String browser = (String) context.getAttribute("browserName");
        screenshotTaker.setBrowser(browser);

        String filePath = null;
        try {
            filePath = screenshotTaker.getScreenshot(result.getMethod().getMethodName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        extentTest.get().addScreenCaptureFromPath(filePath, result.getMethod().getMethodName());
        log.info("DEBUG: added screenshot: " + filePath);

        extentTest.get().log(Status.FAIL, "FAILED");
        extentTest.get().fail(failureReason.getMessage());
        extentTest.get().fail(result.getThrowable());

        log.info("TEST CASED FAILED: " + result.getMethod().getMethodName());
    }

    /**
     * When a test skips log the the results to Extent Reports
     * 
     * @params ITestResult contains the information from the skipped test class.
     */
    @Override
    public void onTestSkipped(ITestResult result) {
        extentTest.get().log(Status.SKIP, "SKIPPED");
        extentTest.get().log(Status.SKIP, result.getThrowable().getMessage());
        extentTest.get().log(Status.SKIP, result.getThrowable());

        log.info("TEST CASE SKIPPED" + result.getMethod().getMethodName());
    }

    /**
     * When a test starts create a new Extent Report test.
     * 
     * @params ITestResult contains the information from the started test class.
     */
    @Override
    public void onTestStart(ITestResult result) {
        ITestContext context = result.getTestContext();
        String browserName = (String) context.getAttribute("browserName");

        /*
         * Retrieve the produktart, marke, highlight, etc variables from the test
         * method and use them to create test titles in Extent Report.
         */
        Object[] parameters = result.getParameters();
        StringBuilder testCaseName = new StringBuilder();
        testCaseName.append(browserName);
        if (parameters != null && parameters.length > 0 && parameters[0] instanceof HashMap) {
            @SuppressWarnings("unchecked") // Safe type-check was done using instanceof
            HashMap<String, String> testParameters = (HashMap<String, String>) parameters[0];

            /*
             * build the string containing the paramteres so we can inject them into the
             * Extent Report title
             */
            int count = 1;
            testCaseName.append("[");
            for (String key : testParameters.keySet()) {
                if (testParameters.get(key) != null && !testParameters.get(key).isEmpty()) {
                    if (count > 1) {
                        testCaseName.append(", ");
                    }
                    testCaseName.append(testParameters.get(key));
                }
                count++;
            }
            testCaseName.append("]");
        }
        log.info("TEST CASE STARTED : Thread #" + Thread.currentThread().getId() + " "
                + result.getMethod().getMethodName() + " : " + testCaseName.toString());

        /* set up an Extent Report instance using the method name */
        test = extent.createTest(result.getMethod().getMethodName() + " : " + testCaseName.toString());

        /* assign the test to a thread-local instance to enable parallel execution */
        extentTest.set(test);

        /* log the browser being used in Extent Reports */
        extentTest.get().log(Status.INFO, "browser used: " + browserName);
    }

    /**
     * When a test is successful log the results to Extent Reports
     * 
     * @params ITestResult contains the information from the successful test class.
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("TEST CASE SUCESS: " + result.getMethod().getMethodName());
        extentTest.get().log(Status.PASS, "PASSED");
    }

    /**
     * When a test skips finishes log the results to Extent Reports
     * 
     * @params ITestResult contains the information from the finished test class.
     */
    @Override
    public void onFinish(ITestContext context) {
        log.info("TEST FINISHED ");

        /* flush the Extent instance otherwise it won't write to file */
        extent.flush();
    }
}
