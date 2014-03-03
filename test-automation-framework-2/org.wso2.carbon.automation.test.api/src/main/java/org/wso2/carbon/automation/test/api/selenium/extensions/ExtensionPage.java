package org.wso2.carbon.automation.test.api.selenium.extensions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.extensionlist.ExtensionListPage;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class ExtensionPage {
    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public ExtensionPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("extension.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("extensions.add.link"))).click();

        log.info("extension adding page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("tenant.role.dashboard.middle.text"))).
                getText().contains("Add Extension")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public ExtensionListPage addNewExtension(String extensionName)
            throws InterruptedException, IOException {

        driver.findElement(By.id(uiElementMapper.getElement("extension.add.text.box"))).sendKeys(extensionName);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("addExtension()");
        Thread.sleep(7000);
        return new ExtensionListPage(driver);

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }
}
