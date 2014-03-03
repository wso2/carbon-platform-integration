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
import org.wso2.carbon.automation.test.api.selenium.apimanager.store.ApiStorePage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.Set;

public class ResourceOverviewPage {
    private WebDriver driver;
    private UIElementMapper uiElementMapper;
    private static final Log log = LogFactory.getLog(ResourceOverviewPage.class);

    public ResourceOverviewPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.

        if (!(driver.getCurrentUrl().contains("configureenvironment.jag"))) {
            throw new IllegalStateException("This is not the Resources Overview page");
        }
    }

    public DatabaseConfigurationPage gotoDataBaseConfigPage() throws IOException {
        driver.findElement(By.linkText(uiElementMapper.getElement("app.factory.database.configure.page.link"))).click();
        return new DatabaseConfigurationPage(driver);
    }

    public DataSourcePage gotoDataSourcePage() throws IOException {
        driver.findElement(By.linkText(uiElementMapper.getElement("app.factory.data.source.page.link"))).click();
        return new DataSourcePage(driver);
    }

    public EndPointAndRegistryPage gotoEndpointAndRegistryPage() throws IOException {
        driver.findElement(By.linkText(uiElementMapper.getElement("app.factory.registry.page.link"))).click();
        return new EndPointAndRegistryPage(driver);
    }


    public boolean isDataSourceAvailable(String dataSourceName) throws IOException {
        String dataSource = driver.findElement(By.linkText((dataSourceName))).getText();
        if (dataSourceName.equals(dataSource)) {
            log.info("Added Data Source Available");
            return true;
        }
        return false;
    }

    public boolean isApiDetailsAvailable(String value) throws InterruptedException {
        //This Threads waits until application Details loads to the Overview Page
        Thread.sleep(20000);
        log.info("waiting until application Resource Details loads to the page");
        String apiPanelText = driver.findElement(By.xpath(uiElementMapper.getElement
                ("app.factory.api.panel.text"))).getText();
        log.info("-------------------------------------------------------");
        log.info(value);
        log.info(apiPanelText);
        log.info("-------------------------------------------------------");
        if (value.equals(apiPanelText)) {
            log.info("API  pane is empty");
            return true;
        }
        return false;
    }

    public void signOut() {
        log.info("Ready to sign out from the system");
        driver.findElement(By.cssSelector(uiElementMapper.getElement
                ("app.factory.sign.out.email"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement
                ("app.factory.sing.out.text"))).click();

        log.info("log out from the app factory");
    }

    public ApiStorePage apiManagerStore() throws InterruptedException, IOException {
        log.info("subscribing");
        driver.findElement(By.linkText(uiElementMapper.getElement
                ("app.factory.subscribe"))).click();
        //this thread waits until new tab open
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
        log.info("shifted to the api manager");
        return new ApiStorePage(driver);
    }

    public ApiManagerHomePage gotoApiManagerPage() throws IOException, InterruptedException {
        driver.findElement(By.linkText(uiElementMapper.getElement
                ("app.api.page.link.text"))).click();
        //Waits until APi Page gets Load
        Thread.sleep(45000);
        return new ApiManagerHomePage(driver);
    }
}