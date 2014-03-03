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

package org.wso2.carbon.automation.test.api.selenium.appfactory.redmine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.wso2.carbon.automation.test.api.selenium.appfactory.appmanagement.IssuePage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.Set;

public class RedMineHomePage {
    private static final Log log = LogFactory.getLog(RedMineLoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;


    public RedMineHomePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("redmine"))) {
            throw new IllegalStateException("This is not the Red Mine Home page");
        }
    }

    public void createIssueForProject(String subject, String description, String version) throws InterruptedException {
        log.info("creating the issue ");
        driver.findElement(By.linkText(uiElementMapper.getElement("app.redMine.issue.button"))).click();
        driver.findElement(By.id(uiElementMapper.getElement("app.redMine.issue.subject"))).sendKeys(subject);
        driver.findElement(By.id(uiElementMapper.getElement("app.redMine.issue.description"))).sendKeys(description);
        new Select(driver.findElement(By.id(uiElementMapper.getElement("app.issue.version.id")))).
                selectByVisibleText(version);

        driver.findElement(By.name(uiElementMapper.getElement("app.redMine.issue.submit"))).click();
        Thread.sleep(15000);
        log.info("Issue is Created");
    }


    public IssuePage gotoAppFactory() throws IOException, InterruptedException {
        Thread.sleep(10000);
        log.info("changing the windows");
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
        return new IssuePage(driver);
    }
}
