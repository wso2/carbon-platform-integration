package org.wso2.carbon.automation.test.utils.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

public class EntitlementManagementSeleniumUtil {
    public static void deleteEntitlementPolicies(WebDriver driver) throws InterruptedException {
        WebElement table = driver.findElement(By.id("dataTable"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        Iterator<WebElement> i = rows.iterator();
        int counter = 1;
        while (i.hasNext()) {
            WebElement row = i.next();
            List<WebElement> columns = row.findElements(By.tagName("td"));
            if (columns.size() > 0) {
                if (!columns.get(0).getText().contains("No policies defined")) {
                    driver.findElement(By.xpath("//tr[2]/td/div/div/form[2]/table/tbody/tr[" +
                            counter + "]/td[2]/input  ")).click();
                    counter++;
                    if (rows.size() == counter) {
                        driver.findElement(By.id("delete1")).click();
                        driver.findElement(By.xpath("//div[3]/div[2]/button")).click();
                        Thread.sleep(5000);
                        break;
                    }
                }
            }
        }
    }
}
