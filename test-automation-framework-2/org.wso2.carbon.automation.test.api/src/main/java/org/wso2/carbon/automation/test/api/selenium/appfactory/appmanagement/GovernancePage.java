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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

public class GovernancePage {

    private static final Log log = LogFactory.getLog(AppLogin.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public GovernancePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.

        if (!(driver.getCurrentUrl().contains("governance.jag"))) {
            // Alternatively, we could navigate to the login page, perhaps logging out first
            throw new IllegalStateException("This is not the Create Application page");
        }
    }

    public void signOut() {
        log.info("Ready to sign out from the system");
        driver.findElement(By.cssSelector(uiElementMapper.getElement
                ("app.factory.sign.out.email"))).click();
        driver.findElement(By.linkText(uiElementMapper.getElement
                ("app.factory.sing.out.text"))).click();

        log.info("log out from the app factory");
    }
    //this method is use to promote the life cycle
    public void promoteVersion(String version, String lifeCycleStage) throws InterruptedException {
        log.info("waiting to load list of versions");
        //wait until list of versions get load
        Thread.sleep(10000);
        if (lifeCycleStage.equals("Development")) {
            String resourceXpath = "/html/body/div/div[3]/article/section/div[2]/ul/li[";
            String resourceXpath2 = "]/ul/li/div/strong";
            for (int i = 2; i < 10; i++) {
                String versionXpath = resourceXpath + i + resourceXpath2;
                String versionName = driver.findElement(By.xpath(versionXpath)).getText();
                log.info("val on app is -------> " + versionName);
                log.info("Correct is    -------> " + version);

                try {

                    if (version.equals(versionName)) {
                        String tempDateId = versionName.replace('.', '_');
                        String dateId = "etaTo_" + tempDateId;
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        //get current date time with Date()
                        Date date = new Date();
                        String formattedDate = dateFormat.format(date);
                        driver.findElement(By.id((dateId))).sendKeys(formattedDate);
                        //save button xpath generator
                        String saveButtonXpath = "(//input[@value='Save'])[";
                        String saveButtonXpath2 = "]";
                        if (i == 2) {
                            driver.findElement(By.xpath(("(//input[@value='Save'])"))).click();
                        } else {
                            String saveButtonXpathGenerator = saveButtonXpath + i + saveButtonXpath2;
                            driver.findElement(By.xpath((saveButtonXpathGenerator))).click();
                        }
                        //this thread sleep is for the date save
                        Thread.sleep(5000);
                        //promote arrow Key Xpath Generator
                        String arrow = "//div[@id='whereItAllGoes']/ul/li[";
                        String arrow2 = "]/ul/li[4]/div/ul/li/button";
                        String arrowXpath = arrow + i + arrow2;
                        driver.findElement(By.xpath((arrowXpath))).click();
                        //this thread sleep is to until loading the code status
                        Thread.sleep(5000);
                        //checking the code status
                        if (!driver.findElement(By.xpath(uiElementMapper.getElement("code.completed.status")))
                                .isSelected()) {
                            driver.findElement(By.xpath(uiElementMapper.getElement("code.completed.status")))
                                    .click();
                        }
                        if (!driver.findElement(By.xpath(uiElementMapper.getElement("code.review.status")))
                                .isSelected()) {
                            driver.findElement(By.xpath(uiElementMapper.getElement("code.review.status")))
                                    .click();
                        }

                        if (!driver.findElement(By.xpath(uiElementMapper.getElement("design.review.status")))
                                .isSelected()) {
                            driver.findElement(By.xpath(uiElementMapper.getElement("design.review.status")))
                                    .click();
                        }
                        //promoting the code
                        String promote = "//button[@onclick=\"doGovernanceAction(this, 'Promote','Development', '";
                        String promote2 = version + "')\"]";
                        String promoteXpath = promote + promote2;
                        driver.findElement(By.xpath((promoteXpath))).click();
                        //this thread is to wait until application's promotion.
                        Thread.sleep(45000);
                        break;
                    }
                } catch (NoSuchElementException ex) {
                    log.info("Cannot find the Build version");
                }
            }
        } else if (lifeCycleStage.equals("testing")) {
            //checking the first element of the testing stage
            String versionName = driver.findElement(By.xpath
                    ("/html/body/div/div[3]/article/section/div[2]/ul[2]/li/ul/li/div/strong")).getText();
            if (version.equals(versionName)) {

                String tempDateId = versionName.replace('.', '_');
                String dateId = "etaTo_" + tempDateId;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //get current date time with Date()
                Date date = new Date();
                String formattedDate = dateFormat.format(date);
                driver.findElement(By.id((dateId))).sendKeys(formattedDate);
                //save button xpath generator
                Thread.sleep(5000);
                driver.findElement(By.xpath(("(//input[@value='Save'])[4]"))).click();
                Thread.sleep(10000);
                driver.findElement(By.xpath(("//div[@id='whereItAllGoes']/ul[2]/li/ul/li[4]/div/ul/" +
                        "li/button"))).click();
                Thread.sleep(5000);

                if (!driver.findElement(By.xpath(uiElementMapper.getElement("code.completed.status")))
                        .isSelected()) {
                    driver.findElement(By.xpath(uiElementMapper.getElement("code.completed.status")))
                            .click();
                }
                if (!driver.findElement(By.xpath(uiElementMapper.getElement("code.review.status")))
                        .isSelected()) {
                    driver.findElement(By.xpath(uiElementMapper.getElement("code.review.status")))
                            .click();
                }
                //promoting the code
                String promote = "//button[@onclick=\"doGovernanceAction(this, 'Promote','Testing', '";
                String promote2 = version + "')\"]";
                String promoteXpath = promote + promote2;
                driver.findElement(By.xpath((promoteXpath))).click();
                //this thread is to wait until application's promotion.
                Thread.sleep(30000);
            } else {

                String resourceXpath = "/html/body/div/div[3]/article/section/div[2]/ul[2]/li[";
                String resourceXpath2 = "]/ul/li/div/strong";
                for (int i = 2; i < 10; i++) {
                    String versionXpath = resourceXpath + i + resourceXpath2;
                    String versionName2 = driver.findElement(By.xpath(versionXpath)).getText();
                    log.info("val on app is -------> " + versionName);
                    log.info("Correct is    -------> " + version);

                    try {

                        if (version.equals(versionName2)) {
                            String tempDateId = versionName.replace('.', '_');
                            String dateId = "etaTo_" + tempDateId;
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            //get current date time with Date()
                            Date date = new Date();
                            String formattedDate = dateFormat.format(date);
                            driver.findElement(By.id((dateId))).sendKeys(formattedDate);
                            //save button xpath generator
                            String saveButtonXpath = "(//input[@value='Save'])[";
                            String saveButtonXpath2 = "]";
                            if (i == 2) {
                                driver.findElement(By.xpath(("(//input[@value='Save'])"))).click();
                            } else {
                                String saveButtonXpathGenerator = saveButtonXpath + i + saveButtonXpath2;
                                driver.findElement(By.xpath((saveButtonXpathGenerator))).click();
                            }
                            //this thread sleep is for the date save
                            Thread.sleep(5000);
                            //promote arrow Key Xpath Generator
                            String arrow = "//div[@id='whereItAllGoes']/ul/li[";
                            String arrow2 = "]/ul/li[4]/div/ul/li/button";
                            String arrowXpath = arrow + i + arrow2;
                            driver.findElement(By.xpath((arrowXpath))).click();
                            //this thread sleep is to until loading the code status
                            Thread.sleep(5000);
                            //checking the code status
                            if (!driver.findElement(By.xpath(uiElementMapper.getElement("code.completed.status")))
                                    .isSelected()) {
                                driver.findElement(By.xpath(uiElementMapper.getElement("code.completed.status")))
                                        .click();
                            }
                            if (!driver.findElement(By.xpath(uiElementMapper.getElement("code.review.status")))
                                    .isSelected()) {
                                driver.findElement(By.xpath(uiElementMapper.getElement("code.review.status")))
                                        .click();
                            }
                            //promoting the code
                            String promote = "//button[@onclick=\"doGovernanceAction(this, 'Promote','Testing', '";
                            String promote2 = version + "')\"]";
                            String promoteXpath = promote + promote2;
                            driver.findElement(By.xpath((promoteXpath))).click();
                            //this thread is to wait until application's promotion.
                            Thread.sleep(30000);
                            break;
                        }
                    } catch (NoSuchElementException ex) {
                        log.info("Cannot find the Build version");
                    }
                }
            }
        }
    }
}

