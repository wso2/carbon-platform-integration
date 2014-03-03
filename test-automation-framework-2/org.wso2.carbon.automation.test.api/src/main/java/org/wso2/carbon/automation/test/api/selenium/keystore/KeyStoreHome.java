package org.wso2.carbon.automation.test.api.selenium.keystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class KeyStoreHome {
    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public KeyStoreHome(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();

        driver.findElement(By.linkText(uiElementMapper.getElement("key.store.add.link"))).click();

        log.info("key store add page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("key.store.dashboard.middle.text"))).
                getText().contains("Key")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public void addKeyStore(String keyStoreFilePath, String passWord, String keyStoreProvider)
            throws InterruptedException {

        driver.findElement(By.linkText(uiElementMapper.getElement("key.store.add.new.link.text"))).click();
        driver.findElement(By.id(uiElementMapper.getElement("key.store.file.path.id"))).sendKeys(keyStoreFilePath);
        driver.findElement(By.name(uiElementMapper.getElement("key.store.password.name"))).sendKeys(passWord);
        driver.findElement(By.name(uiElementMapper.getElement("key.store.provider.name"))).sendKeys(keyStoreProvider);
        driver.findElement(By.xpath(uiElementMapper.getElement("key.store.next.button"))).click();
        driver.findElement(By.name(uiElementMapper.getElement("key.store.pass.key"))).sendKeys(passWord);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("doValidation()");
        Thread.sleep(5000);
        driver.findElement(By.xpath(uiElementMapper.getElement("key.store.successful.xpath"))).click();

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
