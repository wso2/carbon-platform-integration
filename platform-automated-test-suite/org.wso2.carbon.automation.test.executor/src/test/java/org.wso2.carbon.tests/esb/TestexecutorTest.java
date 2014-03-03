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

package org.wso2.carbon.tests.esb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.TestNG;
import org.testng.annotations.BeforeSuite;
import org.testng.xml.XmlSuite;
import org.wso2.carbon.automation.test.executor.MasterTestSuite;
import org.wso2.carbon.automation.test.executor.utils.JarLoader;
import org.wso2.carbon.automation.test.executor.utils.TestSuiteFinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestexecutorTest extends MasterTestSuite {
    private static final Log log = LogFactory.getLog(TestexecutorTest.class);

    @BeforeSuite
    public void execute() {
        System.setProperty("server.list", "ESB");
        System.setProperty("framework.resource.location", resourcePath);
        System.setProperty("automation.property.file", automationPropertyPath);
        JarLoader jarLoader = new JarLoader();
        TestSuiteFinder testSuiteFinder = new TestSuiteFinder();
        for (File jarFile : jarLoader.getJarList()) {
            jarResourcepath = jarFile.getAbsolutePath();
            List<XmlSuite> suite = new ArrayList<XmlSuite>();
            suite.addAll(testSuiteFinder.initializeSuitesAndJarFile(jarResourcepath));
            TestNG testNG = superSuite("TestNG-DSS-Suite", suite);
            testNG.run();
        }
    }


    public static void main(String[] args) throws MojoFailureException {
        JarLoader loader = new JarLoader();
        try {
            loader.addURL(new File(jarResourcepath).toURL());
        } catch (IOException e) {
            log.error("Execution failed :" + e.getMessage());
        }
        new TestexecutorTest().execute();
    }
}
