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
import org.wso2.carbon.automation.test.api.selenium.appfactory.home.AppLogin;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

public class RepositoryAndBuildPage {
    private static final Log log = LogFactory.getLog(AppLogin.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public RepositoryAndBuildPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.

        if (!(driver.getCurrentUrl().contains("reposBuilds.jag"))) {
            throw new IllegalStateException("This is not the Repository and Build page");
        }
    }

    //This method is used to create a branch from the trunk of the Application
    public void createBranchFromTrunk(String branchVersion) throws InterruptedException {
        log.info("Creating a Branch from the Trunk");
        driver.findElement(By.linkText(uiElementMapper.getElement("app.add.branch.link"))).click();
        driver.findElement(By.id(uiElementMapper.getElement("app.add.branch.version"))).sendKeys(branchVersion);
        driver.findElement(By.xpath(uiElementMapper.getElement("app.add.branch.button.xpath"))).click();
        //waits until Branch build and deploy
        Thread.sleep(15000);
        driver.navigate().refresh();
        log.info("Branch is created from the trunk");
    }

    //this method is used to create a branch from a branch version
    public void createBranchFromVersion(String branchID, String branchVersion)
            throws InterruptedException {
        log.info("Creating a Branch from the 1st branch");
        String tmp = branchID.replace('.', '_');
        String branch = "create_branch" + tmp;
        log.info("BranchID= " + branch);
        driver.findElement(By.xpath(uiElementMapper.getElement("app.add.second.branch.xpath"))).click();
        driver.findElement(By.id((branch))).sendKeys(branchVersion);
        driver.findElement(By.xpath(uiElementMapper.getElement("app.add.branch.two.button.xpath"))).click();
        //wait until branch build and deploy
        Thread.sleep(15000);
        driver.navigate().refresh();
        log.info("Branch is created from the 1st branch");
    }

    public void signOut() {
        log.info("Ready to sign out from the system");
        driver.findElement(By.cssSelector(uiElementMapper.getElement
                ("app.factory.sign.out.email"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement
                ("app.factory.sing.out.text"))).click();

        log.info("log out from the app factory");
    }
}
