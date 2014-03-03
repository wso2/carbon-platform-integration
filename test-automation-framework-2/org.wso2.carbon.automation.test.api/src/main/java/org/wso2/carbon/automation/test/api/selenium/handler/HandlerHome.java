package org.wso2.carbon.automation.test.api.selenium.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.NoSuchElementException;

public class HandlerHome {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public HandlerHome(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("handler.add.tab.id"))).click();

        driver.findElement(By.linkText(uiElementMapper.getElement("handler.add.link"))).click();

        log.info("Handler adding page");

        if (!driver.findElement(By.id(uiElementMapper.getElement("add.new.handler.dashboard.middle.text"))).
                getText().contains("Handlers")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public boolean checkOnUploadedHandler(String handlerName) throws InterruptedException {

        Thread.sleep(10000);
        String handlerNameOnServer = driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/td[3]" +
                                          "/table/tbody/tr[2]/td/div/div/table/tbody/tr/td")).getText();

        log.info(handlerName);
        if (handlerName.equals(handlerNameOnServer)) {
            log.info("newly Created handler exists");
            return true;

        } else {
            String resourceXpath = "/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/table/tbody/tr[";
            String resourceXpath2 = "]/td";

            for (int i = 2; i < 10; i++) {
                String artifactNameOnAppServer = resourceXpath + i + resourceXpath2;

                String actualUsername = driver.findElement(By.xpath(artifactNameOnAppServer)).getText();

                log.info("val on app is -------> " + actualUsername);

                log.info("Correct is    -------> " + handlerNameOnServer);

                try {

                    if (handlerNameOnServer.contains(actualUsername)) {
                        log.info("newly Created handler   exists");
                        return true;

                    } else {
                        return false;
                    }


                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the newly Created handler");

                }

            }

        }

        return false;
    }

    public void addNewHandler(String handlerName) throws InterruptedException, IOException {

        driver.findElement(By.linkText(uiElementMapper.getElement("add.new.handler.link.text"))).click();
        Thread.sleep(3000);
        driver.switchTo().frame("frame_payload");
        driver.findElement(By.id(uiElementMapper.getElement("add.new.handler.text.area"))).clear();
        driver.findElement(By.id(uiElementMapper.getElement("add.new.handler.text.area"))).sendKeys(handlerName);
        Thread.sleep(4000);
        driver.switchTo().defaultContent();
        driver.findElement(By.xpath(uiElementMapper.getElement("add.new.handler.save.xpath"))).click();
        driver.findElement(By.cssSelector(uiElementMapper.getElement("add.new.handler.dialog.box"))).click();

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
