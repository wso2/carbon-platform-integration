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
import org.openqa.selenium.support.ui.Select;
import org.wso2.carbon.automation.test.api.selenium.appfactory.home.AppHomePage;
import org.wso2.carbon.automation.test.api.selenium.appfactory.home.AppLogin;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class AddNewAppPage {
    private static final Log log = LogFactory.getLog(AppLogin.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public AddNewAppPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("createapplication.jag"))) {
            throw new IllegalStateException("This is not the Create Application page");
        }
    }

    //this method is used to create a new application for the AppFactory
    public AppHomePage createAnApplication(String appName, String appKey, String iconPath
            , String description, String appType, String repositoryType)
            throws IOException, InterruptedException {
        try {
            driver.findElement(By.id(uiElementMapper.getElement("new.app.add.app.name"))).sendKeys(appName);
            driver.findElement(By.id(uiElementMapper.getElement("new.app.add.app.key"))).clear();
            driver.findElement(By.id(uiElementMapper.getElement("new.app.add.app.key"))).sendKeys(appKey);
            driver.findElement(By.id(uiElementMapper.getElement("new.app.add.app.icon"))).sendKeys(iconPath);
            driver.findElement(By.id(uiElementMapper.getElement("new.app.add.app.Description"))).sendKeys(description);
            new Select(driver.findElement(By.id(uiElementMapper.getElement("new.app.add.app.type")))).
                    selectByVisibleText(appType);
            new Select(driver.findElement(By.id(uiElementMapper.getElement("new.app.add.repository.type")))).
                    selectByVisibleText(repositoryType);
            //this  thread sleep is to wait till add button appears
            Thread.sleep(5000);
            driver.findElement(By.id(uiElementMapper.getElement("create.new.app.button"))).click();
            //this wait until the application creates
            Thread.sleep(45000);
            log.info("Application Creation is successful");
            return new AppHomePage(driver);
        } catch (Exception ex) {
            throw new IllegalStateException("Create Application Process is unsuccessful");
        }
    }
}







