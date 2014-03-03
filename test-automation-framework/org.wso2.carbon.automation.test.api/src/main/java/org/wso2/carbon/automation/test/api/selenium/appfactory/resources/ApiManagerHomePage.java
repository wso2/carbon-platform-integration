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
import org.wso2.carbon.automation.test.api.selenium.apimanager.subscription.SubscriptionPage;
import org.wso2.carbon.automation.test.api.selenium.appfactory.appmanagement.AppFactoryDataHolder;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApiManagerHomePage {

    private static final Log log = LogFactory.getLog(ResourceOverviewPage.class);
    private WebDriver driver;
    private UIElementMapper uiElementMapper;

    public ApiManagerHomePage(WebDriver driver) throws IOException {
        this.driver = driver;
        this.uiElementMapper = UIElementMapper.getInstance();
        // Check that we're on the right page.

        if (!(driver.getCurrentUrl().contains("apimanager.jag"))) {
            throw new IllegalStateException("This is not the Api Manager Page");
        }
    }


    public SubscriptionPage gotoApiManageSubscriptionPage() throws IOException, InterruptedException {
        //this Thread waits until APi Store loads
        Thread.sleep(30000);
        try {
            Set handles = driver.getWindowHandles();
            String current = driver.getWindowHandle();
            handles.remove(current);
            String newTab = (String) handles.iterator().next();
            driver.switchTo().window(newTab);
        } catch (Exception e) {
            log.info(e.getMessage());
        }

        log.info("shifting to the Api Store");

        return new SubscriptionPage(driver);
    }

    //Checking the values.
    public boolean valueCheck() throws IOException {
        String apiPropertyValues = driver.findElement(By.id(uiElementMapper.getElement
                ("app.api.page.resource.list.id"))).getText();
        log.info("*********************property values in Api page*******************************");
        log.info(apiPropertyValues);
        log.info("*******************************************************************************");
        String sandBoxAndProductionDetails = AppFactoryDataHolder.getSandboxAndProductionDetails();
        log.info("========================property values in api manager=========================");
        log.info("APi Store Key Value String");
        log.info(sandBoxAndProductionDetails);
        log.info("===============================================================================");
        //this set of strings declared in order to truncate headings in the string
        String oldStr = apiPropertyValues;
        String newStr;
        newStr = oldStr.replace("Key\n" +
                "Value\n" +
                "prodKey", "");
        String newStr2 = newStr.replace("prodConsumerKey", "");
        String newStr3 = newStr2.replace("prodConsumerSecret", "");
        String newStr4 = newStr3.replace("sandboxKey", "");
        String newStr5 = newStr4.replace("sandboxConsumerKey", "");
        String newStr6 = newStr5.replace("sandboxConsumerSecret", "");
        log.info(oldStr);
        log.info("-----------------------------------------------------------");
        String newString7 = newStr6.replace("\n", "");
        log.info(newString7);
        //truncating the headings of api manager sandbox and production key values
        String new1 = sandBoxAndProductionDetails.replace("Keys Production Hide Keys Access Token", "");
        String new2 = new1.replace("Re-generate Consumer Key", "");
        String new3 = new2.replace("Consumer Secret", "");
        String new4 = new3.replace("Allowed Domains: ALL Edit Sandbox Hide Keys Access Token", "");
        String newApiValue5 = new4.replaceAll("\\s", "");
        log.info(newApiValue5);

        //storing string elements as lists and checking the sequence
        final Set<String[]> s = new HashSet<String[]>();
        final Set<List<String>> s2 = new HashSet<List<String>>();

        s.add(new String[]{newString7, newApiValue5});
        s2.add(Arrays.asList(new String[]{newString7, newApiValue5}));

        if (s2.contains(Arrays.asList(new String[]{newString7, newApiValue5}))) {
            log.info("values are matching in api manager and AppFactory ");
            log.info("Test Case passes ");
            return true;
        }
        return false;
    }
}
