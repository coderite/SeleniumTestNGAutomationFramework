package sadilek.resources;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * ExtentReporterNG class provides utility for generating ExtentReports.
 * 
 * This class provides a method for initializing and configuring the
 * ExtentReports object. It also generates unique filenames for each report
 * based on the current date and time.
 * 
 */
public class ExtentReporterNG {
    /**
     * Returns an ExtentReports object configured with a unique file path.
     *
     * @return ExtentReports object ready for use.
     */
    public static ExtentReports getReportObject() {
        /*
         * Get the current date and time using SimpleDateFormat.
         * The date-time pattern "yyyy-MM-dd_HH-mm-ss" ensures that the filenames
         * are both unique and human-readable.
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateAndTime = sdf.format(new Date());

        /*
         * the JENKINS build number. (Jenkins sets BUILD_NUMBER env var) Will use
         * 'local' if run without Jenkins
         */
        String buildNumber = System.getenv("BUILD_NUMBER") != null ? System.getenv("BUILD_NUMBER") : "local";

        /*
         * Create a unique filename by appending the current date and time to the
         * base file path.
         */
        String path = System.getProperty("user.dir") + "/reports/build_" + buildNumber + "/" + currentDateAndTime
                + ".html";

        /* set up the Extent Report requirements */
        ExtentSparkReporter reporter = new ExtentSparkReporter(path);
        reporter.config().setReportName("Douglas Automation Results");
        reporter.config().setDocumentTitle("Test Results");

        /* set up and return the actual Extent Reports */
        ExtentReports extent = new ExtentReports();
        extent.attachReporter(reporter);
        extent.setSystemInfo("Tester", "Lucas Sadilek");
        return extent;
    }
}
