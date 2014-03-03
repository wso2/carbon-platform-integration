package org.wso2.carbon.automation.test.api.selenium.searchactivity;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.resourcebrowse.ResourceBrowsePage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class SearchActivityPage {

    private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ResourceBrowsePage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public SearchActivityPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        driver.findElement(By.linkText(uiElementMapper.getElement("search.activity.link"))).click();

        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("activities"))) {
            // Alternatively, we could navigate to the login page, perhaps logging out first
            throw new IllegalStateException("This is not the Search Activity page");
        }
    }

    public void searchElement() throws InterruptedException {

        driver.findElement(By.id(uiElementMapper.getElement("search.activity.id"))).clear();
        driver.findElement(By.id(uiElementMapper.getElement("search.activity.id"))).sendKeys("testuser2");
        driver.findElement(By.id(uiElementMapper.getElement("search.activity.name.id"))).sendKeys("/Capp_1.0.0.carTestFile");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("submitActivityForm(1) ");

    }

    public void verifySearchElement(String searchElement) throws InterruptedException {
        log.info("---------------------->>>>> " + searchElement);
        Thread.sleep(5000);

        if (!driver.findElement(By.id(uiElementMapper.getElement("search.activity.exists.id"))).
                getText().contains(searchElement)) {

            throw new IllegalStateException("Search Element Does not Exists");
        }

    }

}
