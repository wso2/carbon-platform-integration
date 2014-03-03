package org.wso2.carbon.automation.test.api.selenium.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.servlistlist.ServiceListPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class ServicePage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public ServicePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("carbon.Main.tab"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("service.add.link"))).click();
        log.info("we are in the correct  Page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("service.dashboard.middle.text"))).
                getText().contains("Service")) {
            throw new IllegalStateException("not in the correct Page");
        }
    }

    public ServiceListPage uploadService(String name, String nameSpace) throws InterruptedException
            , IOException {
        WebElement serviceUploadField = driver.findElement(By.id(uiElementMapper.getElement("service.add.name.id")));
        serviceUploadField.sendKeys(name);
        WebElement serviceUploadNamespace = driver.findElement(By.id(uiElementMapper.getElement("service.add.namespace.id")));
        serviceUploadNamespace.sendKeys(nameSpace);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("addEditArtifact()");
        log.info("successfully Saved");
        driver.findElement(By.linkText(uiElementMapper.getElement("service.check.save.service"))).click();
        return new ServiceListPage(driver);

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.linkText(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
