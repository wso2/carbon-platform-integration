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

public class TeamManagementPage {
    private static final Log log = LogFactory.getLog(AppLogin.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public TeamManagementPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.
        if (!(driver.getCurrentUrl().contains("user-invite.jag"))) {
            // Alternatively, we could navigate to the login page, perhaps logging out first
            throw new IllegalStateException("This is not the User Management page");
        }
    }

    //this method is used to add new member to the AppFactory
    public TeamPage addMemberToTeam(String userEmail, String member)
            throws InterruptedException, IOException {
        driver.findElement(By.id(uiElementMapper.getElement("app.add.member.name"))).sendKeys(userEmail);
        driver.findElement(By.id(uiElementMapper.getElement("app.add.add.to.list.button"))).click();
        //this wait until email added to the system
        Thread.sleep(5000);
        String memberXpath = "(//input[@data-role='" + member + "'])[last()]";
        driver.findElement(By.xpath((memberXpath))).click();
        driver.findElement(By.id(uiElementMapper.getElement("app.invite.users"))).click();
        //this is to wait until team page loads
        Thread.sleep(15000);
        log.info("Loading the team Page");
        return new TeamPage(driver);
    }
}
