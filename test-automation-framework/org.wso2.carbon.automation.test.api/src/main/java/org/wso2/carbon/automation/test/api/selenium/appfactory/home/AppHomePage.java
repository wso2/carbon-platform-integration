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

package org.wso2.carbon.automation.test.api.selenium.appfactory.home;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.appfactory.appmanagement.AddNewAppPage;
import org.wso2.carbon.automation.test.api.selenium.appfactory.appmanagement.AppManagementPage;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class AppHomePage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public AppHomePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.

        if (!(driver.getCurrentUrl().contains("index.jag"))) {
            throw new IllegalStateException("This is not the home page");
        }
    }

    public LoginPage logout() throws IOException {
        driver.findElement(By.linkText(uiElementMapper.getElement("home.greg.sign.out.xpath"))).click();
        return new LoginPage(driver);
    }

    //this method will navigate to addNewApplication Page
    public AddNewAppPage gotoAddNewAppPage() throws IOException, InterruptedException {
        log.info("loading the Home Page");
        //this pause is set until created applications loaded to the home page
        Thread.sleep(15000);
        driver.findElement(By.linkText(uiElementMapper.getElement("app.AddNew.App.link"))).click();
        return new AddNewAppPage(driver);
    }

    //this method is used to check the availability of an added application in the home page
    public boolean isApplicationAvailable(String applicationName) throws InterruptedException {
        //this pause is set until created applications loaded to the home page
        Thread.sleep(15000);
        driver.navigate().refresh();
        //this pause is set until created applications Deployment
        Thread.sleep(20000);
        driver.findElement(By.cssSelector(uiElementMapper
                .getElement("app.factory.list.view"))).click();
        log.info("application is processing");
        driver.findElement(By.id(uiElementMapper.getElement("app.homepage.search.textBox")))
                .sendKeys(applicationName);
        String applicationNameInAppFactory = driver.findElement(By.xpath(uiElementMapper
                .getElement("app.first.element.of.home.page"))).getText();
        if (applicationName.equals(applicationNameInAppFactory)) {
            log.info("Added Application is Available");
            return true;
        }
        log.info("Added Application is Not Available");
        return false;
    }

    //This method will navigate to desired application's overview Page
    public AppManagementPage gotoApplicationManagementPage(String applicationName)
            throws IOException, InterruptedException {
        //this pause is set until created applications loaded to the home page
        Thread.sleep(5000);
        driver.findElement(By.cssSelector(uiElementMapper
                .getElement("app.factory.list.view"))).getText();
        Thread.sleep(15000);
        driver.navigate().refresh();
        driver.findElement(By.cssSelector(uiElementMapper
                .getElement("app.factory.list.view"))).click();
        //this pause is set until created applications Deployment
        Thread.sleep(20000);
        driver.findElement(By.linkText((applicationName))).click();
        return new AppManagementPage(driver);
    }
}
