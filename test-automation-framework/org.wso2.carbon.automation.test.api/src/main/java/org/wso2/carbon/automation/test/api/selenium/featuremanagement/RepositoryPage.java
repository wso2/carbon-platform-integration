package org.wso2.carbon.automation.test.api.selenium.featuremanagement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.NoSuchElementException;

public class RepositoryPage {
    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public RepositoryPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        driver.findElement(By.id(uiElementMapper.getElement("configure.tab.id"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("features.add.link"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement("repository.add.tab.text"))).click();

        log.info("API Add Page");
        if (!driver.findElement(By.id(uiElementMapper.getElement("repositories.table.id"))).
                getText().contains("Repositories")) {

            throw new IllegalStateException("This is not the correct Page");
        }
    }

    public boolean checkOnUploadRepository(String repositoryName) throws InterruptedException {

        log.info("---------------------------->>>> " + repositoryName);
        Thread.sleep(25000);

        String repositoryNameOnServer = driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/" +
                 "td[3]/table/tbody/tr[2]/td/div/div/div/div[3]/div/table/tbody/tr/td/table/tbody/" +
                 "tr[4]/td/div/table/tbody/tr/td")).getText();

        log.info(repositoryNameOnServer);
        if (repositoryName.equals(repositoryNameOnServer)) {
            log.info("Uploaded Repository exists");
            return true;

        } else {
            String resourceXpath = "/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]/td/div/div/div/div[3]/div/table" +
                                   "/tbody/tr/td/table/tbody/tr[4]/td/div/table/tbody/tr[";
            String resourceXpath2 = "]/td";

            for (int i = 2; i < 10; i++) {
                String repositoryNameOnAppServer = resourceXpath + i + resourceXpath2;
                try {
                    String actualUsername = driver.findElement(By.xpath(repositoryNameOnAppServer)).getText();
                    log.info("val on app is -------> " + actualUsername);
                    log.info("Correct is    -------> " + repositoryName);

                    if (repositoryName.contains(actualUsername)) {
                        log.info("newly Created repository   exists");
                        return true;
                    }  else {
                        return false ;
                    }

                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the newly Created repository");

                }
            }
        }
        return false;
    }

    public void addRepository(String repositoryUrlName, String repositoryName) {

        driver.findElement(By.linkText(uiElementMapper.getElement("repository.add.link.text"))).click();
        driver.findElement(By.id(uiElementMapper.getElement("repository.url.name"))).clear();
        driver.findElement(By.id(uiElementMapper.getElement("repository.name.id"))).sendKeys(repositoryName);
        driver.findElement(By.id(uiElementMapper.getElement("repository.url.name"))).sendKeys(repositoryUrlName);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(" addRepository('Adding repository....')");

    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.xpath(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

}
