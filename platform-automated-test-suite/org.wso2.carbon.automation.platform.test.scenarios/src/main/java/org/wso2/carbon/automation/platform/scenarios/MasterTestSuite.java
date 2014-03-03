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

package org.wso2.carbon.automation.platform.scenarios;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.wso2.carbon.automation.core.PlatformExecutionManager;
import org.wso2.carbon.automation.core.PlatformPriorityManager;
import org.wso2.carbon.automation.core.PlatformReportManager;
import org.wso2.carbon.automation.core.PlatformSuiteManager;
import org.wso2.carbon.automation.core.PlatformTestManager;
import org.wso2.carbon.automation.core.utils.suiteutills.SuiteVariables;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MasterTestSuite {
    private static final Log log = LogFactory.getLog(MasterTestSuite.class);
    public static String automationPropertyPath = null;
    public static String automationSettingsPath = null;

    public MasterTestSuite() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL automationFileURL = classLoader.getResource("automation.properties");
        automationPropertyPath = automationFileURL.getPath();
        File resourceDir = new File(automationPropertyPath);
        automationSettingsPath = resourceDir.getParent();
    }

    public static String resourcePath = System.getProperty("system.test.resource.location");

    public TestNG superSuite(String SuiteName, List<SuiteVariables> suiteVariablesList) {
        XmlSuite suite = new XmlSuite();
        GenerateTestNgXml generateTestNgXml= new GenerateTestNgXml();
        suite.setName(SuiteName);
        suite.setVerbose(1);
        suite.setThreadCount(2);
        log.info("[TESTAUTOMATION]----" + SuiteName);
        for (SuiteVariables suiteVariables : suiteVariablesList) {

            XmlTest test = new XmlTest(suite);
            test.setName(suiteVariables.geTestName());
            test.setExcludedGroups(Arrays.asList(suiteVariables.getExcludeGrops()));
            XmlClass[] classes = new XmlClass[]{
                    new XmlClass(suiteVariables.getTestClass()),
            };
            test.setXmlClasses(Arrays.asList(classes));
        }
        TestNG tng = new TestNG();
        List<Class> listnerClasses = new ArrayList<Class>();
        listnerClasses.add(PlatformExecutionManager.class);
        listnerClasses.add(PlatformTestManager.class);
        listnerClasses.add(PlatformSuiteManager.class);
        listnerClasses.add(PlatformReportManager.class);
        listnerClasses.add(PlatformPriorityManager.class);
        tng.setListenerClasses(listnerClasses);
        tng.setDefaultSuiteName(SuiteName);
        tng.setXmlSuites(Arrays.asList(new XmlSuite[]{suite}));
        tng.setOutputDirectory("src/main/reports");
        generateTestNgXml.generateXml(suite.getName(),suite.toXml());
        return tng;
    }
}

