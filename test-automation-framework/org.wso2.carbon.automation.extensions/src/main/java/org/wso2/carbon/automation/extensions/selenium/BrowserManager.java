/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.automation.extensions.selenium;

import com.opera.core.systems.OperaDriver;
import com.saucelabs.selenium.client.factory.SeleniumFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.extensions.ExtensionConstants;
import org.wso2.carbon.automation.extensions.XPathConstants;

import javax.xml.xpath.XPathExpressionException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class BrowserManager {
    private static final Log log = LogFactory.getLog(BrowserManager.class);
    private static AutomationContext automationContext;
    public static WebDriver driver;

    public static WebDriver getWebDriver() throws MalformedURLException, XPathExpressionException {
        automationContext = new AutomationContext();
        String driverSelection = automationContext.getConfigurationNodeList(
                String.format(XPathConstants.SELENIUM_BROWSER_TYPE)).item(0).
                getFirstChild().getNodeValue();
        if (automationContext.getConfigurationNode(String.format(XPathConstants.SELENIUM_REMOTE_WEB_DRIVER_URL))
                .getAttributes().item(0).getNodeValue().equals("false")) {

            if (System.getenv().containsKey("JOB_URL")) {
                log.info("Test runs on Sauce Labs environment...");
                log.info("Operating System : " + System.getenv().get("SELENIUM_PLATFORM") +
                        " On Browser " + System.getenv().get("SELENIUM_BROWSER") + " version "+
                        System.getenv().get("SELENIUM_VERSION"));
                driver = SeleniumFactory.createWebDriver();
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                return driver;
            } else {
                log.info("Test runs on " + driverSelection + " browser...");
                getDriver(driverSelection);
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                return driver;
            }
        } else {
            log.info("Test runs on remote browser");
            if (System.getenv().containsKey("JOB_URL")) {
                log.info("Test runs on Sauce Labs environment...");
                log.info("Operating System : " + System.getenv().get("SELENIUM_PLATFORM") +
                        " On Browser " + System.getenv().get("SELENIUM_BROWSER") + " version "+
                        System.getenv().get("SELENIUM_VERSION"));
                driver = SeleniumFactory.createWebDriver();
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                return driver;
            } else {
                getRemoteWebDriver();
                driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
                return driver;
            }
        }
    }

    private static void getDriver(String driverSelection) throws XPathExpressionException {
        if (driverSelection.equalsIgnoreCase(ExtensionConstants.FIREFOX_BROWSER)) {
            driver = new FirefoxDriver();
        } else if (driverSelection.equalsIgnoreCase(ExtensionConstants.CHROME_BROWSER)) {
            System.setProperty("webdriver.chrome.driver",
                    automationContext.getConfigurationValue
                            (String.format(XPathConstants.CHROME_WEB_DRIVER_URL)));
            driver = new ChromeDriver();
        } else if (driverSelection.equalsIgnoreCase(ExtensionConstants.IE_BROWSER)) {
            driver = new InternetExplorerDriver();
        } else if (driverSelection.equalsIgnoreCase(ExtensionConstants.HTML_UNIT_DRIVER)) {
            driver = new HtmlUnitDriver(true);
        } else {
            driver = new OperaDriver();
        }
    }

    private static void getRemoteWebDriver() throws MalformedURLException, XPathExpressionException {
        URL url;
        String browserName = automationContext.getConfigurationNodeList(
                String.format(XPathConstants.SELENIUM_BROWSER_TYPE)).item(0).
                getFirstChild().getNodeValue();
        String remoteWebDriverURL =
                automationContext.getConfigurationValue
                        (String.format(XPathConstants.SELENIUM_REMOTE_WEB_DRIVER_URL));
        if (log.isDebugEnabled()) {
            log.debug("Browser selection " + browserName);
            log.debug("Remote WebDriverURL " + remoteWebDriverURL);
        }
        try {
            url = new URL(remoteWebDriverURL);
        } catch (MalformedURLException e) {
            log.error("Malformed URL " + e);
            throw new MalformedURLException("Malformed URL " + e);
        }
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setJavascriptEnabled(true);
        capabilities.setBrowserName(browserName);
        driver = new RemoteWebDriver(url, capabilities);
    }
}
