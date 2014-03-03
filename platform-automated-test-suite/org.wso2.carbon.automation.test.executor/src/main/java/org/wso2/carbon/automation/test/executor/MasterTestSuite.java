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

package org.wso2.carbon.automation.test.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;
import org.wso2.carbon.automation.core.PlatformExecutionManager;
import org.wso2.carbon.automation.core.PlatformPriorityManager;
import org.wso2.carbon.automation.core.PlatformReportManager;
import org.wso2.carbon.automation.core.PlatformSuiteManager;
import org.wso2.carbon.automation.core.PlatformTestManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterTestSuite {

    private static final Log log = LogFactory.getLog(MasterTestSuite.class);
    private Map<String, String> parameters = new HashMap<String, String>();
    public static String automationPropertyPath = null;
    public static String jarResourcepath = null;
    public static String resourcePath = System.getProperty("system.test.resource.location");

    public MasterTestSuite() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL automationPropertyResource = classLoader.getResource("automation.properties");
        automationPropertyPath = automationPropertyResource.getPath();
    }

    public TestNG superSuite(String SuiteName, List<XmlSuite> suite) {

        TestNG tng = new TestNG();
        List<Class> listnerClasses = new ArrayList<Class>();
        listnerClasses.add(PlatformExecutionManager.class);
        listnerClasses.add(PlatformTestManager.class);
        listnerClasses.add(PlatformSuiteManager.class);
        listnerClasses.add(PlatformReportManager.class);
        listnerClasses.add(PlatformPriorityManager.class);
        tng.setListenerClasses(listnerClasses);
        tng.setDefaultSuiteName(SuiteName);
        tng.setXmlSuites(suite);
        tng.setOutputDirectory("src/main/reports");
        return tng;
    }
}
