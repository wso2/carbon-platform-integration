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

package org.wso2.carbon.automation.test.api.selenium.appfactory.appmanagement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.appfactory.redmine.RedMineLoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.Set;

public class IssuePage {

    private static final Log log = LogFactory.getLog(RedMineLoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public IssuePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("issuetracker.jag"))) {
            throw new IllegalStateException("This is not the Issue  page");
        }
    }

    public RedMineLoginPage gotoRedMineTab() throws IOException, InterruptedException {
        driver.findElement(By.linkText(uiElementMapper.getElement("app.issue.redMine.tab.link"))).click();
        Thread.sleep(10000);
        try {
            Set handles = driver.getWindowHandles();
            String current = driver.getWindowHandle();
            handles.remove(current);
            String newTab = (String) handles.iterator().next();
            driver.switchTo().window(newTab);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        log.info("shifted to the new Tab");
        return new RedMineLoginPage(driver);
    }


    public boolean isIssueDetailsAreAvailable() throws InterruptedException {
        //checking the total bug count is equal to 1 
        driver.navigate().refresh();
        Thread.sleep(30000);
        String itemHeader = driver.findElement(By.id(uiElementMapper.getElement
                ("app.factory.issue.item.header.id"))).getText();
        String bugCount = driver.findElement(By.xpath(uiElementMapper.getElement
                ("app.factory.issue.total.count.xpath"))).getText();
        log.info("----------------------------------------------------------");
        log.info(itemHeader);
        log.info(bugCount);
        log.info("----------------------------------------------------------");
        if (bugCount.contains("1.0.3 Staging 1 0 0")) {
            log.info("no of issues matches");
            return true;
        }
        return false;
    }
}
