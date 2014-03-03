package org.wso2.carbon.automation.test.api.selenium.configuretab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.NoSuchElementException;

public class UsersHomePage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public UsersHomePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("users.and.roles.link.text"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("users.add.link.id"))).click();

        log.info("API Add Page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("users.adn.roles.add.page.middle.text"))).
                getText().contains("Users")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public boolean checkOnUploadUser(String UserName) throws InterruptedException {

        if (!driver.findElement(By.id(uiElementMapper.getElement("users.adn.roles.add.page.middle.text"))).
                getText().contains("Users")) {

            throw new IllegalStateException("This is not the user add Page");
        }

        log.info(UserName);
        Thread.sleep(5000);

        String userNameOnServer = driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/table[2]/tbody/tr/td")).getText();
        log.info(userNameOnServer);
        if (UserName.equals(userNameOnServer)) {
            log.info("Uploaded Item exists");
            return true;

        } else {
            String resourceXpath = "/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/table[2]/tbody/tr[";
            String resourceXpath2 = "]/td";

            for (int i = 2; i < 10; i++) {
                String userNameOnAppServer = resourceXpath + i + resourceXpath2;

                String actualUsername = driver.findElement(By.xpath(userNameOnAppServer)).getText();
                log.info("val on app is -------> " + actualUsername);
                log.info("Correct is    -------> " + UserName);

                try {

                    if (UserName.contains(actualUsername)) {
                        log.info("newly Created User   exists");
                        return true;

                    }  else {
                        return false;
                    }

                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the newly Created User");

                }

            }

        }

        return false;
    }

    public void addUser(String userName, String passWord) {

        driver.findElement(By.linkText(uiElementMapper.getElement("users.add.new.user.link.id"))).click();
        driver.findElement(By.name(uiElementMapper.getElement("users.add.new.user.name.id"))).sendKeys(userName);
        driver.findElement(By.name(uiElementMapper.getElement("users.add.new.user.password.name"))).sendKeys(passWord);
        driver.findElement(By.name(uiElementMapper.getElement("users.add.new.user.password.retype.name"))).sendKeys(passWord);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("doNext()");
        driver.findElement(By.xpath(uiElementMapper.getElement("users.save"))).click();

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
