package sadilek.testcomponents;

import java.util.HashMap;
import java.util.Map;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * The Retry class enables a test to be retried if it fails.
 * Implements IRetryAnalyzer that works nicely with TestNG
 */
public class Retry extends BaseTest implements IRetryAnalyzer {
    private Map<String, Integer> retryCountMap = new HashMap<>();

    // private static int count = 0;
    /* the number of times a test should be retried if it fails */
    private int maxRetry;

    /**
     * Set the max retries in the constructor so that it is only called once.
     */
    public Retry() {
        try {
            this.maxRetry = Integer.parseInt(getProperty("iRetry"));
        } catch (Exception e) {
            log.info("iRetry error " + e.getMessage(), e);
            this.maxRetry = 3;
        }
    }

    /**
     * Retry is called each time a test fails.
     * Checks the maxEntry variable and let's the IRetryAnalyzer know where it
     * shoudl continue to retry or whether to fail the test case.
     */
    @Override
    public boolean retry(ITestResult result) {
        Object[] parameters = result.getParameters();
        String methodName = result.getMethod().getMethodName();
        String key = methodName;

        if (parameters != null && parameters.length > 0) {
            for (Object param : parameters) {
                if (param != null) {
                    key += param.toString();
                }
            }
        }

        int currentCount = retryCountMap.getOrDefault(key, 0);

        if (currentCount < maxRetry) {
            retryCountMap.put(key, currentCount + 1);
            return true;
        }
        return false;
    }
}
