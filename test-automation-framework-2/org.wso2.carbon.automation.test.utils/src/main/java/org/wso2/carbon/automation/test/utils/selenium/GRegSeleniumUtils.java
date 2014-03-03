/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.automation.test.utils.selenium;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static org.testng.Assert.assertTrue;

public class GRegSeleniumUtils {

    private static final Log log = LogFactory.getLog(GRegSeleniumUtils.class);

    public static int getResourceId(WebDriver driver, String resourceName) {
        int pageCount = 10;
        int id = 0;
        long currentTime = System.currentTimeMillis();
        long actualTime;

        do {
            if (driver.getPageSource().contains(resourceName)) {
                for (int i = 1; i <= pageCount; i++) {
                    if (driver.findElement(By.id("resourceView" + i)).getText().equals(resourceName) ||
                        driver.findElement(By.id("resourceView" + i)).getText().equals(resourceName +
                                                                                       " " + "..")) {
                        return i;
                    }
                }
            }
            actualTime = System.currentTimeMillis();
        } while (!(((actualTime - currentTime) / 1000) > 10));

        return id;
    }

    public static void deleteResourceFromBrowser(WebDriver driver, String resourceName) {
        int resourceRowId = getResourceId(driver, resourceName);
        if (resourceRowId != 0) {
            try {
                driver.findElement(By.id("actionLink" + resourceRowId)).click();
                resourceRowId = ((resourceRowId - 1) * 7) + 2;
                driver.findElement(By.xpath("//tr[2]/td[3]/table/tbody/tr[2]/td/div/div/table/tbody/tr/td" +
                                            "/div[2]/div[3]/div[3]/div[9]/table/tbody/tr["
                                            + resourceRowId + "]/td/div/a[3]")).click();
                assertTrue(driver.findElement(By.id("ui-dialog-title-dialog")).getText().contains("WSO2 Carbon"),
                           "Popup not found :");
                driver.findElement(By.xpath("//button")).click();
            } catch (WebDriverException ignored) {
                log.info("Web element not found");
            }
            gotoDetailViewTab(driver);
        }
    }

    public static boolean waitForBrowserPage(WebDriver driver) {
        long currentTime = System.currentTimeMillis();
        long exceededTime;
        do {
            try {
                if (driver.findElement(By.id("middle")).findElement(By.tagName("h2")).getText()
                        .contains("Browse")) {
                    return true;
                }
            } catch (WebDriverException ignored) {
                log.info("Waiting for the element");
            }
            exceededTime = System.currentTimeMillis();
        } while (!(((exceededTime - currentTime) / 1000) > 60));
        return false;
    }


    public static boolean waitForElement(WebDriver driver, String elementType, String element) {
        long currentTime = System.currentTimeMillis();
        long exceededTime;
        Boolean status = false;
        do {
            try {
                if (elementType.equals("xpath")) {
                    if (driver.findElement(By.xpath(element)).isDisplayed()) {
                        status = true;
                        break;
                    }
                } else if (elementType.equals("id")) {
                    if (driver.findElement(By.id(element)).isDisplayed()) {
                        status = true;
                        break;
                    }
                }
            } catch (WebDriverException ignored) {
                log.info("Waiting for the element");
            }
            exceededTime = System.currentTimeMillis();
        } while (!(((exceededTime - currentTime) / 1000) > 60));
        assertTrue(status, "Element not found within 60 sec - " + element);
        return status;
    }

    private static void gotoDetailViewTab(WebDriver driver) {
        driver.findElement(By.linkText("Browse")).click();    //Click on Browse link
        GRegSeleniumUtils.waitForBrowserPage(driver);
        driver.findElement(By.id("stdView")).click();        //Go to Detail view Tab
    }
}
