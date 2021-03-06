package base;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import reporting.ExtentManager;
import reporting.ExtentTestManager;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommonAPI {

    //**********************WebDriver Instance******************************//
    public static WebDriver driver = null;

    public String browserstack_username= "";
    public String browserstack_accesskey = "";
    public String saucelabs_username = "";
    public String saucelabs_accesskey = "";

    //***********************Extent Report Listener***********************//
    public static ExtentReports extent;
    @BeforeSuite
    public void extentSetup(ITestContext context) {
        ExtentManager.setOutputDirectory(context);
        extent = ExtentManager.getInstance();
    }
    @BeforeMethod
    public void startExtent(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName().toLowerCase();
        ExtentTestManager.startTest(method.getName());
        ExtentTestManager.getTest().assignCategory(className);
    }
    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
    @AfterMethod
    public void afterEachTestMethod(ITestResult result) {
        ExtentTestManager.getTest().getTest().setStartedTime(getTime(result.getStartMillis()));
        ExtentTestManager.getTest().getTest().setEndedTime(getTime(result.getEndMillis()));

        for (String group : result.getMethod().getGroups()) {
            ExtentTestManager.getTest().assignCategory(group);
        }

        if (result.getStatus() == 1) {
            ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed");
        } else if (result.getStatus() == 2) {
            ExtentTestManager.getTest().log(LogStatus.FAIL, getStackTrace(result.getThrowable()));
        } else if (result.getStatus() == 3) {
            ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped");
        }
        ExtentTestManager.endTest();
        extent.flush();
        if (result.getStatus() == ITestResult.FAILURE) {
            captureScreenshot(driver, result.getName());
        }
        driver.quit();
    }
    @AfterSuite
    public void generateReport() {
        extent.close();
    }
    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    //***************Browsers Local and  Cloud and OS Configuration***************//
    @Parameters({"useCloudEnv","cloudEnvName","os","os_version","browserName","browserVersion","url"})
    @BeforeMethod
    public void setUp(@Optional("false") boolean useCloudEnv, @Optional("false")String cloudEnvName,
                      @Optional("OS X") String os, @Optional("10") String os_version, @Optional("chrome-options")
                                  String browserName, @Optional("34")
                                  String browserVersion, @Optional("http://www.amazon.com") String url) throws IOException {

        if (useCloudEnv == true) {
            if (cloudEnvName.equalsIgnoreCase("browserstack")) {
                getCloudDriver(cloudEnvName, browserstack_username, browserstack_accesskey, os, os_version, browserName, browserVersion);
            } else if (cloudEnvName.equalsIgnoreCase("saucelabs")) {
                getCloudDriver(cloudEnvName, saucelabs_username, saucelabs_accesskey, os, os_version, browserName, browserVersion);
            }
        } else {
            getLocalDriver(os, browserName);
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(25, TimeUnit.SECONDS);
        driver.get(url);
    }


        public WebDriver getLocalDriver(@Optional("mac") String OS, String browserName){
            if(browserName.equalsIgnoreCase("chrome")){
                if(OS.equalsIgnoreCase("OS X")){
                    System.setProperty("webdriver.chrome.driver", "../Generic/browser-driver/chromedriver");
                }else if(OS.equalsIgnoreCase("Windows")){
                    System.setProperty("webdriver.chrome.driver", "../Generic/browser-driver/chromedriver.exe");
                }
                driver = new ChromeDriver();
            } else if(browserName.equalsIgnoreCase("chrome-options")){
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                if(OS.equalsIgnoreCase("OS X")){
                    System.setProperty("webdriver.chrome.driver", "../Generic/browser-driver/chromedriver");
                }else if(OS.equalsIgnoreCase("Windows")){
                    System.setProperty("webdriver.chrome.driver", "../Generic/browser-driver/chromedriver.exe");
                }
                driver = new ChromeDriver(options);
            }

            else if(browserName.equalsIgnoreCase("firefox")){
                if(OS.equalsIgnoreCase("OS X")){
                    System.setProperty("webdriver.gecko.driver", "../Generic/browser-driver/geckodriver");
                }else if(OS.equalsIgnoreCase("Windows")) {
                    System.setProperty("webdriver.gecko.driver", "../Generic/browser-driver/geckodriver.exe");
                }
                driver = new FirefoxDriver();

            } else if(browserName.equalsIgnoreCase("ie")) {
                System.setProperty("webdriver.ie.driver", "../Generic/browser-driver/IEDriverServer.exe");
                driver = new InternetExplorerDriver();
            }
            return driver;

        }
        public WebDriver getCloudDriver(String envName,String envUsername, String envAccessKey,String os, String os_version,String browserName,
                String browserVersion)throws IOException {
            DesiredCapabilities cap = new DesiredCapabilities();
            cap.setCapability("browser",browserName);
            cap.setCapability("browser_version",browserVersion);
            cap.setCapability("os", os);
            cap.setCapability("os_version", os_version);
            if(envName.equalsIgnoreCase("Saucelabs")){
                //resolution for Saucelabs
                driver = new RemoteWebDriver(new URL("http://"+envUsername+":"+envAccessKey+
                        "@ondemand.saucelabs.com:80/wd/hub"), cap);
            }else if(envName.equalsIgnoreCase("Browserstack")) {
                cap.setCapability("resolution", "1024x768");
                driver = new RemoteWebDriver(new URL("http://" + envUsername + ":" + envAccessKey +
                        "@hub-cloud.browserstack.com/wd/hub"), cap);
            }
            return driver;
        }

    @AfterMethod
    public void cleanUp(){
    //driver.close();
    }
    //****************************//Type Method//**************************************//
    //TypeByID
    public void typeOnID(String locator, String value) {
        driver.findElement(By.id(locator)).sendKeys(value);
    }

    //TypeByClassName
    public void typeOnClassName(String locator, String value) {
        driver.findElement(By.className(locator)).sendKeys(value);
    }

    //TypeByCss
    public void typeOnCss(String locator, String value) {
        driver.findElement(By.cssSelector(locator)).sendKeys(value);
    }

    //TypeByXpath
    public void typeOnXpath(String locator, String value) {
        driver.findElement(By.xpath(locator)).sendKeys(value);
    }

    //TypeOnElement
    public void typeOnElement(String locator, String value) {
        try {
            driver.findElement(By.cssSelector(locator)).sendKeys(value);
        } catch (Exception ex1) {
            try {
                System.out.println("First Attempt was not successful");
                driver.findElement(By.name(locator)).sendKeys(value);
            } catch (Exception ex2) {
                try {
                    System.out.println("Second Attempt was not successful");
                    driver.findElement(By.xpath(locator)).sendKeys(value);
                } catch (Exception ex3) {
                    System.out.println("Third Attempt was not successful");
                    driver.findElement(By.id(locator)).sendKeys(value);
                }
            }
        }
    }

    //****************************//Type and Enter Method//**************************************//
    //TypeByID
    public void typeOnIDAndEnter(String locator, String value) {
        driver.findElement(By.id(locator)).sendKeys(value,Keys.ENTER);
    }

    //TypeByClassName
    public void typeOnClassNameAndEnter(String locator, String value) {
        driver.findElement(By.className(locator)).sendKeys(value,Keys.ENTER);
    }

    //TypeByCss
    public void typeOnCssAndEnter(String locator, String value) {
        driver.findElement(By.cssSelector(locator)).sendKeys(value,Keys.ENTER);
    }

    //TypeByXpath
    public void typeOnXpathAndEnter(String locator, String value) {
        driver.findElement(By.xpath(locator)).sendKeys(value,Keys.ENTER);
    }

    //TypeOnElement
    public static void typeOnElementNEnter(String locator, String value) {
        try {
            driver.findElement(By.cssSelector(locator)).sendKeys(value, Keys.ENTER);
        } catch (Exception ex1) {
            try {
                System.out.println("First Attempt was not successful");
                driver.findElement(By.name(locator)).sendKeys(value, Keys.ENTER);
            } catch (Exception ex2) {
                try {
                    System.out.println("Second Attempt was not successful");
                    driver.findElement(By.xpath(locator)).sendKeys(value, Keys.ENTER);
                } catch (Exception ex3) {
                    System.out.println("Third Attempt was not successful");
                    driver.findElement(By.id(locator)).sendKeys(value, Keys.ENTER);
                }
            }
        }
    }
    //****************************//Click Method//**************************************//
    //ClickByID
    public void clickOnId(String locator) {
        driver.findElement(By.cssSelector(locator)).click();
    }

    //ClickByClassName
    public void clickOnClassName(String locator) {
        driver.findElement(By.cssSelector(locator)).click();
    }

    //ClickByCss
    public void clickOnCss(String locator) {
        driver.findElement(By.cssSelector(locator)).click();
    }

    //ClickByXpath
    public void clickOnXpath(String locator) {
        driver.findElement(By.xpath(locator)).click();
    }

    //ClickByElement
    public void clickOnElement(String locator) {
        try {
            driver.findElement(By.cssSelector(locator)).click();
        } catch (Exception ex1) {
            try {
                System.out.println("First Attempt was not successful");
                driver.findElement(By.name(locator)).click();
            } catch (Exception ex2) {
                try {
                    System.out.println("Second Attempt was not successful");
                    driver.findElement(By.xpath(locator)).click();
                } catch (Exception ex3) {
                    System.out.println("Third Attempt was not successful");
                    driver.findElement(By.id(locator)).click();
                }
            }
        }
    }

    //****************************//GetTextFromWebElemets Method//****************************//
    //get the text from the webElements of the arrayList
    public static List<String> getTextFromWebElements(String locator) {
        List<WebElement> element = new ArrayList<WebElement>();
        List<String> text = new ArrayList<String>();
        element = driver.findElements(By.cssSelector(locator));
        for (WebElement web : element) {
            String st = web.getText();
            text.add(st);
        }
        return text;
    }

    //*****************************//Caputure ScreenShot Method//*****************************//
    //capture ScreenShot
    public static void captureScreenshot(WebDriver driver, String screenshotName) {
        DateFormat df = new SimpleDateFormat("(MM.dd.yyyy-HH:mma)");
        Date date = new Date();
        df.format(date);

        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            //FileUtils.copyFile(file, new File(System.getProperty("user.dir") + "/screenshots/" + screenshotName + " " + df.format(date) + ".png"));
            System.out.println("Screenshot captured");
        } catch (Exception e) {
            System.out.println("Exception while taking screenshot " + e.getMessage());
            ;
        }

    }
    //*****************************//Spliting String Method//*****************************//
    //Spliting String form st String
    public static String convertToString(String st) {
        String splitString = "";
        splitString = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(st), ' ');
        return splitString;
    }

}

