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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.appfactory.resources.ResourceOverviewPage;
import org.wso2.carbon.automation.test.api.selenium.login.LoginPage;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.NoSuchElementException;

public class AppManagementPage {

    private WebDriver driver;
    private UIElementMapper uiElementMapper;
    private static final Log log = LogFactory.getLog(LoginPage.class);

    public AppManagementPage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.

        if (!(driver.getCurrentUrl().contains("application.jag"))) {
            throw new IllegalStateException("this is not the Application Management Page");
        }
    }

    //this method is used to check the application details accuracy in the application Overview page.
    public boolean isAppDetailsAvailable(String repositoryType, String appOwner, String Description,
                                         String applicationType, String applicationKey)
            throws InterruptedException {
        //this  wait until overview page load
        Thread.sleep(5000);
        String repositoryTypeName = driver.findElement(By.id(uiElementMapper.getElement
                ("app.overview.page.repository.type.id"))).getText();
        String appOwnerName = driver.findElement(By.id(uiElementMapper.getElement
                ("app.overview.page.app.owner.id"))).getText().toUpperCase();
        String DescriptionOfApp = driver.findElement(By.id(uiElementMapper.getElement
                ("app.overview.page.app.description.id"))).getText();
        String applicationTypeOfApp = driver.findElement(By.id(uiElementMapper.getElement
                ("app.overview.page.app.type.id"))).getText();
        String applicationKeyOfApp = driver.findElement(By.xpath(uiElementMapper.getElement
                ("app.overview.page.app.key.xpath"))).getText();

        if (repositoryType.equals(repositoryTypeName) && appOwner.equals(appOwnerName) &&
                Description.equals(DescriptionOfApp) && applicationType.equals(applicationTypeOfApp) && applicationKey.
                equals(applicationKeyOfApp)) {

            log.info(repositoryType);
            log.info(repositoryTypeName);
            log.info("--------------------------------------------------");

            log.info(appOwner);
            log.info(appOwnerName);
            log.info("---------------------------------------------------");

            log.info(Description);
            log.info(DescriptionOfApp);
            log.info("---------------------------------------------------");

            log.info(applicationType);
            log.info(applicationTypeOfApp);
            log.info("----------------------------------------------------");

            log.info(applicationKey);
            log.info(applicationKeyOfApp);
            log.info("----------------------------------------------------");

            log.info("application details added are accurate in App Management page");
            return true;
        }

        log.info(repositoryType);
        log.info(repositoryTypeName);
        log.info("--------------------------------------------------");

        log.info(appOwner);
        log.info(appOwnerName);
        log.info("---------------------------------------------------");

        log.info(Description);
        log.info(DescriptionOfApp);
        log.info("---------------------------------------------------");

        log.info(applicationType);
        log.info(applicationTypeOfApp);
        log.info("----------------------------------------------------");

        log.info(applicationKey);
        log.info(applicationKeyOfApp);
        log.info("----------------------------------------------------");

        log.info("application details added are inaccurate in App Management page");
        return false;
    }

    public void editApplicationDetails(String editedString) throws InterruptedException {
        //only description could be edited by now later this method will give the functionality to
        //edit the rest of the values
        driver.findElement(By.id(uiElementMapper.getElement("app.overview.page.app.description.id"))).click();
        //this wait until overview page loads
        Thread.sleep(5000);
        driver.findElement(By.id(uiElementMapper.getElement("new.app.add.app.edit.Description"))).clear();
        driver.findElement(By.id(uiElementMapper.getElement("new.app.add.app.edit.Description")))
                .sendKeys(editedString);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("updateDescription()");
        log.info("Description is updated");
    }

    //this method use to check the Description editing of the Application
    public boolean isEdited(String editedText) throws InterruptedException {
        log.info("checking the edited text of the description text area");
        //this waits until overview page loads
        Thread.sleep(5000);
        String editedDescription = driver.findElement(By.id(uiElementMapper.getElement
                ("app.overview.page.app.description.id"))).getText();
        log.info("-------------------------------------");
        log.info(editedText);
        log.info(editedDescription);

        if (editedText.equals(editedDescription)) {
            log.info("Application Description edit is successful");
            return true;
        }

        log.info("Application Description edit is unsuccessful");
        return false;
    }

    public GovernancePage gotoGovernancePage() throws IOException {
        driver.findElement(By.id(uiElementMapper.getElement("app.navigate.Governance.page.link"))).click();
        return new GovernancePage(driver);
    }

    public IssuePage gotoIssuePage() throws IOException {
        driver.findElement(By.id(uiElementMapper.getElement("app.navigate.isue.page.link.id"))).click();
        return new IssuePage(driver);
    }

    //this method use to go to team page
    public TeamPage gotoTeamPage() throws IOException {
        driver.findElement(By.id(uiElementMapper.getElement("app.team.page.id"))).click();
        return new TeamPage(driver);
    }
    //this method use to go to team page
    public ResourceOverviewPage gotoResourceOverviewPage() throws IOException {
        driver.findElement(By.id(uiElementMapper.getElement("app.factory.db.admin.id"))).click();
        return new ResourceOverviewPage(driver);
    }

    //this method use to  go to repository and build page
    public RepositoryAndBuildPage gotoRepositoryAndBuildPage() throws IOException {
        driver.findElement(By.linkText(uiElementMapper.getElement("app.navigate.Link"))).click();
        return new RepositoryAndBuildPage(driver);
    }

    public boolean isTeamDetailsAvailable(String teamMember) {
        String memberDetails = driver.findElement(By.id(uiElementMapper.getElement
                ("app.overview.page.team.details.id"))).getText();
        if (memberDetails.contains(teamMember)) {
            log.info(teamMember + "Team Details Are Available");
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


    //this method is used to check the build details of the versions of desired application at the
    //overview Page
    public boolean isBuildDetailsAccurate(String buildVersion) throws InterruptedException {
        //this thread waits until deployment details loads to the overview Page
        Thread.sleep(30000);
        log.info("Verifying the Build Details of the application");
        String version = driver.findElement(By.xpath(uiElementMapper.getElement("app.trunk.overview.xpath")))
                .getText();
        if (buildVersion.equals(version))

        {
            log.info("Trunk of the Application");

            String buildStatus = driver.findElement(By.xpath(uiElementMapper.getElement
                    ("app.trunk.build.status.xpath"))).getText();
            log.info(buildStatus);

            if (buildStatus.equals("SUCCESSFUL")) {
                log.info("Trunk of the application  build successful");
                return true;
            }
            log.info("Trunk of the application build unsuccessful");
            return false;
        } else {
            String resourceXpath = "/html/body/div/div/article/section[3]/div/ul[";
            String resourceXpath2 = "]/li/p/strong";

            for (int i = 2; i < 10; i++) {
                String versionXpath = resourceXpath + i + resourceXpath2;
                String versionName = driver.findElement(By.xpath(versionXpath)).getText();
                log.info("val on app is -------> " + versionName);
                log.info("Correct is    -------> " + buildVersion);

                try {

                    if (buildVersion.equals(versionName)) {

                        String buildStatusXpath = "/html/body/div/div[2]/article/section[3]/div/ul[";
                        String buildStatusXpath2 = "]/li[4]/p/span/strong";
                        String xpathConstructForBuild = buildStatusXpath + i + buildStatusXpath2;
                        String buildStatus = driver.findElement(By.xpath(xpathConstructForBuild)).getText();
                        if (buildStatus.equals("SUCCESSFUL")) {
                            log.info("build status of" + "" + buildVersion + " is" + buildStatus);
                            return true;
                        } else {
                            return false;
                        }
                    }
                } catch (NoSuchElementException ex) {
                    log.info("Cannot Find the Uploaded Profile");
                }
            }
        }

        return false;
    }
}
