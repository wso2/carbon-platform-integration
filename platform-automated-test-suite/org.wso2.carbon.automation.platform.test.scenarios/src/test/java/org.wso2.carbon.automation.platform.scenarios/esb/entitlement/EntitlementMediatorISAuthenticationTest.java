package org.wso2.carbon.automation.platform.scenarios.esb.entitlement;

import junit.framework.Assert;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.api.clients.identity.entitlement.EntitlementPolicyServiceClient;
import org.wso2.carbon.automation.api.clients.security.SecurityAdminServiceClient;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.UserInfo;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentBuilder;
import org.wso2.carbon.automation.core.utils.environmentutils.EnvironmentVariables;
import org.wso2.carbon.automation.utils.axis2client.SecureAxisServiceClient;
import org.wso2.carbon.automation.utils.esb.ESBBaseTest;

import java.io.File;

/**
 * This test for Entitlement Mediator WSO2IS authentication.
 * https://wso2.org/jira/browse/ESBJAVA-1915
 */
public class EntitlementMediatorISAuthenticationTest {

    private static final Log log = LogFactory.getLog(EntitlementMediatorISAuthenticationTest.class);

    private EnvironmentVariables environmentESB;
    private EnvironmentVariables environmentIS;
    int userId =2;
    private ESBBaseTest esbSBaseTest;
    private UserInfo userInfo ;
    SecurityAdminServiceClient securityAdminServiceClient;
    EntitlementPolicyServiceClient entitlementPolicyServiceClient;

    @BeforeClass(alwaysRun = true)
    public void testInitialize() throws Exception {
        EnvironmentBuilder builder = new EnvironmentBuilder().is(userId);
        esbSBaseTest = new ESBBaseTest(userId);
        environmentIS = builder.build().getIs();
        environmentESB = esbSBaseTest.esbServer;
        String synapseConfigPath = "artifacts" + File.separator + "ESB" + File.separator +
                "entitlementMediatorConfig" + File.separator + "entitlementMediatorSynapse.xml";
        esbSBaseTest.loadESBConfigurationFromClasspath(synapseConfigPath);
        esbSBaseTest.applySecurity("EchoProxy",1, esbSBaseTest.getUserRole(userId+""));
        userInfo = esbSBaseTest.userInfo;
        entitlementPolicyServiceClient = new EntitlementPolicyServiceClient(environmentIS.getBackEndUrl(),
                environmentIS.getSessionCookie());
        String policyFilePath = ProductConstant.SYSTEM_TEST_RESOURCE_LOCATION + "artifacts" + File.separator +
                "IS" + File.separator + "entitlement" + File.separator + "policies" + File.separator + "policy1.xml";
        entitlementPolicyServiceClient.addPolicies(new File(policyFilePath));
    }

    @Test(groups = {"wso2.as"}, description = "sending secure request")
    public void testEntitlementMediator() throws Exception {
        SecureAxisServiceClient serviceClient = new SecureAxisServiceClient();
        OMElement resOmElement = serviceClient.sendReceive(userInfo.getUserName(), userInfo.getPassword(),
                esbSBaseTest.getProxyServiceSecuredURL("EchoProxy"), "echoString",
                AXIOMUtil.stringToOM("<p:echoString xmlns:p=\"http://echo.services.core.carbon.wso2.org\">\n" +
                "      <in>EntitlementTest</in>\n" +
                "   </p:echoString>"), 1);
        Assert.assertTrue("Response message content mismatched", resOmElement.toString().contains("EntitlementTest"));
    }
    @AfterClass(alwaysRun = true)
    public void addESBConfigurations() throws Exception {
          esbSBaseTest.cleanup();
    }
}
