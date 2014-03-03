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

package org.wso2.carbon.automation.test.api.selenium.appfactory.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class NewDatabasePage {
    private static final Log log = LogFactory.getLog(ResourceOverviewPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public NewDatabasePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
           if (!(driver.getCurrentUrl().contains("newdatabase.jag"))) {
            throw new IllegalStateException("This is not the New Database page");
        }
    }

    public DatabaseConfigurationPage createDatabaseDefault(String databaseName, String passWord) throws IOException, InterruptedException {
        log.info("loading the Create Database Page");
        driver.findElement(By.id(uiElementMapper.getElement("app.factory.database.name.id"))).sendKeys(databaseName);
        driver.findElement(By.id(uiElementMapper.getElement("app.factory.database.password"))).sendKeys(passWord);
        driver.findElement(By.id(uiElementMapper.getElement("app.factory.database.confirm.password"))).sendKeys(passWord);
        driver.findElement(By.cssSelector(uiElementMapper.getElement("app.factory.database.submit.button"))).click();
        Thread.sleep(15000);
        return new DatabaseConfigurationPage(driver);
    }

    public DatabaseConfigurationPage createDatabaseCustomised(String databaseName, String passWord,
                                                              String dbEnvironment, String user, String template) throws IOException, InterruptedException {
        driver.findElement(By.id(uiElementMapper.getElement("app.factory.database.name.id")))
                .sendKeys(databaseName);
        driver.findElement(By.id(uiElementMapper.getElement("app.factory.database.password")))
                .sendKeys(passWord);
        driver.findElement(By.cssSelector(uiElementMapper.getElement("app.factory.database.advance.Checkbox")))
                .click();
        //going for the advanced option of creating the database
        //This thread will ease the driver to do the selection
        Thread.sleep(5000);
        //selecting the database environment
        new Select(driver.findElement(By.id(uiElementMapper.getElement("app.database.db.environment.id")))).
                selectByVisibleText(dbEnvironment);
        //selecting the user
        //thread waits for the selection
        Thread.sleep(1000);
        new Select(driver.findElement(By.id(uiElementMapper.getElement("app.database.db.environment.user")))).
                selectByVisibleText(user);
        //selecting the template
        //Thread waits for the selection
        Thread.sleep(1000);
        new Select(driver.findElement(By.id(uiElementMapper.getElement("app.database.db.environment.template")))).
                selectByVisibleText(template);
        driver.findElement(By.cssSelector(uiElementMapper.getElement("app.factory.database.submit.button"))).click();
        return new DatabaseConfigurationPage(driver);
    }
}