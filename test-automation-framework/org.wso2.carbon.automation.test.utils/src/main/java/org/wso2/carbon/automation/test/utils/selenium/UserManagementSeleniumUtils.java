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
*under the License.*/

package org.wso2.carbon.automation.test.utils.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;

public class UserManagementSeleniumUtils {

    public static void deleteUserByName(WebDriver driver, String userName) {
        WebElement table =
                driver.findElement(By.id("userTable"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        Iterator<WebElement> i = rows.iterator();
        boolean status = false;
        boolean outStatus = false;
        int counter = 0;
        while (i.hasNext()) {
            WebElement row = i.next();
            List<WebElement> columns = row.findElements(By.tagName("td"));
            for (WebElement column : columns) {

                if (column.getText().equals(userName)) {
                    status = true;
                }
                if (status && column.getText().contains("Delete")) {
                    outStatus = true;
                    driver.findElement(By.xpath("/html/body/table/tbody/tr[2]/td[3]/table/tbody/tr[2]" +
                                                "/td/div/div/table/tbody/tr[" + counter +
                                                "]/td[2]/a[3]")).click();
                    driver.findElement(By.xpath("//button")).click();
                    break;
                }
            }
            if (outStatus) {
                break;
            }
            counter++;
        }
    }

    public static void deleteRoleByName(WebDriver driver, String roleName) {
        WebElement table =
                driver.findElement(By.id("roleTable"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        Iterator<WebElement> i = rows.iterator();
        boolean status = false;
        boolean outStatus = false;
        int counter = 0;
        while (i.hasNext()) {
            WebElement row = i.next();
            List<WebElement> columns = row.findElements(By.tagName("td"));
            for (WebElement column : columns) {

                if (column.getText().equals(roleName)) {
                    status = true;
                }
                if (status && column.getText().contains("Delete")) {
                    outStatus = true;
                    driver.findElement(By.xpath
                            ("/html/body/table/tbody/tr[2]/td[3]/table/tbody/" +
                             "tr[2]/td/div/div/table/tbody/tr[" +
                             counter + "]/td[2]/a[4]")).click();
                    driver.findElement(By.xpath("//button")).click();
                    break;
                }
            }
            if (outStatus) {
                break;
            }
            counter++;
        }
    }


}
