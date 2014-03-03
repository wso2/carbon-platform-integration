package org.wso2.carbon.automation.test.api.selenium.featuremanagement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

//this can be executed when u want to install a new feature

public class InstallingNewFeaturePage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public InstallingNewFeaturePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("features.add.link"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("feature.find.feature.text"))).click();

        log.info("API Add Page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("repositories.dashboard.text"))).
                getText().contains("Feature")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public void installingFeature(String installingFeature) throws InterruptedException {

        driver.findElement(By.id(uiElementMapper.getElement("feature.install.name.id"))).sendKeys(installingFeature);
        driver.findElement(By.id(uiElementMapper.getElement("feature.find.id"))).click();
        driver.findElement(By.name(uiElementMapper.getElement("feature.install.click"))).click();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("doNext('AF-RF');return false");
        Thread.sleep(10000);
        js.executeScript("doNext('RF-RL')");
        Thread.sleep(10000);
        driver.findElement(By.id(uiElementMapper.getElement("feature.install.accept.button"))).click();


    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
