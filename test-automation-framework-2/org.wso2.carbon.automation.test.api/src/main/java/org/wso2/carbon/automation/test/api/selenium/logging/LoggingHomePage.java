package org.wso2.carbon.automation.test.api.selenium.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class LoggingHomePage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public LoggingHomePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();

        driver.findElement(By.linkText(uiElementMapper.getElement("logging.add.link"))).click();

        log.info("Logging Configuration Page");

        if (!driver.findElement(By.id(uiElementMapper.getElement("logging.dashboard.middle.text"))).
                getText().contains("Logging")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public void changeGlobalLog4jConfiguration(String globalLogLevel) throws InterruptedException {

        new Select(driver.findElement(By.id("globalLogLevel"))).selectByVisibleText(globalLogLevel);
        driver.findElement(By.id(uiElementMapper.getElement("logging.update.button.id"))).click();

        //here onwards hold due to inabilitiy to click the okay button of the alert
        //driver.findElement(By.id(uiElementMapper.getElement("log4j.global.success.xpath"))).click();

    }

    public void changeLog4JAppender(String log4jAppenderName, String globalLogLevel2)
            throws InterruptedException {

        log.info("---------------------->>>>>> " + log4jAppenderName);
        log.info("---------------------->>>>>> " + globalLogLevel2);
        new Select(driver.findElement(By.id("appenderCombo"))).selectByVisibleText(log4jAppenderName);
        Thread.sleep(5000);
        new Select(driver.findElement(By.id("appenderThresholdCombo"))).selectByVisibleText(globalLogLevel2);
        driver.findElement(By.id(uiElementMapper.getElement("log4j.appender.update.id"))).click();

        //here onwards hold due to inabilitiy to click the okay button of the alert
        // driver.findElement(By.id(uiElementMapper.getElement("log4j.global.success.xpath"))).click();

    }

    public void configureLog4jLoggers(String loggerName, String globalLogLevel)
            throws InterruptedException {

        log.info("----------->>>>>>>>>>>>>>>>>12345  " + loggerName);
        Thread.sleep(15000);
        driver.findElement(By.id(uiElementMapper.getElement("log4j.logger.search.id"))).sendKeys(loggerName);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("showLoggers('true');return false");
        Thread.sleep(10000);
        String selectId = loggerName + "LogLevel";
        new Select(driver.findElement(By.id(selectId))).selectByVisibleText(globalLogLevel);
        System.out.println("im printing9999");
        driver.findElement(By.xpath(uiElementMapper.getElement("log4j.logger.successful.button.xpath"))).click();
        System.out.println("im printing");
        

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
