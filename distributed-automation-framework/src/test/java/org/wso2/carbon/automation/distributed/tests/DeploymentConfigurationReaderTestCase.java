/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.automation.distributed.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.distributed.beans.Deployment;
import org.wso2.carbon.automation.distributed.commons.DeploymentConfigurationReader;
import org.wso2.carbon.automation.distributed.frameworkutils.FrameworkPathUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Unit test for Configuration Reader.
 */
public class DeploymentConfigurationReaderTestCase {

    HashMap<String, Deployment> deploymentHashMap = new HashMap<>();

    @BeforeClass
    public void init() throws IOException {
        System.setProperty(FrameworkPathUtil.SYSTEM_ARTIFACT_RESOURCE_LOCATION,
                           new File("src/test/resources/").getAbsolutePath() + File.separator);
        deploymentHashMap = new DeploymentConfigurationReader().getDeploymentHashMap();
    }

    @Test
    public void testConfigurationObject() {
        Assert.assertFalse(deploymentHashMap.entrySet().isEmpty());
    }

    @Test
    public void testConfigurationObject2() {
        Assert.assertFalse(deploymentHashMap.entrySet().isEmpty());
    }

}
