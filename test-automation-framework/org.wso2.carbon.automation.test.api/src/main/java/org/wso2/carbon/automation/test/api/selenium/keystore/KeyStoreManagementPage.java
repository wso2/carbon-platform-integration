package org.wso2.carbon.automation.test.api.selenium.keystore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.NoSuchElementException;

public class KeyStoreManagementPage {
    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;

    public KeyStoreManagementPage(WebDriver driver) throws IOException {
        this.driver = driver;
        UIElementMapper uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("key.store.add.link"))).click();
        log.info("key store add page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("key.store.dashboard.middle.text"))).
                getText().contains("KeyStore Management")) {
            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public boolean checkOnUploadedKeyStore(String keyStoreName) throws InterruptedException {
        log.info("---------------------------->>>> " + keyStoreName);
        Thread.sleep(25000);

        String kerStoreNameOnServer = driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/td[3]" +
                                                                  "/table/tbody/tr[2]/td/div/div/table/tbody/tr/td")).getText();
        log.info(kerStoreNameOnServer);
        if (keyStoreName.equals(kerStoreNameOnServer)) {
            log.info("Uploaded KeyStore exists");
            return true;

        } else {
            String resourceXpath = "/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/table/tbody/tr[";
            String resourceXpath2 = "]/td";

            for (int i = 2; i < 10; i++) {
                String keyStoreNameNameOnAppServer = resourceXpath + i + resourceXpath2;

                String actualUsername = driver.findElement(By.xpath(keyStoreNameNameOnAppServer)).getText();

                log.info("val on app is -------> " + actualUsername);

                log.info("Correct is    -------> " + keyStoreName);

                try {

                    if (keyStoreName.contains(actualUsername)) {
                        log.info("newly Created keyStore   exists");
                        return true;

                    } else {
                        return false ;
                    }

                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the newly Created keryStore");

                }
            }

        }
        return false;
    }

}
