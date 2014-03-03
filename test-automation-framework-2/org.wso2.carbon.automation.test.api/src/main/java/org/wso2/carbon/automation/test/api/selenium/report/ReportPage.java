package org.wso2.carbon.automation.test.api.selenium.report;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class ReportPage {

    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public ReportPage(WebDriver driver) throws IOException {
        this.driver = driver;

        this.uiElementMapper = UIElementMapper.getInstance();

        driver.findElement(By.linkText(uiElementMapper.getElement("manage.report.page.link"))).click();

        if (!driver.findElement(By.id(uiElementMapper.getElement("add.report.list.dashboard.middle.text"))).
                getText().contains("Add Report")) {
           

            throw new IllegalStateException("This is not the add Report Page");
        }

    }

    public void addNewReport(String reportName, String templatePath, String reportType,
                             String reportClass)
            throws InterruptedException, IOException {

        driver.findElement(By.linkText(uiElementMapper.getElement("report.add.link"))).click();
        driver.findElement(By.id(uiElementMapper.getElement("add.report.name"))).sendKeys(reportName);
        driver.findElement(By.id(uiElementMapper.getElement("add.report.template"))).sendKeys(templatePath);
        new Select(driver.findElement(By.id(uiElementMapper.getElement("add.report.type")))).selectByVisibleText(reportType);
        driver.findElement(By.id(uiElementMapper.getElement("add.report.class"))).sendKeys(reportClass);
        JavascriptExecutor js2 = (JavascriptExecutor) driver;
        js2.executeScript("addReport()");

    }

}
