# overview

A Selenium TestNG Automation framework that asserts product relevancy of filtered seach results.

# commands to run tests

- mvn test -Pregression -Dbrowser=chrome
- mvn test -Pregression -Dbrowser="chrome headless"
- mvn test -Pregression -Dbrowser=firefox
- mvn test -Pregression -Dbrowser="firefox headless"
- mvn test -Pregression -Dbrowser=edge
- mvn test -Pregression -Dbrowser="edge headless"

System.setProperty("hudson.model.DirectoryBrowserSupport.CSP", "")

# framework will fail a test if:

- highlight, marke, produktart, furWen does not match a product listing
- a product url is not accessible (404s, etc)
- out of stock items show up in search results but are ignored since they lack tags to indicate filter relevance

# features

- Runs in Chrome, Firefox, and Edge (including headless mode)
- JenkinsL: nightly scheduled runs for Chrome, Firefox, and Edge (all headless)
- single @Test case 'testProductFilters' in the 'ProductFilterTest' class in the 'tests' folder covers project requirements.
- failed test cases will
  - Extent Reports: contain a screenshot from the parfum page results with the product that failed the test in viewport.
  - Extent Reports: log browser name used
  - Extent Reports: log the product URL that is causing the failure
  - retry a specific number of times using IRetryAnalyzer to make sure we avoid flaky fails
- Logging with Log4J to file and SDOUT

# Best Practices for automation

- classes are thread safe using ThreadLocal and ThreadGuard enabling TestNG parellel execution
- Page Object Model strategy used
- test case data populated via Apache POI (external Excel file)
- JSoup used to query data from individual product pages instead of a POM class in order to speed up execution and since we are not intending to interact with product page UI
- Jenkins used to schedule cross-browser test runs
- Strategic recursive POM methods to combat flaky UI (e.g. the setFilter method and the handleModal methods will retry if something causes them flak. Mostly happens with setFilter. Sometimes the cookie modal on the homepage flaks due to Douglas anti-scrape or rate limit protection).

# Reporting

- recommending Extent reports
  - intuitive display
  - easily customizable
  - easy integration into our test case
  - easy screenshot integration for failed test cases

# TODO

- finish ReadMe
