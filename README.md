# overview

A Selenium TestNG Automation framework that asserts product relevancy of filtered seach results.

# commands to run tests

There are 2 ways to run tests.

1. Jenkins UI
2. CLI with the below Maven test commands.

examples:
The test runs with the below Maven command. There is no need to set all available variables unless customization is wanted.

- mvn test -Pregression -Dbrowser=chrome
- mvn test -Pregression -Dbrowser="chrome headless"

There is a settings.properties file with defaults for the below settings.

All variables that can be set:

- **browser** (chrome headless, firefox headless, edge headless, chrome, firefox, edge)
- **url** (the base entry URL)
- **dataProvider** (relative path to the Excel file containing the test case data) default = "dataxls/testcases.xlsx"
- **retries** (number of retries for recursive methods ) default = 3
- **iRetry** (number of times a failed test case is retried before reporting it as a fail) default = 3
- **timeout** (timeout in seconds for waits) default = 20

# features

- Runs in Chrome, Firefox, and Edge (including headless mode)
- uses TestNG as the underlying testing framework
- Jenkins: nightly scheduled runs for Chrome, Firefox, and Edge (all headless)
- single @Test case 'testProductFilters' in the 'ProductFilterTest' class in the 'tests' folder covers project requirements.
- failed test cases will
  - Extent Reports: contain a screenshot from the parfum page results with the product that failed the test in viewport.
  - Extent Reports: log browser name used
  - Extent Reports: log the product URL that is causing the failure and the reason it failed
  - retry a specific number of times using IRetryAnalyzer to make sure we avoid flaky fails
- Logging with Log4J to file and SDOUT
- customizable settings in the settings.properties file which can also be set via the CLI or Jenkins parameters.
- Extent Reports accessible via Jenkins artifact archives using dynamic folders based on build numbers.
- highlight(sale, neu), marke, produktart data points are parsed directly from the product listing on the parfum page filtered results.
- highlight(limitier) and geschenk fur data points are parsed from the product page by grabbing the page source after opening the product page in a new tab.

# framework will fail a test if:

- highlight, marke, produktart, geschenk fur does not match a product listing
- a product url is not accessible (404s, etc)
- out of stock items show up in search results but are ignored since they lack tags to indicate filter relevance
- if the actual filters set does not match the expected filters set based on the data from the test case xlsx

# Best Practices for automation

- Parallel test runs via TestNG. Set data-provider-thread-count to 3 threads at the suite level but tested with up to 8 successfully.
- classes are thread safe using ThreadLocal and ThreadGuard enabling TestNG parellel execution
- Page Object Model strategy used
- test case data populated via Apache POI (external Excel file)
- Jenkins used to schedule cross-browser test runs. 3 jobs are set up for Chrome, Firefox, and Edge. These can run on any schedule required. All reports resulting from that can be viewed from within the Jenkins UI.
- Strategic recursive POM methods to combat flaky UI (e.g. the setFilter method method will retry if something causes them flak. Mostly happens with setFilter. Sometimes the cookie modal on the homepage flaks due to Douglas anti-scrape or rate limit protection but only after a heavy load of parallel job runs).
- Page source data is pulled from product pages by opening product pages in new tabs and then closing them. This was done to avoid having to re-instantiate WebDriver instances.Page source is to used to query data from individual product pages for the limitier and geschenk fur data points. Used this approach instead of a POM class in order to speed up execution and since we are not intending to interact with product page UI.
- To speed up tests, only the first page of products are parsed for relevancy. Reasons are that most test cases result in only 1 page of results. Additional pagination support can be added back in if required.

# Reporting

- recommending Extent reports as the reporting mechanism
  - intuitive display
  - easily customizable
  - easy integration into our test case
  - easy screenshot integration for failed test cases

# Limitations

- the fur wen filter is set but product listings are not validated for the fur wen filter since no relevant (fur wen) data points to validate were found on the product page.
