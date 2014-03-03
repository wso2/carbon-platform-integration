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

import org.apache.maven.plugin.MojoFailureException;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;
import org.wso2.carbon.automation.test.executor.utils.JarLoader;
import org.wso2.carbon.automation.test.executor.utils.TestSuiteFinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Keep this class for debugging purpose, debugging test class it self is bit tricky.
 */
public class TestExecutor extends MasterTestSuite {
    static String path ="/home/dharshana/wso2Trunk/carbon-2012-7/carbon/platform/branches/4.0.0/platform-integration/clarity-tests/1.0.2/org.wso2.carbon.automation.test.executor/src/main/resources/";

    public void testset() throws MojoFailureException {
        System.setProperty("server.list", "AS,ESB");
        System.setProperty("framework.resource.location", "/home/dharshana/wso2Trunk/carbon-2012-7/carbon/platform/branches/4.0.0/platform-integration/" +
                                                          "clarity-tests/1.0.2/org.wso2.carbon.automation.test.repo/src/main/resources/");
        System.setProperty("automation.property.file", "/home/dharshana/wso2Trunk/carbon-2012-7/carbon/platform/branches/" +
                                                    "4.0.0/platform-integration/clarity-tests/1.0.2/org.wso2.carbon.automation.test.executor/src/main/resources/clarity.properties/");
        TestSuiteFinder testSuiteFinder = new TestSuiteFinder();
        List<XmlSuite> suite = new ArrayList<XmlSuite>();
        suite.addAll(testSuiteFinder.initializeSuitesAndJarFile(path + "org.wso2.carbon.esb.tests-4.5.0-tests.jar"));
        TestNG testNG = superSuite("test2222", suite);

        testNG.run();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args) throws MojoFailureException {
        JarLoader loader = new JarLoader();
        try {
            loader.addURL(new File(path+"org.wso2.carbon.esb.tests-4.5.0-tests.jar").toURL());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        new TestExecutor().testset();
    }
}

