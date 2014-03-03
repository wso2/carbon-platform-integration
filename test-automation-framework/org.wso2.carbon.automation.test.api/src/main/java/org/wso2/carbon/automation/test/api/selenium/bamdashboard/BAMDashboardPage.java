/*
 * Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.automation.test.api.selenium.bamdashboard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.automation.test.api.selenium.util.UIElementMapper;

import java.io.IOException;

/**
 * BAM dashboard page class - contains methods to test BAM dashboard page
 */
public class BAMDashboardPage {
	private static final Log log = LogFactory.getLog(BAMDashboardPage.class);
	private UIElementMapper uiElementMapper;
	private WebDriver driver;

	public BAMDashboardPage(WebDriver driver) throws IOException {
		this.driver = driver;
		this.uiElementMapper = UIElementMapper.getInstance();
		// Check whether we are on the correct page.
		log.info("BAM dashboard page");

		// click main tab
		driver.findElement(By.id(uiElementMapper.getElement("carbon.Main.tab")))
				.click();

		if (!driver
				.findElement(
						By.xpath(uiElementMapper
								.getElement("bam.dashboard.tab.id"))).getText()
				.contains("BAM Dashboards")) {
			throw new IllegalStateException("This is not the correct home Page");
		}
	}

	public void testPageLoadFail() throws IOException, InterruptedException {
		// test should fail if page load fails
		driver.findElement(
				By.xpath(uiElementMapper.getElement("bam.dashboard.tab.id")))
				.click();

		for (int second = 0;; second++) {
			if (second >= 60)
				break;

			driver.navigate().refresh();
			String pageSource = driver.getPageSource();
			if (null != pageSource && pageSource.contains("Sign In")) {
				break;
			}

			Thread.sleep(500);
		}

		try {
			if (!driver
					.findElement(
							By.xpath(uiElementMapper
									.getElement("bam.dashboard.signin.xpath")))
					.getText().contains("Sign In")) {
				throw new IOException("Page loading failed");
			}
		} catch (NoSuchElementException e) {
			throw new IOException("Page loading failed");
		}
	}
}
