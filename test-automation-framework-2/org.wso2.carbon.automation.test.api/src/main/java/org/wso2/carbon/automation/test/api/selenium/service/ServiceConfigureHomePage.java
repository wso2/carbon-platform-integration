package org.wso2.carbon.automation.test.api.selenium.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.metadata.ServicePage;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class ServiceConfigureHomePage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public ServiceConfigureHomePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("service.configure.add.link"))).click();

        log.info("Service Configure Page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("service.configure.dashboard.middle.text"))).
                getText().contains("Services")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public ServicePage configureService(String newServiceConfiguration)
            throws InterruptedException, IOException {

        //changes should be come in to the new service Configuration parameter

        //driver.findElement(By.id(uiElementMapper.getElement("notification.add.email.id"))).sendKeys(newServiceConfiguration);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("SaveConfiguration()");
        Thread.sleep(5000);
        return new ServicePage(driver);

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
