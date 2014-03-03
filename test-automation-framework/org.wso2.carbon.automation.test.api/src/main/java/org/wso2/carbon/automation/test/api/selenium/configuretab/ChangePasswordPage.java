package org.wso2.carbon.automation.test.api.selenium.configuretab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class ChangePasswordPage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public ChangePasswordPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("users.and.roles.link.text"))).click();

        driver.findElement(By.linkText(uiElementMapper.getElement("pass.word.change.link"))).click();

        log.info("Change Password Page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("users.adn.roles.add.page.middle.text"))).
                getText().contains("Password")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public void changePasswordCheck(String UserName, String passWord)
            throws InterruptedException, IOException {

        driver.findElement(By.linkText(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        new LoginPage(driver).loginAs(UserName, passWord);
        log.info("password Change Successful");
    }

    public void changePassword(String passWord, String newPassword) throws InterruptedException {

        driver.findElement(By.name(uiElementMapper.getElement("pass.word.current.name"))).sendKeys(passWord);
        driver.findElement(By.name(uiElementMapper.getElement("pass.word.new.name"))).sendKeys(newPassword);
        driver.findElement(By.name(uiElementMapper.getElement("pass.word.check.name"))).sendKeys(newPassword);
        Thread.sleep(4000);
        driver.findElement(By.xpath(uiElementMapper.getElement("pass.word.change.save.xpath"))).click();
        Thread.sleep(10000);
        driver.findElement(By.xpath(uiElementMapper.getElement("password.change.dialog.xpath"))).click();

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
