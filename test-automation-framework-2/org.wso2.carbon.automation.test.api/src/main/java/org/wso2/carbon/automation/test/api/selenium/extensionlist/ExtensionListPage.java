package org.wso2.carbon.automation.test.api.selenium.extensionlist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.NoSuchElementException;

public class ExtensionListPage {

    private static final Log log = LogFactory.getLog(ExtensionListPage.class);
    private WebDriver driver;

    public ExtensionListPage(WebDriver driver) throws IOException {
        this.driver = driver;
        UIElementMapper uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!driver.findElement(By.id(uiElementMapper.getElement("extension.list.page.dashboard.middle.text"))).
                getText().contains("Extension List")) {

            throw new IllegalStateException("This is not the Extension List Page");
        }
    }

    public boolean checkOnUploadedExtension(String extensionName) throws InterruptedException {

        String extensionNameOnServer = driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/td[3]" +
                                                                   "/table/tbody/tr[2]/td/div/div/form/" +
                                                                   "table/tbody/tr/td")).getText();

        log.info(extensionName);
        if (extensionName.equals(extensionNameOnServer)) {
            log.info("newly Created extension exists");
            return true;
        } else {
            String resourceXpath = "/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/form/table/tbody/tr[";
            String resourceXpath2 = "]/td";

            for (int i = 2; i < 10; i++) {
                String extensionNameOnAppServer = resourceXpath + i + resourceXpath2;

                String actualUsername = driver.findElement(By.xpath(extensionNameOnAppServer)).getText();
                log.info("val on app is -------> " + actualUsername);
                log.info("Correct is    -------> " + extensionName);

                try {

                    if (extensionName.contains(actualUsername)) {
                        log.info("newly Created extension   exists");
                        return true;
                    }  else {
                        return false;
                    }

                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the newly Created organization");

                }
            }
        }
        return false;
    }
}
