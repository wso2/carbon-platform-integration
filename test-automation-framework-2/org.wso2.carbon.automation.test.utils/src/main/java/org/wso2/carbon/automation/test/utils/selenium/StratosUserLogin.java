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

import com.thoughtworks.selenium.Selenium;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.testng.Assert.assertTrue;


public class StratosUserLogin {
    private static final Log log = LogFactory.getLog(StratosUserLogin.class);


    public static void userLogin(WebDriver driver, Selenium selenium, String userName,
                                 String password,
                                 String productName) throws InterruptedException {
        driver.findElement(By.xpath("//a[2]/img")).click();
        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("//tr[4]/td[2]/input")).click();

        if (productName.equalsIgnoreCase("manager")) {
            assertTrue(driver.findElement(By.id("middle")).findElement(By.id("cloudService"))
                               .getText().contains("Application Server"),
                       "Manager Home page Failed");
            String pageSource = driver.getPageSource();
            assertTrue(pageSource.contains("Mashup Server"), "Manager Home page Failed");
            assertTrue(pageSource.contains("Identity Server"), "Manager Home page Failed");
            assertTrue(pageSource.contains("Message Broker"), "Manager Home page Failed");
            assertTrue(pageSource.contains("Enterprise Service Bus"),
                       "Manager Home page Failed");
        } else {
            assertTrue(driver.findElement(By.className("dashboard-title"))
                               .getText().toLowerCase().contains("quick start dashboard"),
                       "Failed to display service home Page :");
        }
    }
}
