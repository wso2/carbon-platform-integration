package org.wso2.carbon.automation.test.api.selenium.searchhome;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class SearchHomePage {

    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public SearchHomePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        driver.findElement(By.linkText(uiElementMapper.getElement("search.page.link"))).click();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("search"))) {
            // Alternatively, we could navigate to the login page, perhaps logging out first
            throw new IllegalStateException("This is not the Search Activity page");
        }
    }

    public void search(String searchItem) {
        driver.findElement(By.name(uiElementMapper.getElement("search.resource.name"))).sendKeys(searchItem);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("submitAdvSearchForm()");
    }

    public void checkForTheUploadedElement(String searchItem) {

        if (!driver.findElement(By.id(uiElementMapper.getElement("search.results.id"))).
                getText().contains(searchItem)) {

            throw new IllegalStateException("Search Element Does not Exists");
        }

    }

}


