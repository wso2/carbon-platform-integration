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
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class DeleteDBPage {
    private static final Log log = LogFactory.getLog(ResourceOverviewPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public DeleteDBPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.

        if (!(driver.getCurrentUrl().contains("editdatabase.jag"))) {
            throw new IllegalStateException("This is not the new db user Deletion Page");
        }
    }

    public ResourceOverviewPage deleteDatabase() throws InterruptedException, IOException {
        log.info("@ the delete db user Page");
        driver.findElement(By.linkText(uiElementMapper.getElement("app.factory.delete.db"))).click();
        //waits until alert box appears
        Thread.sleep(5000);
        driver.findElement(By.linkText(uiElementMapper.getElement("app.factory.delete.Ok"))).click();
        //waits until the delete Process completion
        Thread.sleep(15000);
        return new ResourceOverviewPage(driver);
    }
}