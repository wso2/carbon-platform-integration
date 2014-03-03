package org.wso2.carbon.automation.test.api.selenium.serverrolecreation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.NoSuchElementException;

public class ServerRoleHome {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public ServerRoleHome(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("server.role.add.link"))).click();
        log.info("Server role Add Page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("server.role.dashboard.middle.text"))).
                getText().contains("Roles")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public boolean checkOnUploadedServerRole(String serverRoleName) throws InterruptedException {
        log.info("---------------------------->>>> " + serverRoleName);
        Thread.sleep(5000);
        String serverRoleNameOnServer = driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/table/tbody/tr/td")).getText();
        log.info(serverRoleNameOnServer);
        if (serverRoleName.equals(serverRoleNameOnServer)) {
            log.info("newly Created notification exists");
            return true;

        } else {
            String resourceXpath = "/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/table/tbody/tr[";
            String resourceXpath2 = "]/td";

            for (int i = 2; i < 10; i++) {
                String serverRoleNameOnAppserver = resourceXpath + i + resourceXpath2;
                String actualUsername = driver.findElement(By.xpath(serverRoleNameOnAppserver)).getText();
                log.info("val on app is -------> " + actualUsername);
                log.info("Correct is    -------> " + serverRoleName);
                try {
                    if (serverRoleName.contains(actualUsername)) {
                        log.info("newly Created notification   exists");
                        return true;
                    }   else {
                        return false;
                    }

                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the newly Created notification");

                }

            }

        }

        return false;
    }

    public void addServerRole(String serverRoleName) throws InterruptedException, IOException {

        driver.findElement(By.linkText(uiElementMapper.getElement("server.role.add.link.text"))).click();
        driver.findElement(By.id(uiElementMapper.getElement("server.role.name.id"))).sendKeys(serverRoleName);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("addServerRole()");

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
