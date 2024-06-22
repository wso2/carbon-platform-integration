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

package org.wso2.carbon.automation.engine.test.context;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;

import javax.xml.xpath.XPathExpressionException;

public class AutomationContextTestCase {
    AutomationContext context;

    @Factory(dataProvider = "automationContextOverride")
    public AutomationContextTestCase(AutomationContext automationContext) {
        this.context = automationContext;
    }

    @BeforeClass(alwaysRun = true)
    public void initTest() throws XPathExpressionException {

    }


    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testInstance() throws XPathExpressionException {
        Assert.assertFalse(context.getInstance().getHosts().isEmpty());
    }

    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testDefaultInstance() throws XPathExpressionException {
        Assert.assertFalse(context.getInstance().getHosts().isEmpty());
    }

    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testGetSuperTenant() throws XPathExpressionException {

        Assert.assertEquals(context.getSuperTenant().getTenantAdmin().getUserName(), "admin");
    }

    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testGetTenant() throws XPathExpressionException {
        Assert.assertTrue(context.getContextTenant().getTenantAdmin().getUserName().contains("admin"));
    }

    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testGetContestUrls() throws XPathExpressionException {
        Assert.assertTrue(context.getContextUrls().getBackEndUrl().contains("http"));
    }

    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testGetConfigValue() throws XPathExpressionException {
        Assert.assertTrue(context.getConfigurationValue("//datasources/datasource[@name='dataService']/password/text()").contains("wso2carbon"));
    }
    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testGetConfigNode() throws XPathExpressionException {

        Assert.assertEquals(
                context.getConfigurationNode("//datasources/datasource[@name='dataService']").getChildNodes().item(2)
                        .getNodeName(), "password");
    }
    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testGetCofigNodeList() throws XPathExpressionException {

        Assert.assertEquals(context.getConfigurationNodeList("//datasources/datasource").getLength(), 2);
    }
    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testGetDefaultInstance() throws XPathExpressionException {
        Assert.assertTrue(context.getDefaultInstance().getName().contains("00"));
    }
    @Test(groups = "context.unit.test", description = "Upload aar service and verify deployment")
    public void testGetIsClustered() throws XPathExpressionException {
        Assert.assertFalse(context.getProductGroup().isClusterEnabled());
    }


    @DataProvider
    private static AutomationContext[][] automationContextOverride() throws XPathExpressionException {
        return new AutomationContext[][]{
                new AutomationContext[]{new AutomationContext()},
                new AutomationContext[]{new AutomationContext("ESB", TestUserMode.SUPER_TENANT_USER)},
                new AutomationContext[]{new AutomationContext("ESB", TestUserMode.SUPER_TENANT_ADMIN)},
                new AutomationContext[]{new AutomationContext("ESB", TestUserMode.TENANT_USER)},
                new AutomationContext[]{new AutomationContext("ESB", TestUserMode.TENANT_ADMIN)},
                new AutomationContext[]{new AutomationContext("ESB","esbm001",TestUserMode.SUPER_TENANT_USER)},
                new AutomationContext[]{new AutomationContext("ESB","esbm001",TestUserMode.SUPER_TENANT_ADMIN)},
                new AutomationContext[]{new AutomationContext("ESB","esbm001",TestUserMode.TENANT_USER)},
                new AutomationContext[]{new AutomationContext("ESB","esbm001",TestUserMode.TENANT_ADMIN)},
                new AutomationContext[]{new AutomationContext("ESB","esbm001","wso2","user1")},
        };
    }

}
